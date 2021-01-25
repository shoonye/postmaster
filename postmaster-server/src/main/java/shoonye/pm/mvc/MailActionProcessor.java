package shoonye.pm.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import shoonye.dbmvc.ActionProcessor;
import shoonye.dbmvc.ViewProcessor;
import shoonye.dbmvc.dao.ActionHandlerConfigDao;
import shoonye.dbmvc.dao.ViewGroupDao;
import shoonye.dbmvc.entity.ActionHandlerConfig;
import shoonye.dbmvc.entity.ViewGroup;
import shoonye.event.spec.EventData;
import shoonye.pm.bean.MailBody;
import shoonye.pm.bean.MailMessage;
import shoonye.pm.spec.AbstractMailHandler;
import shoonye.pm.spec.MailData;
import shoonye.util.CollectionUtil;
import shoonye.util.StringUtil;
import shoonye.util.bean.Duple;
import shoonye.util.ec.ExecutionContext;


@Component("mailActionProcessor")
public class MailActionProcessor implements ActionProcessor<List<MailMessage>, EventData> {
	static final Logger logger = LoggerFactory.getLogger(MailActionProcessor.class);
	
	@Autowired private ActionHandlerConfigDao actionHandlerConfigDao;
	@Autowired private ViewGroupDao viewGroupDao;
	@Autowired private ViewProcessor<MailBody> mailViewProcessor;
	@Autowired private ExecutionContext executionContext;
	
	@Override
	public List<MailMessage> process(String actionName, EventData input) {
		ViewGroup view = null;
		MailData mailData=null;
		try {
			mailData = executeAction(actionName, input);
			
			if(mailData==null) return null;
			if(CollectionUtil.isBlank(mailData.getTo())) return null;
			
			view = viewGroupDao.findByKey(mailData.getKey());
			while(mailData.hasNext() && view==null){
				view = viewGroupDao.findByKey(mailData.nextKey());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} 
		if(StringUtil.isBlank(mailData.getKey())){
		    mailData.setKey(actionName);
		}
		
		Map<String,Object> data = getGlobalData();
		if (mailData!=null && mailData.getData()!=null) data.putAll(mailData.getData());
		
		List<MailMessage> messages;
		if(mailData.isSingleMail()){
		    messages = new ArrayList<MailMessage>(1);
		    MailBody mailBody =  mailViewProcessor.process(view, data);
		    
	        MailMessage message = new MailMessage(mailData);
	        message.setMailBody(mailBody);
	        
	        messages.add(message);
		}else{
		    messages = new ArrayList<MailMessage>(mailData.getTo().size()); 
		    //TODO optimize
		    for(Duple<String, String> to : mailData.getTo()){
		        Set<String> toEmail = new HashSet<String>(1);
                toEmail.add(to.getKey());
                
		        String displayName = to.getValue()==null?"": to.getValue();
                data.put("user_name", displayName);
		        MailBody mailBody =  mailViewProcessor.process(view, data);
		        
	            MailMessage message = new MailMessage(mailData);        
	            message.setTo(toEmail);
	            message.setMailBody(mailBody);
	            
	            messages.add(message);
		    }
		}	
		return messages;
	}

	
	private Map<String, Object> getGlobalData() {
		return new HashMap<String, Object>();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private MailData executeAction(String actionName, EventData data) throws Exception{
		
		ActionHandlerConfig hc = actionHandlerConfigDao.findByKey(actionName);
		if(hc==null){
			throw new Exception(" Could not find action for " + actionName);
		}		
		
		logger.debug("Input params {} ",data.toString());
		
		AbstractMailHandler handler;
		try {
			handler = (AbstractMailHandler)hc.createHandler();
		} catch (Exception e) {
			logger.error("Failed creation of Action Handler : {} Bean Details : {}, {} ", hc.getKey(),hc.getHandlerFQCN(), hc.getHandlerSBN());
			throw e;
		}
		MailData result = (MailData)handler.execute(data);
		return result;
	}
}
