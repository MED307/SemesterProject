package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class addPlayerController {
	
	@FXML
	private Button createChatBtn;
	
	@FXML
	private TextField playerName;
	
	@FXML
	private TextField classes;
	
	@FXML
	private Text infoText;
	
	@FXML
	private Text popErrorRoomTxt;
	
	@FXML
	private ComboBox<String> classSelectComboBox;
	
	private String newName;
	
	private String newClass;	
	
	@FXML
	public void initialize() {
		classSelectComboBox.getItems().removeAll(classSelectComboBox.getItems());
		classSelectComboBox.getItems().addAll("Artificer", "Barbarian", "Bard", "Bloodhunter", "Cleric", "Druid", "Fighter", "Monk", "Paladin", "Ranger", "Rogue", "Sorcerer", "Warlock", "Wizard");
		classSelectComboBox.getSelectionModel().select("Choose Class");
	}
	
	public void create(ActionEvent event)
	{
		if (playerName.getText().trim().isEmpty()) {
			System.out.println("Error");
			infoText.setVisible(false);
			popErrorRoomTxt.setText("Error: Missing name");
			popErrorRoomTxt.setVisible(true);
		}
		
		if (classSelectComboBox.getSelectionModel().getSelectedItem() == null) {
			System.out.println("Error");
			infoText.setVisible(false);
			popErrorRoomTxt.setText("Error: Missing Class");
			popErrorRoomTxt.setVisible(true);
			
		}
		
		
		else if (this.playerName.getText() != null && this.classSelectComboBox.getSelectionModel().getSelectedItem() != null) 
		{
			this.setNewName(this.playerName.getText());
			this.setNewClass(this.classSelectComboBox.getSelectionModel().getSelectedItem());
			((Stage)(((Button)event.getSource()).getScene().getWindow())).close(); 
		}
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewClass() {
		return newClass;
	}

	public void setNewClass(String newClass) {
		this.newClass = newClass;
	}
	
	
}
