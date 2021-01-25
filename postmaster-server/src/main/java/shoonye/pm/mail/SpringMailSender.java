package shoonye.pm.mail;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import shoonye.pm.bean.MailMessage;
import shoonye.util.StringUtil;

public class SpringMailSender implements MailSender {
	private static final Logger logger = LoggerFactory.getLogger(SpringMailSender.class);
	private static final String ALTERNATIVE = "alternative";
	
	
	Executor executor = Executors.newFixedThreadPool(5);
	private JavaMailSender sender;
	private boolean sendMailEnabled=true;

	/**
	 * send mail prepares an instance of MimeMessage using Sims3MailPreparator
	 * and sends mail.
	 */
	public boolean sendMail(final MailMessage mailMessage) {
	    if(sendMailEnabled){
	        MailPreparator preparator = new MailPreparator(mailMessage);
	        sender.send(preparator);
	    }else{
	        logger.warn("Not sending Mail, sending mail disabled");
	        logger.info("{}",mailMessage);
	    }
		return true;
	}

    public void setSender(JavaMailSender sender) {
        this.sender = sender;
    }

    
    public boolean isSendMailEnabled() {
        return sendMailEnabled;
    }

    
    public void setSendMailEnabled(boolean reallySendMail) {
        this.sendMailEnabled = reallySendMail;
    }

    private class MailPreparator implements MimeMessagePreparator {
		protected MailMessage mailMessage;
		
		MailPreparator(MailMessage mailMessage) {
			this.mailMessage = mailMessage;
		}

		public void prepare(MimeMessage msg) throws Exception {
			logger.debug("Starting prepare(MimeMessage msg)");
			try {
				InternetAddress ia = new InternetAddress(mailMessage.getFrom());
				if (mailMessage.getPersonal() != null) {
					ia.setPersonal(mailMessage.getPersonal());
				}
				msg.addFrom(InternetAddress.parse(ia.toString()));
				for(String to : mailMessage.getTo()){
					msg.addRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
				}
				
				if(StringUtil.hasText(mailMessage.getReplyTo())){
				    msg.setReplyTo(InternetAddress.parse(mailMessage.getReplyTo()));
				}
				
				msg.setSubject(mailMessage.getSubject());
				MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(msg, true);

				if(mailMessage.hasAttachments()){
					Set<Map.Entry<String, InputStreamSource>> entrySet = mailMessage.getAttachments().entrySet();
					for (Entry<String, InputStreamSource> entry : entrySet) {
						mimeMessageHelper.addAttachment(entry.getKey(), entry.getValue());
					}
				}

				MimeMultipart mmPartRoot = mimeMessageHelper.getMimeMultipart();
				
				MimeMultipart mpContent = new MimeMultipart(ALTERNATIVE);
				
				BodyPart plainTextBodyPart = new MimeBodyPart();
				plainTextBodyPart.setContent(mailMessage.getPlainTextBody(),"text/plain; charset=UTF-8");
				mpContent.addBodyPart(plainTextBodyPart);
				
				BodyPart htmlBodyPart = new MimeBodyPart();
				htmlBodyPart.setContent(mailMessage.getHtmlBody(),"text/html; charset=UTF-8");
				mpContent.addBodyPart(htmlBodyPart);
				
				MimeBodyPart contentPartRoot = new MimeBodyPart();
				contentPartRoot.setContent(mpContent);

				mmPartRoot.addBodyPart(contentPartRoot);

			} catch (AddressException ae) {
				logger.error("Can not preapre Email, Invalid from address : ",ae);
				throw ae;
			} catch (MessagingException me) {
				logger.error("Can not preapre Email, messaging exception : ",
						me);
				throw me;
			}
		}
	}

}
