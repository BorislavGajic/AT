package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable {

    private String receiver;
    private String sender;
    private String dateTime;
    private String subject;
    
    
    
	public Message() {
		super();

	}
	public Message(String receiver, String sender, String dateTime, String subject) {
		super();
		this.receiver = receiver;
		this.sender = sender;
		this.dateTime = dateTime;
		this.subject = subject;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
    
    
}
