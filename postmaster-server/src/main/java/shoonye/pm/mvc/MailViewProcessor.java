package shoonye.pm.mvc;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import shoonye.dbmvc.ViewProcessor;
import shoonye.dbmvc.entity.ViewGroup;
import shoonye.dbmvc.entity.ViewTemplate;
import shoonye.dbmvc.parser.TemplateParser;
import shoonye.dbmvc.parser.TemplateParserRegistry;
import shoonye.pm.bean.MailBody;

@Component("mailViewProcessor")
public class MailViewProcessor implements ViewProcessor<MailBody> {
	static final Logger logger = LoggerFactory.getLogger(MailViewProcessor.class);
	
	@Override
	public MailBody process(ViewGroup viewLayout, Map<String, Object> data) {	
		if(viewLayout==null) return null;
		MailBody mailBody = new MailBody();
		for(ViewTemplate template: viewLayout.getViewTemplates()){
			String view = null;
			try{
				TemplateParser parser = TemplateParserRegistry.getParser(template.getLanguage());
				view = parser.parse(template, data);
				if(template.getKey().contains("HTML")){
					mailBody.setHtml(view);
				}else{
					mailBody.setPlainText(view);
				}
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}
		return mailBody;
	}

}
