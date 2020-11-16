package application;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

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
        	campaign.setName(result.get());
        	campaignList.getItems().add(campaign);
        }

	}
	
	public void displayCampaign(MouseEvent arg0)
	{
		currentCampaign = campaignList.getSelectionModel().getSelectedItem();
		chosenCampaign.setText(currentCampaign.getName());
		
		
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		campaignList.setCellFactory(new Callback<ListView<Campaign>, ListCell<Campaign>>() {
		    @Override
		    public ListCell<Campaign> call(ListView<Campaign> lv) {
		        return new ListCell<Campaign>() {
		            @Override
		            public void updateItem(Campaign item, boolean empty) {
		                super.updateItem(item, empty);
		                if (empty || item == null) {
		                    setText(null);
		                } else {
		                    setText(item.getName());
		                }		             
		            }
		        };
		    }
		});
	}
}
