package agents;

import javax.ejb.Stateful;

import jms.JMSQueue;
import model.ACLPoruka;
import model.AID;
import model.Agent;
import model.Performative;

@Stateful
public class Pong extends Agent {

	@Override
	public void handleMessage(ACLPoruka poruka) {
		System.out.println("Pong has rcived message, well see what it does. " + poruka);
		if (poruka.getPerformative().equals(Performative.REQUEST)) {
			ACLPoruka aclPoruka = new ACLPoruka();
			aclPoruka.setSender(this.getId());
			aclPoruka.setReceivers(new AID[] { poruka.getSender() });
			aclPoruka.setConversationId(poruka.getConversationId());
			aclPoruka.setContent("vratio");
			aclPoruka.setPerformative(Performative.INFORM);
			new JMSQueue(aclPoruka);
		}
	}
}