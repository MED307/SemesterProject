package application;

import java.util.ArrayList;

public class Campaign {
	
	private String name;
	
	private ArrayList<Player> players;
	
	
	Campaign() 
	{

	}
	
	public void setName(String _name)
	{
		this.name = _name;
	}
	
	public String getName()
	{
		return name;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
}
