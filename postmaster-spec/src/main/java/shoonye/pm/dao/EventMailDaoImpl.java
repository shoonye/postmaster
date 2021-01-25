package shoonye.pm.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import shoonye.pm.entity.EventMail;
import shoonye.util.hibernate.HibernateBaseDao;


@Component("eventMailDao")
public class EventMailDaoImpl extends HibernateBaseDao<EventMail, Integer> implements EventMailDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<EventMail> findByEventName(String eventName) {
		Criteria cirteria = session().createCriteria(EventMail.class);
		cirteria.add(Restrictions.eq("eventName", eventName));
		return cirteria.list();
	}
	
    @Override
    public EventMail findByEventNameAndProcessorKey(String eventName, String processorKey) {
        Criteria cirteria = session().createCriteria(EventMail.class);
        cirteria.add(Restrictions.eq("eventName", eventName));
        cirteria.add(Restrictions.eq("processorKey", processorKey));
        return (EventMail)cirteria.uniqueResult();
    }


    @SuppressWarnings("unchecked")
    @Override
    public void deleteAll() {
        Criteria cirteria = session().createCriteria(EventMail.class);
        List<EventMail> all = cirteria.list();
        for(EventMail em : all){
            session().delete(em);
        }
    }
}
