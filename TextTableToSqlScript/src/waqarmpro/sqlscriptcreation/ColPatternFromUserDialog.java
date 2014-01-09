package waqarmpro.sqlscriptcreation;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ColPatternFromUserDialog {
	
	String[] inputInstructions = new String[11];
	
	public ColPatternFromUserDialog() {
		//assemble instructions for user for input that is requested
		inputInstructions[0] = "SQL requires some datatypes to be enclosed in quotes while others are not.\n";
		inputInstructions[1] = "Examine column datatypes of table you are creating to identify which ones need quotes.";
		inputInstructions[2] = "    Use  letter n to specify columns of number datatype which are not enclosed in quotes";
		inputInstructions[3] = "    Use q to specify columns that are non-number datatype and must be enclosed in quotes";
		inputInstructions[4] = "\nFor example, if your table has columns of: ";
		inputInstructions[5] = "    EmployeeID (number--SQL does not need quotes for this datatype)->use n";
		inputInstructions[6] = "    hiredate (date--SQL requires quotes for this datatype)->use q";
		inputInstructions[7] = "    name (VARCHAR2--SQL requires quotes for this datatype)->use q";
		inputInstructions[8] = "then you would enter below: nqq";
		inputInstructions[9] = "    (you do not need to use quotes, spaces, brackets or anything else in your entry)";
		inputInstructions[10] = "\nEnter below the sequence of letters n and/or q in same order in which columns exist in table.";
	}
	
	//Gets input via dialog box by instantiating class UserEntryDialog and checks if valid
	public String getColPatternFromUser() {

		String regexToRemoveEnclosersSeparators = "['*\"*{*}*(*)*\\[*\\]*\\.*,*-*_*:*;*/*\\*|*\\**\\s]";
		String regexToCheckForInvalidEntry = "[^nNqQ]";
		Pattern patternForInvalidEntry = Pattern.compile(regexToCheckForInvalidEntry);
	
		//instantiate object to accept input via keyboard
		UserEntryDialog userEntryDialog = new UserEntryDialog(this.inputInstructions);
		
		//variables for exit condition for while loop below
		int count = 0;
		boolean validEntry = false;
		
		//Loop until valid entry, or 5 consecutive invalid entries
		while ( (validEntry == false) && (count < 5) ) {
			
			//Get entry from user
			userEntryDialog.getTextInput();
			
			//if user used anything to enclose or separate letters n and q in sequence then remove
			userEntryDialog.textEntered = userEntryDialog.textEntered.replaceAll(regexToRemoveEnclosersSeparators,"");
	
			//check for invalid characters--anything other than sequence of letters n and q
			Matcher matcher = patternForInvalidEntry.matcher(userEntryDialog.textEntered);
			if (matcher.find()) {
				if (count == 0)
					inputInstructions[0] = "ERROR! Unexpected character found. "+
							"Please enter only a sequence of letters n and q\n\n"+inputInstructions[0];
				count++;
			}
			else
				validEntry = true;
		}		
		if (count == 5) {
			System.out.println("5 consecutive invalid entries done for column type pattern. Please read documentation.\nExiting program.");
			System.exit(1);
		}
		return userEntryDialog.textEntered;					
	}
}
