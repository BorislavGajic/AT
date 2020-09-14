package agents;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Stateful;

import jms.JMSQueue;
import model.ACLPoruka;
import model.AID;
import model.Agent;
import model.Performative;
import java.util.Map.Entry;

@Stateful
public class Initiator extends Agent{
	
	public static HashMap<AID, Integer> offers = new HashMap<>();
	
	@Override
	public void handleMessage(ACLPoruka poruka) {
		System.out.println(poruka.getPerformative());
		switch (poruka.getPerformative()) {
		case REQUEST: // client initiated, sends cpf to all running participant agents
			System.out.println("Initiator agent got first message: " + poruka.getContent());
			handleRequest(poruka);
			waitForParticipans(poruka, 5);
			break;

		case RESUME:
			chooseAgent(poruka);
			break;

		case REFUSE:
			handleRefuse(poruka);
			break;

		case PROPOSE:
			handlePropose(poruka);
			waitForParticipans(poruka, 5);

			break;

		case FAILURE:
			System.out.println("PROCESS ABORTED: Choosen agent: [" + poruka.getSender().getName() + " - "
					+ poruka.getSender().getType().getName() + "] failed to execute the task.");
			break;

		case INFORM:
			System.out.println(
					"Choosen agent: [" + poruka.getSender().getName() + " - " + poruka.getSender().getType().getName()
							+ "] successfully executed the task. Returned: " + poruka.getContent());
			break;

		default:
			System.out.println("Performative not supported.");
		}
	}
	
	private void handlePropose(ACLPoruka poruka) {
		try {
			int offer = (int) poruka.getContentObj();
			addOfferToSession(poruka.getConversationId(), poruka.getSender(), offer);
			
			System.out.println("Agent: [" + poruka.getSender().getName() + " - "
					+ poruka.getSender().getType().getName() + "] offered: " + offer);
			
		} catch (Exception e) {
			System.out.println("OFFER CANCELED: Agent: [" + poruka.getSender().getName() + " - "
					+ poruka.getSender().getType().getName() + "] sends an invalid value.");
			addOfferToSession(poruka.getConversationId(), poruka.getSender(), -1);
		}

	}
	
	private void addOfferToSession(String conversationID, AID aid, int value) {
		this.offers.put(aid, value);
		//System.out.println(offers.size());

		for (Entry<AID, Integer> entry : this.offers.entrySet()) {
			System.out.println(entry.getKey().getName()+ " - " + entry.getValue());
		}
	}
	
	private void handleRefuse(ACLPoruka poruka) {
		System.out.println("Agent: [" + poruka.getSender().getName() + " - " + poruka.getSender().getType().getName()
				+ "]  refused call for proposal.");

	}
	
	private void chooseAgent(ACLPoruka poruka) {

		System.out.println("Biram agenta");

		AID acceptedAgent = getBestOffer();

		if (acceptedAgent == null) {
			System.out.println("All agents refused to propose.");
			return;
		}

		System.out.println(
				"The choosen agent: [" + acceptedAgent.getName() + " - " + acceptedAgent.getType().getName() + "]");
	
		ArrayList<AID> agents = new ArrayList<AID>();


		for (Entry<AID, Integer> entry : offers.entrySet()) {
			agents.add(entry.getKey());
		}

		for (AID agent : agents) {
			if (agent.equals(acceptedAgent)) // send accept proposal
			{
				ACLPoruka acceptMessage = new ACLPoruka();
				acceptMessage.setSender(this.getId());
				AID receivers[] = new AID[] {};
				AID newreceivers[] = new AID[receivers.length +1 ];
				for(int i=0; i< receivers.length ; i++) {
					newreceivers[i] = receivers[i];
				}
				newreceivers[receivers.length] = acceptedAgent;	
				acceptMessage.setReceivers(newreceivers);
				acceptMessage.setContent("Accepted aid: " + agent.getName());
				acceptMessage.setPerformative(Performative.ACCEPT_PROPOSAL);
				acceptMessage.setConversationId(poruka.getConversationId());

				new JMSQueue(acceptMessage);
			} else // send decline proposal
			{
				ACLPoruka declineMessage = new ACLPoruka();
				declineMessage.setSender(this.getId());
				declineMessage.setContent("Declined aid: " + agent.getName());
				declineMessage.setConversationId(poruka.getConversationId());
				AID receivers[] = new AID[] {};
				AID newreceivers[] = new AID[receivers.length +1 ];
				for(int i=0; i< receivers.length ; i++) {
					newreceivers[i] = receivers[i];
				}
				newreceivers[receivers.length] = agent;	
				declineMessage.setReceivers(newreceivers);
				declineMessage.setPerformative(Performative.REJECT_PROPOSAL);
				
				new JMSQueue(declineMessage);
			}
		}

	}
	
	private AID getBestOffer() {
		System.out.println(this.offers.size());
		ArrayList<AID> agents = new ArrayList<AID>();
		for (Entry<AID, Integer> entry : this.offers.entrySet()) {
			agents.add(entry.getKey());
			System.out.println("aid od svih "+entry.getKey().getName());
		}
		
		ArrayList<Integer> offers = new ArrayList<Integer>();
		for (Entry<AID, Integer> entry : this.offers.entrySet()) {
			offers.add(entry.getValue());
		}
		
		int bestOffer = 0;
		int bestOfferIndex = -1;

		for (int i = 0; i < offers.size(); i++) {
			if (offers.get(i) > bestOffer) {
				bestOffer = offers.get(i);
				bestOfferIndex = i;
			}
		}
		System.out.println("Best offer: " + bestOffer + " i index "+ bestOfferIndex);

		if (bestOfferIndex == -1 || bestOffer == -1)
			return null;
		return agents.get(bestOfferIndex);
	}
	
	
	private void handleRequest(ACLPoruka poruka) {
		
		AID participantAID = poruka.getReplyTo();
		ACLPoruka msgToParticipant = new ACLPoruka();
		msgToParticipant.setContent("Message from initiator...");
		msgToParticipant.setPerformative(Performative.CALL_FOR_PROPOSAL);
		msgToParticipant.setSender(this.getId());
		AID receivers[] = new AID[] {};
		AID newreceivers[] = new AID[receivers.length +1 ];
		for(int i=0; i< receivers.length ; i++) {
			newreceivers[i] = receivers[i];
		}
		newreceivers[receivers.length] = participantAID;	
		msgToParticipant.setReceivers(newreceivers);
		
	
		new JMSQueue(msgToParticipant);
		
		System.out.println("CALL FOR PROPOSAL MESSAGE FROM INIT: " + msgToParticipant.getContent().toString() + "FROM " + msgToParticipant.getSender().toString());
		


		}
	
	private void waitForParticipans(ACLPoruka poruka, int sleep) {
		System.out.println("Agent: [" + this.getId().getName() + " - Initiator] waits for proposals to collect for "
				+ sleep + " seconds.");

		ACLPoruka pause = new ACLPoruka();
		AID receivers[] = new AID[] {};
		AID newreceivers[] = new AID[receivers.length +1 ];
		for(int i=0; i< receivers.length ; i++) {
			newreceivers[i] = receivers[i];
		}
		newreceivers[receivers.length] = this.getId();	
		pause.setReceivers(newreceivers);
		pause.setContent("Pause from initiator finished.");
		pause.setConversationId(poruka.getConversationId());
		pause.setSender(this.getId());
		pause.setPerformative(Performative.RESUME);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(sleep * 1000);
					
					new JMSQueue(pause);				
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();

	}
}
