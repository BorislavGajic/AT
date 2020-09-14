package agents;

import java.util.ArrayList;
import java.util.Random;

import javax.ejb.Stateful;
import javax.mail.search.RecipientStringTerm;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.mysql.fabric.Response;

import jms.JMSQueue;
import model.ACLPoruka;
import model.AID;
import model.Agent;
import model.Performative;

@Stateful
public class Participant extends Agent{
	@Override
	public void handleMessage(ACLPoruka poruka) {
		
		switch(poruka.getPerformative())
		{
			case CALL_FOR_PROPOSAL:
				handleCallForProposal(poruka);
			break;
			
			case REJECT_PROPOSAL:
				handleRejection(poruka);
			break;
			
			case ACCEPT_PROPOSAL:
				handleAcceptance(poruka);
			break;
			
			default:
				System.out.println("Performative not supported.");
		}
		
	}
	
	private void handleAcceptance(ACLPoruka poruka) {
		
		ACLPoruka aclPoruka = new ACLPoruka();
		AID participantAID = poruka.getSender();
		aclPoruka.setConversationId(poruka.getConversationId());
		aclPoruka.setContent("Participant accepted.");
		aclPoruka.setSender(this.getId());
		
		AID receivers[] = new AID[] {};
		AID newreceivers[] = new AID[receivers.length +1 ];
		for(int i=0; i< receivers.length ; i++) {
			newreceivers[i] = receivers[i];
		}
		newreceivers[receivers.length] = participantAID;	
		aclPoruka.setReceivers(newreceivers);
		
		aclPoruka.setPerformative(Performative.INFORM);
		new JMSQueue(aclPoruka);
		
	}
	
	private void handleRejection(ACLPoruka poruka) {
		System.out.println("Agent: [" + this.getId().getName() + " - " + this.getId().getType().getName() + "] is refused - ok:(");

		
	}
	
private void handleCallForProposal(ACLPoruka poruka) {
		
		Random rand = new Random();
		boolean refuseOrPropose = rand.nextBoolean();
		
		if(refuseOrPropose) //send propose
		{
			ACLPoruka proposeMessage = new ACLPoruka();
			AID participantAID = poruka.getSender();
			proposeMessage.setSender(this.getId());
			proposeMessage.setConversationId("cnet");
			proposeMessage.setContent("Call for proposal: sending propose...");
			
			AID receivers[] = new AID[] {};
			AID newreceivers[] = new AID[receivers.length +1 ];
			for(int i=0; i< receivers.length ; i++) {
				newreceivers[i] = receivers[i];
			}
			newreceivers[receivers.length] = participantAID;
			
			proposeMessage.setReceivers(newreceivers);
			proposeMessage.setPerformative(Performative.PROPOSE);
			
			proposeMessage.setContentObj(rand.nextInt(101)); //send a random number [0 - 100]
			new JMSQueue(proposeMessage);
			
		}
		else					//send refuse
		{
			ACLPoruka refuseMessage = new ACLPoruka();
			AID participantAID = poruka.getSender();
			refuseMessage.setSender(this.getId());
			refuseMessage.setConversationId("cnet");

			refuseMessage.setContent("Call for proposal: refused.");
			
			AID receivers[] = new AID[] {};
			AID newreceivers[] = new AID[receivers.length +1 ];
			for(int i=0; i< receivers.length ; i++) {
				newreceivers[i] = receivers[i];
			}
			newreceivers[receivers.length] = participantAID;
			
			refuseMessage.setReceivers(newreceivers);
			refuseMessage.setPerformative(Performative.REFUSE);

			new JMSQueue(refuseMessage);
			}
		
	}

}
