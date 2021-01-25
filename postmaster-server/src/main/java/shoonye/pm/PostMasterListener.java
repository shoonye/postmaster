package shoonye.pm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shoonye.dbmvc.ActionProcessor;
import shoonye.event.spec.Event;
import shoonye.event.spec.EventData;
import shoonye.pm.bean.MailMessage;
import shoonye.pm.dao.EventMailDao;
import shoonye.pm.entity.EventMail;
import shoonye.pm.mail.MailSender;
import shoonye.util.spring.ExecutionContextAwareExecutor;

@Service("postMasterListener")
@Transactional(readOnly = true)
@SuppressWarnings("rawtypes")
// TODO write a better listener (use AKKA for distributed processing?)
public class PostMasterListener {
    private static final Logger logger = LoggerFactory.getLogger(PostMasterListener.class);
    @Autowired private EventMailDao eventMailDao;
    @Autowired private ActionProcessor mailActionProcessor;
    private ExecutionContextAwareExecutor executor =  new ExecutionContextAwareExecutor();
    @Autowired private MailSender mailSender;
    
    public PostMasterListener(){
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.initialize();
    }

    public <T extends EventData> void onMessage(Event<T> event) {
        logger.debug("Recieved event for sending mail {}", event);
        List<EventMail> eventMails = eventMailDao.findByEventName(event.getEventType().getName());
        if(eventMails != null) {
            for(EventMail eventMail : eventMails) {
                sendMail(eventMail.getProcessorKey(), event.getData());
            }
        }
    }

    private void sendMail(final String key, final EventData eventdata) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    reallySendMail(key, eventdata);
                } catch(Exception e) {
                    logger.error("Error Sending Email : ", e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void reallySendMail(String key, EventData eventdata) {
        List<MailMessage> mailMessages = (List<MailMessage>) mailActionProcessor.process(key, eventdata);
        if(mailMessages!=null){
            for(MailMessage mailMessage : mailMessages){
                logger.debug("Sending mail {}", mailMessage);
                mailSender.sendMail(mailMessage);
            }
        }
        
    }

}
