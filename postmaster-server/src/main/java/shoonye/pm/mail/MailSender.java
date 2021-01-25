package shoonye.pm.mail;

import shoonye.pm.bean.MailMessage;

public interface MailSender {	
	public boolean sendMail(MailMessage mailMessage);
	
}
