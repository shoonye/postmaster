package shoonye.pm.spec;

import shoonye.dbmvc.bean.ActionHandler;
import shoonye.dbmvc.bean.ActionResultType;

public abstract class AbstractMailHandler<I extends Object> implements ActionHandler<MailData,I> {
	
	protected MailData createMaildata(String key){
		MailData data = new MailData();
		data.setType(ActionResultType.VIEW);
		data.setKey(key);
		return data;
	}

}
