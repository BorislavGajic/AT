package agents;

import javax.ejb.Stateful;

import jms.JMSQueue;
import model.ACLPoruka;
import model.AID;
import model.Agent;

import model.Performative;

@Stateful
public class Ping extends Agent {

	@Override
	public void handleMessage(ACLPoruka poruka) {
		System.out.println("Ping je primio poruku: " + poruka);
		if (poruka.getPerformative().equals(Performative.REQUEST)) {
			ACLPoruka aclPoruka = new ACLPoruka();
			aclPoruka.setSender(this.getId());
			aclPoruka.setReceivers(new AID[] { poruka.getSender() });
			aclPoruka.setConversationId(poruka.getConversationId());
			aclPoruka.setPerformative(Performative.INFORM);
			aclPoruka.setContent("vratio");
			new JMSQueue(aclPoruka);
		} else if (poruka.getPerformative().equals(Performative.INFORM)) {
			System.out.println("Pong je odgovorio.");
		}
	}

}