package model;

import java.util.ArrayList;

import javax.ejb.Singleton;

import model.Message;

@Singleton
public class MessageData {
    private ArrayList<Message> messages;
    private static MessageData instance = null;
    
	public MessageData() {
		super();
		this.messages = new ArrayList<>();
	}
	
	public static MessageData getInstance() {
		if(MessageData.instance == null)
			MessageData.instance = new MessageData();
		return MessageData.instance;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}
    
    
}

