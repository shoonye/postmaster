package shoonye.pm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shoonye.pm.spec.AbstractMailHandler;

public class EmailEntry {
	private String eventName;
	private String mailKey;
	private Class<? extends AbstractMailHandler<?>> handlerClass;
	private String springBeanName;
	private Class<?> dataClass;
	private List<String> viewKeys;
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getMailKey() {
		return mailKey;
	}
	public void setMailKey(String mailKey) {
		this.mailKey = mailKey;
	}
	public Class<? extends AbstractMailHandler<?>> getHandlerClass() {
		return handlerClass;
	}
	public void setHandlerClass(Class<? extends AbstractMailHandler<?>> handlerClass) {
		this.handlerClass = handlerClass;
	}
	public String getSpringBeanName() {
		return springBeanName;
	}
	public void setSpringBeanName(String springBeanName) {
		this.springBeanName = springBeanName;
	}
	public Class<?> getDataClass() {
		return dataClass;
	}
	public void setDataClass(Class<?> dataClass) {
		this.dataClass = dataClass;
	}
	
	public String hanlerClassFQCN(){
		return this.handlerClass.getName();
	}
	
	public String dataClassFQCN(){
		return this.dataClass.getName();
	}
    
    public List<String> getViewKeys() {
        return viewKeys;
    }
    
    public void setViewKeys(List<String> viewKeys) {
        this.viewKeys = viewKeys;
    }
    
    public void addViewKeys(String... viewKey){
        if(viewKey==null) return;
        if(this.viewKeys==null) this.viewKeys = new ArrayList<String>(viewKey.length);
        viewKeys.addAll(Arrays.asList(viewKey));
    }

}
