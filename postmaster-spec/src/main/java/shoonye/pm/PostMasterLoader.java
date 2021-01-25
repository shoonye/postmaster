package shoonye.pm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import shoonye.dbmvc.dao.ActionHandlerConfigDao;
import shoonye.dbmvc.dao.ViewGroupDao;
import shoonye.dbmvc.dao.ViewTemplateDao;
import shoonye.dbmvc.entity.ActionHandlerConfig;
import shoonye.dbmvc.entity.TemplateLanguage;
import shoonye.dbmvc.entity.ViewGroup;
import shoonye.dbmvc.entity.ViewTemplate;
import shoonye.pm.dao.EventMailDao;
import shoonye.pm.entity.EventMail;
import shoonye.util.CollectionUtil;

@Component("postMasterLoader")
public class PostMasterLoader {
	protected String emailFolder = "/emails/";
    @Autowired private ActionHandlerConfigDao actionHandlerConfigDao;
	@Autowired private EventMailDao eventMailDao;
	@Autowired private ViewGroupDao viewGroupDao;
	@Autowired private ViewTemplateDao viewTemplateDao;
	
	@Transactional
	public void loadEntry(EmailEntry emailEntry) throws IOException{
		
		
		EventMail eventMail = new EventMail();
		String mailKey = emailEntry.getMailKey();
        eventMail.setProcessorKey(mailKey);
		eventMail.setEventName(emailEntry.getEventName());
		eventMail.setCreatedTime(new DateTime());
		eventMail.setCreatedBy("post-master-loader");
		eventMailDao.saveOrUpdate(eventMail);
		
		ActionHandlerConfig ahc =actionHandlerConfigDao.findByKey(mailKey);
		if(ahc==null){
			ahc = new ActionHandlerConfig();
			ahc.setHandlerFQCN(emailEntry.hanlerClassFQCN());
			ahc.setKey(mailKey);
			ahc.setName(mailKey);
			ahc.setInputFQCN(emailEntry.dataClassFQCN());
			ahc.setCreatedTime(new DateTime());
			ahc.setHandlerSBN(emailEntry.getSpringBeanName());
			ahc.setCreatedBy("post-master-loader");
			actionHandlerConfigDao.saveOrUpdate(ahc);
		}
		if(CollectionUtil.isBlank(emailEntry.getViewKeys())){
		    //If view list is empty, default view key to mail key
		    createViewGroup(mailKey);        
		}else{
		    for(String viewKey: emailEntry.getViewKeys()){
		        createViewGroup(viewKey);
		    }
		}
		
	}

    private void createViewGroup(String viewKey) throws IOException {
        ViewGroup viewGroup = viewGroupDao.findByKey(viewKey);
        if(viewGroup!=null) return; //can't create two views with same key
        
        String ptFileName = viewKey+".fmt";
        String htmlFileName = viewKey+"_HTML.fmt";
        
        ViewTemplate plainTextTemplate = new ViewTemplate();
        plainTextTemplate.setKey(viewKey);
        plainTextTemplate.setName(viewKey);
        plainTextTemplate.setBody(fileContent(ptFileName));
        plainTextTemplate.setLanguage(TemplateLanguage.FREEMARKER);
        plainTextTemplate.setCreatedTime(new DateTime());
        plainTextTemplate.setCreatedBy("post-master-loader");
        plainTextTemplate = viewTemplateDao.saveOrUpdate(plainTextTemplate);
        
        ViewTemplate htmlTemplate = new ViewTemplate();
        String name_html = viewKey+"_HTML";
        htmlTemplate.setKey(name_html);
        htmlTemplate.setName(name_html);
        htmlTemplate.setBody(fileContent(htmlFileName));
        htmlTemplate.setLanguage(TemplateLanguage.FREEMARKER);
        htmlTemplate.setCreatedTime(new DateTime());
        htmlTemplate.setCreatedBy("post-master-loader");
        htmlTemplate = viewTemplateDao.saveOrUpdate(htmlTemplate);
        
        viewGroup = new ViewGroup();
        viewGroup.setKey(viewKey);
        viewGroup.setName(viewKey);
        viewGroup.addViewTemplate(plainTextTemplate);
        viewGroup.addViewTemplate(htmlTemplate);
        viewGroup.setCreatedTime(new DateTime());
        viewGroup.setCreatedBy("post-master-loader");
        viewGroup= viewGroupDao.saveOrUpdate(viewGroup);
    }
	
    @Transactional
	public void removeEntry(String eventName, String processorKey){
	    EventMail mail = eventMailDao.findByEventNameAndProcessorKey(eventName, processorKey);
        eventMailDao.delete(mail);
	}
	
	private String fileContent(String flieName) throws IOException {
		InputStream is = getClass().getResourceAsStream(emailFolder + flieName);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int readCh = is.read();
		while (readCh != -1) {
			bout.write(readCh);
			readCh = is.read();
		}
		return new String(bout.toByteArray());
	}
	
	@Transactional
	public void deleteAll(){
	    actionHandlerConfigDao.deleteAll();
        viewGroupDao.deleteAll();
        viewTemplateDao.deleteAll();
        eventMailDao.deleteAll();
	}
}
