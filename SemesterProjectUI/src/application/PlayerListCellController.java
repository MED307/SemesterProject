package application;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class PlayerListCellController extends ListCell<Player>{
	
	@FXML
	Text name;
	
	@FXML
	AnchorPane pane;
	
	FXMLLoader mLLoader;
	
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
        	//adds this as the controller for ChatListCell.FXML
        	if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("PlayerListCell.fxml"));
                mLLoader.setController(this);
                
                //tries to load the FXML
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
//        	name.setText(player.getName().substring(0,1).toUpperCase() + player.getName().substring(1) + " the " + player.getClasses().substring(0,1).toUpperCase() + player.getClasses().substring(1) + "  Init: " + player.getInitiative() );
        	name.setText(player.getName().substring(0,1).toUpperCase() + player.getName().substring(1) + " the " + player.getClasses().substring(0,1).toUpperCase() + player.getClasses().substring(1));
        	setGraphic(pane);
        }
	}
	
	public void edit() 
	{
		
	}
	
	public void stats()
	{
		
	}
	
	public void remove()
	{
		for(Campaign e : Main.campaigns) 
		{
			if(this.getItem() != null && e.getCampaignName().compareTo(this.getItem().getCampaignName()) == 0)
			{
				e.getPlayers().remove(this.getListView().getSelectionModel().getSelectedIndex());
			}
		}
		this.getListView().getItems().remove(this.getListView().getSelectionModel().getSelectedIndex());
	}
}
