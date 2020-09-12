package model;

public class Agent {

	private AID id;

	public Agent() {
		super();

	}

	public Agent(AID id) {
		super();
		this.id = id;
	}

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}

	public void handleMessage(ACLPoruka poruka) {}
	public void init(AID aid) {}
	
}