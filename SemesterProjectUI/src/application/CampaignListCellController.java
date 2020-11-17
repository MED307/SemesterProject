package application;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class CampaignListCellController extends ListCell<Campaign>{

	@FXML
	Text name;
	
	@FXML
	AnchorPane pane;
	@Override
    protected void updateItem(Campaign campaign, boolean empty) 
	{
        super.updateItem(campaign, empty);
        
        //checks that the message to be displayed is not null
        if(empty || campaign == null) 
        {

            setText(null);
            setGraphic(null);

        } 
        else 
        {
        	//remove everything after the first digit in the ID so only the name is displayed
        	setText(campaign.getName());
        	setGraphic(pane);
        }
	}
}
