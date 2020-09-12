package jms;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.Session;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.Gson;

import model.ACLPoruka;
import model.AID;
import model.Agent;
import model.AgentskiCentar;
import model.Data;
import ws.WSEndPoint;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/exported/jms/queue/mojQueue") })

public class MDBConsumer implements MessageListener {

	@EJB
	WSEndPoint ws;

	@EJB
	Data database;

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		ObjectMessage msg = (ObjectMessage) arg0;
		System.out.println("uso");
		try {
			ACLPoruka acl = (ACLPoruka) msg.getObject();
			String currentIp = "";
			BufferedReader br = null;
			java.nio.file.Path p = Paths.get(".").toAbsolutePath().normalize();
			String line = "";

			try {
				br = new BufferedReader(new FileReader(p.toString() + "\\config.txt"));

				StringBuilder sb = new StringBuilder();
				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					try {
						line = br.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				currentIp = sb.toString();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			currentIp = currentIp.substring(2, currentIp.length() - 2);
			AgentskiCentar host = new AgentskiCentar("8080", currentIp);
			List<Agent> lista = new ArrayList<Agent>();
			for (AID a : acl.getReceivers()) {
				for (Agent ag : database.getAgents().values()) {
					if (a.getName().equals(ag.getId().getName())
							&& a.getType().getName().equals(ag.getId().getType().getName())) {
						lista.add(ag);
					}

				}
			}
			for (int i = 0; i < lista.size(); i++) {

				System.out.println(lista.get(i).getId().getHost().getAddress());
				System.out.println(host.getAddress());
				if (lista.get(i).getId().getHost().getAddress().equals(host.getAddress())) {
					ACLPoruka acln = new ACLPoruka(acl, i);
					
					String msgjson = "";
					Gson gson = new Gson();
					msgjson += gson.toJson(acln);
					ws.echoTextMessage(msgjson);
					lista.get(i).handleMessage(acl);

				} else {
					ACLPoruka acln1 = new ACLPoruka(acl, i);
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = client
							.target(lista.get(i).getId().getHost().getAddress() + "/ATProjectWAR/rest/messages");
					target.request(MediaType.APPLICATION_JSON).post(Entity.entity(acln1, MediaType.APPLICATION_JSON));
				}
			}

			// System.out.println(acl.toString());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}