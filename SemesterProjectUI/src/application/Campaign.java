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
}
