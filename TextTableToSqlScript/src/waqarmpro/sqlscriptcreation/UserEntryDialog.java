package waqarmpro.sqlscriptcreation;

import javax.swing.JOptionPane;


//eventually move this to a package generally available

//This class is for a dialog box for text input. It displays instructions
//to user and accepts a string input
public class UserEntryDialog {
	String[] inputInstructions;
	String textEntered;
	
	//Constructor that accepts instructions for user via its argument
	public UserEntryDialog(String[] inputInstructions) {
		this.inputInstructions = inputInstructions;
	}
	
	//method that gets text input by user
	public void getTextInput() {
		String instructionsForInput = "";
		for (int i=0; i<this.inputInstructions.length; i++) {
			instructionsForInput += (this.inputInstructions[i]+"\n");
		}
		this.textEntered = JOptionPane.showInputDialog(instructionsForInput);
	}

}
