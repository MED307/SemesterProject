package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;

public class PlayerListCellController extends ListCell<Player>{

	@FXML
	Button stats;
	
	@FXML
	Button edit;
	
	@FXML
	Button remove;
	
	@FXML
	Text name;
	
	@FXML
	Text playerDesc;
	
	@Override
    protected void updateItem(Player player, boolean empty) 
	{
        super.updateItem(player, empty);
        
        if(empty || player == null) 
        {

            setText(null);
            setGraphic(null);

        } 
        else 
        {
        	//remove everything after the first digit in the ID so only the name is displayed
        	name.setText(player.getName());
        }
	}
}
