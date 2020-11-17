package application;

public class Player extends Character{
	
	private String classes;
	
	private String CampaignName;
	Player(String name, String classes, Campaign campaign) 
	{
		this.setName(name);
		this.setClasses(classes);
		this.setCampaignName(campaign.getName());
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
	
}
