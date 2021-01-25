package shoonye.pm.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.InputStreamSource;

import shoonye.pm.spec.MailData;
import shoonye.util.StringUtil;

public class MailMessage {
	private Set<String> to;
	private String from;
	private String subject;
	private String personal;
	private MailBody mailBody;
	private String replyTo;
	protected Map<String, InputStreamSource> attachments;
	
	public MailMessage(MailData data){
		this.to = data.getToEmails();
		this.from = data.getFrom().getKey();
		this.subject = data.getSubject();
		this.personal = data.getFrom().getValue();
		this.attachments = data.getAttachments();
		if(StringUtil.hasText(replyTo)){
		    this.setReplyTo(data.getReplyTo());
		}
	}
	
	public Set<String> getTo() {
		return to;
	}
	public void setTo(Set<String> to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPersonal() {
		return personal;
	}
	public void setPersonal(String personal) {
		this.personal = personal;
	}
	
	public Map<String, InputStreamSource> getAttachments() {
		return attachments;
	}
	public String getPlainTextBody() {
		if(mailBody==null) return null;
		return mailBody.getPlainText();
	}
	public String getHtmlBody() {
		if(mailBody==null) return null;
		return mailBody.getHtml();
	}
	public void setMailBody(MailBody mailBody) {
		this.mailBody = mailBody;
	}
	public void setAttachments(Map<String, InputStreamSource> attachments) {
		this.attachments = attachments;
	}
	public void addAttachment(String name, InputStreamSource attachment){
		if(attachments==null) attachments = new HashMap<String, InputStreamSource>();
		attachments.put(name,attachment);
	}
	
	public boolean hasAttachments(){
		return !(attachments==null || attachments.isEmpty());
	}
    
    public String getReplyTo() {
        return replyTo;
    }
    
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
   
    public MailBody getMailBody() {
        return mailBody;
    }

    @Override
    public String toString() {
        return "MailMessage [to=" + to + ", from=" + from + ", subject=" + subject + ", personal=" + personal
                + ", mailBody=" + mailBody + ", attachments=" + attachments + "]";
    }
	
}
