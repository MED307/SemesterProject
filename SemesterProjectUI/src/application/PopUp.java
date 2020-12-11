package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;



public class PopUp {
	
	private Campaign currentCampaign;

	public Player addPlayer(String title, String FXML) throws IOException
	{
		Player player = null;
		Stage window = new Stage();
		
		FXMLLoader loader = new FXMLLoader();
		
		loader.setLocation(getClass().getResource(FXML));
		
		window.initModality(Modality.APPLICATION_MODAL);
		
		AnchorPane root = (AnchorPane)loader.load();
		
		Scene popUp = new Scene(root);
		
		//popUp.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		window.setResizable(false);
		
		window.setScene(popUp);
		
		window.setTitle(title);
		
		window.setMinWidth(250);
		
		window.showAndWait();
		
	    if (FXML.compareTo("addPlayer.fxml") == 0)
	    {
	        addPlayerController controller = loader.getController();
	        player = new Player(controller.getNewName(), controller.getNewClass());
	    }
		
	    return player;
		
	}
	
	//get
	public Campaign getcurrentCampaign() {
		return currentCampaign;
		
	}
	//set
	public void setCampaign(Campaign currentCampaign) {
		this.currentCampaign = currentCampaign;

	
	}
}

