package application;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class CampaignLoaderController extends Controller implements Initializable{
	
	private Campaign currentCampaign;

	@FXML
	private Button newCampaignBtn;
	
	@FXML
	private Button loadCampaignBtn;
	
	@FXML
	private Button deleteCampaignBtn;
	
	@FXML
	private Button addPlayerBtn;
	
	@FXML
	private Button editDescriptionBtn;
	
	@FXML
	private ListView<Campaign> campaignList;
	
	@FXML
	private ListView<Player> playerList;
	
	@FXML
	private Text chosenCampaign;
	
	@FXML
	private TextArea descriptionTextarea;
	
	@FXML
	private ImageView menuShadowImg;
	
	
	public void createCampaign()
	{
		TextInputDialog userName = new TextInputDialog("campaign 0");
		userName.setTitle("Create Campaign");
		userName.setHeaderText("Choose a Campaign name");
		userName.setContentText("Campaign name: ");
		
		Optional<String> result = userName.showAndWait();
        
        if (result.isPresent()) 
        {
        	Campaign campaign = new Campaign();
        	campaign.setCampaignName(result.get());
        	campaign.getPlayers().add(new Player("Leg", "Fighter 6 - Warlock 8", campaign)); // Test
        	campaign.getPlayers().add(new Player("Brian", "Wizard 1 - Cleric 1", campaign)); // Test
        	campaignList.getItems().add(campaign);
        }

	}
	
	public void displayCampaign(MouseEvent arg0)
	{
		playerList.getItems().clear();
		currentCampaign = campaignList.getSelectionModel().getSelectedItem();
		if (currentCampaign != null)
		{
			chosenCampaign.setText(currentCampaign.getCampaignName());
			for(Player e: currentCampaign.getPlayers())
			{
				playerList.getItems().add(e);
			}
		}

		
		
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		playerList.setCellFactory(chatRoomListView -> new PlayerListCellController());
		campaignList.setCellFactory(chatRoomListView -> new CampaignListCellController());
	}
}
