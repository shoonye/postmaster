package shoonye.pm.spec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.InputStreamSource;

import shoonye.dbmvc.bean.ActionResult;
import shoonye.util.CollectionUtil;
import shoonye.util.bean.Duple;

public class MailData extends ActionResult{
    //Duple key ==> email Id, value ==> Display Name
    private Duple<String, String> from;
	private Set<Duple<String, String>> to;
	private String subject;
	protected Map<String, InputStreamSource> attachments;
	private boolean singleMail = false;
	private String replyTo;
	
    public Set<Duple<String, String>> getTo() {
        return to;
    }
    
    public void setTo(Set<Duple<String, String>> to) {
        this.to = to;
    }
    
    public void setTo(String email, String name){
        to = new HashSet<Duple<String,String>>(1);
        to.add(new Duple<String, String>(email, name));
    }
    
    public void addTo(String email, String name){
        if(to==null){
            to = new HashSet<Duple<String,String>>();
        }     
        to.add(new Duple<String, String>(email, name));
    }
    
    public void setFrom(String email, String name){
        from = new Duple<String, String>(email, name);
    }
    
    public boolean isSingleMail() {
        return singleMail;
    }
    
    public void setSingleMail(boolean singleMail) {
        this.singleMail = singleMail;
    }
	
	public MailData subject(String subject) {
		this.subject = subject;
		return this;
	}
	
    public Duple<String, String> getFrom() {
        return from;
    }

    
    public void setFrom(Duple<String, String> from) {
        this.from = from;
    }

    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, InputStreamSource> getAttachments() {
		return attachments;
	}
	
    public void setAttachments(Map<String, InputStreamSource> attachments) {
		this.attachments = attachments;
	}
	
    public void addAttachment(String name, InputStreamSource attachment){
		if(attachments==null) attachments = new HashMap<String, InputStreamSource>();
		attachments.put(name,attachment);
	}
    
    
    public String getReplyTo() {
        return replyTo;
    }

    
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Set<String> getToEmails(){
        if(CollectionUtil.isBlank(to)) return null;
        HashSet<String> emails = new HashSet<String>(to.size());
        for(Duple<String, String> email : to){
            emails.add(email.getKey());
        }
        return emails;
    }
}
