package application;
	
import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	public static ArrayList<Campaign> campaigns =  new ArrayList<>();
	@Override	
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			
			//Sets the location of where the FXML file is
			loader.setLocation(getClass().getResource("CampaignLoader.fxml"));
			
			//loads the fXML file
			AnchorPane root = (AnchorPane)loader.load();
			Scene scene = new Scene(root,1000,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setMinWidth(primaryStage.getWidth()/2);
	        primaryStage.setMinHeight(primaryStage.getHeight()/2);
		} catch(Exception e) {
			e.printStackTrace();
		}
		 
		
	}
	
	public static void main(String[] args) {
	
	
			launch(args);
	
		
	}
}
