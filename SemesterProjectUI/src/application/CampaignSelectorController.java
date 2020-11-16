package application;

import java.awt.Button;
import java.awt.TextField;

import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class CampaignSelectorController {

	private ListView<Campaign> listView;
	private Button next;
	private Text campaignName;
	private TextField description;
	private Text adventurers;
	private ListView<Player> cplayers;
	private Button feather;
	
	
	public static Campaign cpn = new Campaign();

	
	void addPlayers(Player players) {
		this.cplayers.getItems().add(players);
	}
	
	void removePlayers(String name) {
		for(int i = 0 ; i <cpn.getPlayers().size() ; i++) {
			if(cpn.getPlayers().get(i).getName().compareTo(players) == 0) {
				players.remove();
			}
		}
	}
	
	void addCampaign() {
		
	}

	void removeCampaign(String name) {
		for(Campaign i:) {	
			if() {
				listView.remove(i);
			}
		}
	}
	
	
	
	//Getter and Setter for description
	public TextField getDescription() {
		return description;
	}
	public void setDescription(TextField description) {
		this.description = description;
	}
}
