package ws;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import model.MessageData;
import model.Message;


@Singleton
@ServerEndpoint("/ws/{user}")
@LocalBean
public class WSEndPoint {

static Map<String, Session> sessions = new ConcurrentHashMap<>();
	
	@OnOpen
	public void onOpen(@PathParam("user") String user, Session session) {
		sessions.put(user, session);
	}
	

	@OnMessage
	public void echoTextMessage(String msg) {
		
		Gson g = new Gson();		
		Message message = g.fromJson(msg, Message.class);
		MessageData.getInstance().getMessages().add(message);
		if (message.getReceiver().equals("All")) {
			for(Map.Entry<String, Session> entry : sessions.entrySet()) {
				try {
					entry.getValue().getBasicRemote().sendText(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			
			try {
				sessions.get(message.getReceiver()).getBasicRemote().sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				sessions.get(message.getSender()).getBasicRemote().sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@OnClose
		public void onClose(@PathParam("user") String user, Session session, CloseReason closeReason) {
			// TODO Auto-generated method stub
		sessions.remove(user);
		}
		
	
}
