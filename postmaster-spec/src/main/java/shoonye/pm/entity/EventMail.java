package shoonye.pm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import shoonye.util.hibernate.AuditedEntity;

@Entity
@Table(name="event_mail")
@Audited
public class EventMail extends AuditedEntity<Integer>{
	private static final long serialVersionUID = 1L;

	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id private Integer id;
	
	@Column(name="event_name", length=100) private String eventName;
	@Column(name="processor_key", length=50) private String processorKey;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String event_name) {
		this.eventName = event_name;
	}
	public String getProcessorKey() {
		return processorKey;
	}
	public void setProcessorKey(String processor_key) {
		this.processorKey = processor_key;
	}
    @Override
    public String toString() {
        return "EventMail [eventName=" + eventName + ", processorKey=" + processorKey + "]";
    }
}
