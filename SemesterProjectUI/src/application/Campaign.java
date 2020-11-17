package application;

import java.util.ArrayList;

public class Campaign {
	
	private String campaignName;
	
	private ArrayList<Player> players = new ArrayList<>();
	
	
	Campaign() 
	{

	}

	
	public void setCampaignName(String _name)
	{
		this.campaignName = _name;
	}
	public String getCampaignName()
	{
		return campaignName;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
}
