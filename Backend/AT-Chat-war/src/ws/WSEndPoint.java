package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@Singleton
@ServerEndpoint("/ws")
@LocalBean
public class WSEndPoint {

static List<Session> sessions = new ArrayList<Session>();
	
	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
	}
	

	@OnMessage
	public void echoTextMessage(String msg) {
		
			for(Session entry : sessions) {
				try {
					entry.getBasicRemote().sendText(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
	}
	
	@OnClose
		public void onClose(Session session, CloseReason closeReason) {
			// TODO Auto-generated method stub
		sessions.remove(session);
		}
		
	
}
