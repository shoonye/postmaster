package shoonye.pm.dao;

import java.util.List;

import shoonye.pm.entity.EventMail;
import shoonye.util.hibernate.BaseDao;


public interface EventMailDao extends BaseDao<EventMail, Integer> {
	List<EventMail> findByEventName(String eventName);
	public EventMail findByEventNameAndProcessorKey(String eventName, String processorKey);
	void deleteAll();
}
