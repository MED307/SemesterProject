package application;

public class Player extends Character{
	
	private String classes;
	
	private boolean moved = true;
	private boolean placed = false;
	
	private int[] pos =  {1000,1000};
	
	private String CampaignName;
	Player(String name, String classes, Campaign campaign) 
	{
		this.setName(name);
		this.setClasses(classes);
		this.setCampaignName(campaign.getCampaignName());
	}
	
	Player(String name, String classes) 
	{
		this.setName(name);
		this.setClasses(classes);
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public String getCampaignName() {
		return CampaignName;
	}

	public void setCampaignName(String campaignName) {
		CampaignName = campaignName;
	}

	public int[] getPos() {
		return pos;
	}

	public void setPos(int x, int y) {
		this.pos[0] = x;
		this.pos[1] = y;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public boolean isPlaced() {
		return placed;
	}

	public void setPlaced(boolean placed) {
		this.placed = placed;
	}
	
}
