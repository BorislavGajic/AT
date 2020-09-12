package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;



@Startup
@Singleton
public class Data {

	private static ArrayList<AgentType> types = new ArrayList<>();
	private static ArrayList<AgentskiCentar> agentskiCentri = new ArrayList<>();
	private static HashMap<String, Agent> agents = new HashMap<>();
	public static List<Agent> agenti = new ArrayList<>();

	static {

		AgentType a = new AgentType();
		a.setModule("abc");
		a.setName("Ping");
		types.add(a);

		AgentType a1 = new AgentType();
		a1.setModule("abc");
		a1.setName("Pong");
		types.add(a1);

	}

	public ArrayList<AgentskiCentar> getAgentskiCentri() {
		return agentskiCentri;
	}

	public void setAgentskiCentri(ArrayList<AgentskiCentar> agentskiCentri) {
		this.agentskiCentri = agentskiCentri;
	}

	public ArrayList<AgentType> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<AgentType> tipovi) {
		this.types = tipovi;
	}

	public HashMap<String, Agent> getAgents() {
		return agents;
	}

	public void setAgents(HashMap<String, Agent> agenti) {
		this.agents = agenti;
	}

}