package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	
	private String newName;
	
	private String newClass;	
	
	public void create(ActionEvent event)
	{
		if (playerName.getText().trim().isEmpty()) {
			System.out.println("Error");
			infoText.setVisible(false);
			popErrorRoomTxt.setText("Error: Missing name");
			popErrorRoomTxt.setVisible(true);
		}
		
		if (classes.getText().trim().isEmpty()) {
			System.out.println("Error");
			infoText.setVisible(false);
			popErrorRoomTxt.setText("Error: Missing Class");
			popErrorRoomTxt.setVisible(true);
			
		}
		
		else if (this.playerName.getText() != null && this.classes.getText() != null) 
		{
			this.setNewName(this.playerName.getText());
			this.setNewClass(this.classes.getText());
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
