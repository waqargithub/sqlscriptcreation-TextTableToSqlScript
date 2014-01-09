/*Class: TextTableToSqlScript
 * Author: Waqar Mahmood
 * Version: 2.0
 * Date of Rev 1.0: December 22, 2013
 * Date of current revision: January 8, 2014
 */

/* Purpose: 
 * This program accepts a file that has text data for a table and creates
 * the SQL script needed to load these values into a database table.
*/

/* Example of Expected Input and Corresponding Output
 * 
 * Input File: C:\Employees\Employee_Data.txt
 * File Contents:
 * Emp_No	Emp_Name	Emp_Hiredate
 * 1002		Pat Harris	10-Jun-1996
 * 1003		Chris Reyes	23-Jun-1996
 * 
 * This column pattern type expected is: nqq
 * Because Emp_no  is not to be enquoted whereas the other two are.
 * 
 * Output File: C:\Employees\Employee_Data_Script.sql
 * File Contents:
 * INSERT INTO Employee_Data VALUES
 * 	(1002, 'Pat Harris', '10-Jun-1996');
 * INSERT INTO Employee_Data VALUES
 * 	(1003, 'Chris Reyes', '23-June-1996');
 */

/*Assumptions:
 * 
 * 1. The text file for database table Some_Name has same name for filename: Some_Name  
 * 	(with whichever extension). E.g. Some_Name.txt
 * 2. The first row of the text file has column names.
 * 3. The SQL script is to be written to same folder as text file.
 * 4. The column type pattern specifies which columns are datatype Numbers
 * 	and which are not. Non-number types are  to be enclosed with single quotes.
 * 5. Non-number types do not already have single quotes enclosing them.
 * 6. If the delimiter is white space, it is NOT single space.
 * 
 * Planned Revisions:
 * 
 * 1. Added in Revision 1.2: Use file dialog box to specify table.
 * 2. Added in Revision 1.1: Allow file extensions of any length.
 * 3. Have code scan text file and identify delimiter.
 * 3b. Added in Revision 1.2: Allow more than one tab.
 * 4. Accommodate for tables that do not have a header, or have Table name also.
 * 5. Allow dialog box to specify where table script should be written.
 * 5b. Allow user to specify output file name.
 * 6. Have code scan text file and determine column datatype.
 * 7. Allow user to identify SQL script that creates table and use it to define
 * 	column data types. 
 * 8. Accommodate tables that already have non-number types in quotes.
 * 9. Added in Revision 2.0: Allow user to specify column data types via dialog box.
 * 10. Allow user to specify parameters via Args
 * 11. Allow multiple table creation
 * 12. How to handle special characters other than single quotes.
 * 13. Allow option to append to existing script file?
 * 14. Review file I/O efficiency for write operation
 * 15. Tolerate click on cancel in file dialog box
 * 16. Check if non-number types already have quotes around them
 * 17. Added in Revision 2.0: Check that column pattern type string only has valid letters.
 * 18. Allow user input for delimiter
 * 19. Allow user input for enquote symbol
 * 20. Allow more efficient ways of allowing column pattern type (e.g. q* for all q, q4n for qqqqn).
 */

/*Changes Effected via Revision 1.1
 * 1. Allow file extensions of any length.
 */

/*Changes Effected via Revision 1.2
 * 1. Added assumption 8: delimiter cannot be single space.
 * 2. Accepts any amount of white space greater than single space as delimiter:
 * a) added constructor
 * b) added if statement that replaces white space with single tab if delimiter is white space
 * c) Modified main to call constructor that takes only one argument.
 * 3. Added code to replace & with || CHR(38) ||
 * 4. Fixed bug with code that allows file extension of any size.
 * 5. Allow text file with values for table to be specified by file dialog box.
 */

/*Changes Effected via Revision 2.0
 * 1. Allow column pattern type to be entered via keyboard input from user, check for errors
 * a) Added class UserEntryDialog, ColPatternFromUserDialog
 * b) Added constructor when no arguments are provided
 * c) Declared char[] variable and modified loop for enquote to use char[]
 */



package waqarmpro.sqlscriptcreation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.util.Arrays;


//import waqarmpro.iotools.TextInputDialog;

public class TextTableToSqlScript {

	public static void main(String[] args) {

		class TableFile {
			public String folderPath;
			public String fileName;
			public String tableName;
			public String columnTypePattern;
			public String delimiter;

			//Overloaded constructors to allow different input scenarios
			
		
			//User provides all parameters
			public TableFile(String folderPath, String fileName, String tableName, 
					String columnTypePattern, String delimiter) {
				super();
				this.folderPath = folderPath;
				this.fileName = fileName;
				this.tableName = tableName;
				this.columnTypePattern = columnTypePattern;
				this.delimiter = delimiter;
			}
			
			//User does not provide table name; it is derived from file name			
			public TableFile(String folderPath, String fileName, 
					String columnTypePattern, String delimiter) {
			super();
			this.folderPath = folderPath;
			this.fileName = fileName;
			this.tableName = this.fileName.substring(0, this.fileName.lastIndexOf('.'));
			this.columnTypePattern = columnTypePattern;
			this.delimiter = delimiter;
		}
			
			//user provides file name via dialog box, table name derived
			public TableFile(String columnTypePattern, String delimiter) {
				super();
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.showOpenDialog(null);
				this.folderPath = fileChooser.getSelectedFile().getParent();
				this.fileName = fileChooser.getSelectedFile().getName();
				this.tableName = this.fileName.substring(0, this.fileName.lastIndexOf('.'));
				this.columnTypePattern = columnTypePattern;
				this.delimiter = delimiter;
			}
			
			//user provides file name via dialog box, table name derived, no delimiter specified (default is white space)
			public TableFile(String columnTypePattern) {
				super();
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.showOpenDialog(null);
				this.folderPath = fileChooser.getSelectedFile().getParent();
				this.fileName = fileChooser.getSelectedFile().getName();
				this.tableName = this.fileName.substring(0, this.fileName.lastIndexOf('.'));
				this.columnTypePattern = columnTypePattern;
				this.delimiter = "\\s";
			}
			
			//user provides file name via dialog box, table name derived, no delimiter specified (default is white space)
			//Column type pattern also obtained via input dialog box
			public TableFile() {
				super();
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(null);
				this.folderPath = fileChooser.getSelectedFile().getParent();
				this.fileName = fileChooser.getSelectedFile().getName();
				this.tableName = this.fileName.substring(0, this.fileName.lastIndexOf('.'));
				ColPatternFromUserDialog colPatternFromUserDialog = new ColPatternFromUserDialog(); 
				this.columnTypePattern = colPatternFromUserDialog.getColPatternFromUser();
				this.delimiter = "\\s";
			}
			
/*			//eventually move this to a package generally available
			//This class is for a dialog box for text input. It displays instructions
			//to user and accept a string input
			class TextInputDialog {
				String[] inputInstructions;
				String inputEntered;
				
				//Constructor that accepts instructions for user via its argument
				public TextInputDialog(String[] inputInstructions) {
				this.inputInstructions = inputInstructions;
				}
				
				//method that gets text input by user
				public void getTextInput() {
					String instructionsForInput = "";
					for (int i=0; i<this.inputInstructions.length; i++) {
						instructionsForInput += this.inputInstructions[i];
					}
					this.inputEntered = JOptionPane.showInputDialog(instructionsForInput);
				}
			}
*/
			
			//method that gets column type pattern from user via input dialog box using class TextInputDialog
/*			private String getColPatternFromUser() {
				String[] inputInstructions = new String[11];
				String textEntered = "";
				
				//assemble instructions for user for input requested
				inputInstructions[0] = "SQL requires some datatypes to be enclosed in quotes while others are not.\n";
				inputInstructions[1] = "Examine datatypes of table you are creating to identify which ones need quotes.";
				inputInstructions[2] = "    Use  letter n to specify columns of number datatype which are not enclosed in quotes";
				inputInstructions[3] = "    Use q to specify columns that are non-number datatype and must be enclosed in quotes";
				inputInstructions[4] = "\nFor example, if your table has columns of: ";
				inputInstructions[5] = "    EmployeeID (a number--SQL does not need quotes for this datatype)->use n";
				inputInstructions[6] = "    hiredate (date--SQL requires quotes for this datatype)->use q";
				inputInstructions[7] = "    name (VARCHAR2--SQL requires quotes for this datatype)->use q";
				inputInstructions[8] = "\nthen you would enter below: nqq";
				inputInstructions[9] = "    you do not need to use quotes or anything else in your entry)";
				inputInstructions[10] = "\nEnter below the sequence of letters n and/or q in same order in which columns exist in table.";
						
				//instantiate object to accept input via keyboard and then get this entry
				boolean validEntry = false;
*/
//				String regexToRemoveEnclosersSeparators = "['*\"*{*}*(*)*\\[*\\]*\\.*,*-*_*:*;*/*\\*|*\\**]";
/*				String regexToCheckForInvalidEntry = "[^nNqQ]";
				Pattern patternForInvalidEntry = Pattern.compile(regexToCheckForInvalidEntry);
				
				TextInputDialog textInputDialog = new TextInputDialog(inputInstructions);
				
				int count = 0;
				while ( (validEntry == false) && (count < 5) ) {
					textInputDialog.getTextInput();
					textEntered = textInputDialog.inputEntered;
					
					//if user used quotes, commas, periods, or brackets then remove them
					textEntered = textEntered.replaceAll(regexToRemoveEnclosersSeparators,"");
	
					Matcher matcher = patternForInvalidEntry.matcher(textEntered);
					if (matcher.find()) {
						inputInstructions[0] = "Unexpected character found. Please enter only a sequence of n and q\n"+
								inputInstructions[0];
						count++;
					}
					else
						validEntry = true;
				}		
				if (count == 5) {
					System.out.println("5 consecutive invalid entries done for column type pattern. Please read documentation.\nExiting program.");
					System.exit(1);
				}
				return textEntered;					
			}
*/			
			//This function encloses non-number datatypes in single quotes.
			private String enquote(String textIn) {
				String text = textIn;
				
				//If a table field value  has an apostrophe or single quote in text
				//data type then add another single quote as SQL escape character
				//
				text = text.replaceAll("'", "''");
				return ("'" + text + "'");
			}
			
			//This fuction reads the table file and then writes the script file
			public void writeSqlScript(char enquoteSymbol) {

				//Declare variables
				
				//variables needed to read table
				String readPath = this.folderPath + "\\" + this.fileName;
				BufferedReader table = null;
				String tableRow = null;
				String[] columns;
				char[] columnTypePattern = this.columnTypePattern.toCharArray();
				
				//variables needed to write table
				String writePath = this.folderPath + "\\" + this.tableName + "_script.sql";
				//each table row results in two lines written to script file
				String lineToWrite1, lineToWrite2;

				
				//open file to read
				try {
					FileReader fileReader = new FileReader(readPath);
					table = new BufferedReader(fileReader);
				} 
				catch (IOException e) {
					e.printStackTrace();
				} 
				
				//open file to write
				try {
					File outFile = new File(writePath);
					
					// if file doesn't exist, then create it
					if (!outFile.exists()) {
						outFile.createNewFile();
					}
				
					FileWriter fileWriter = new FileWriter(outFile.getAbsoluteFile());
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

					//read first line of table that has headers to remove it
					//since it does not get written to SQL script
					tableRow = table.readLine(); 
					
					//Line by line: read line from input, write two lines to output
					while ((tableRow = table.readLine()) !=  null) {
						
						
						//Split table row into array of column values of that row using delimiter
						//Default delimiter is white space (\s), in which case collapse space between columns
						if (this.delimiter == "\\s") {
							tableRow = tableRow.trim().replaceAll("\\s{2,}|\\t+|\\n+|\\r+", "\t");
							columns = tableRow.split("\t");
						}
						else {
						//read table row and split into array of column values of that row
							columns = tableRow.split(this.delimiter);
						}
						
						//Prepare first line written to SQL script
						lineToWrite1 = "INSERT INTO " + this.tableName + " VALUES\n";
						//Prepare beginning of second line written to SQL script
						lineToWrite2 = "\t(";
						//for each column value, perform needed checks and append to second script line
						for (int currentColumn = 0; currentColumn < columns.length; currentColumn++) {
							
							//Check if non-number data type and if single quotes must enclose this value
							if (columnTypePattern[currentColumn] == enquoteSymbol) {
								columns[currentColumn] = enquote(columns[currentColumn]);
							}
							
							//if column value has symbol "&", replace it with || CHR(38) || as reqd by sql
							columns[currentColumn] = columns[currentColumn].replaceAll("&", "|| CHR(38) ||");							
							
							//Comma separates columns in sql script. Add comma before all columns except
							//column[0] and then append column to second script line 
							if (currentColumn > 0)
								lineToWrite2 += ", ";
							lineToWrite2 += columns[currentColumn];
						}
						//when all columns have been written, close second script line
						//as needed by SQL syntax
						lineToWrite2 += ");\n";
						//write the two lines to file
						bufferedWriter.write(lineToWrite1);
						bufferedWriter.write(lineToWrite2);
					}
					//close both files
					bufferedWriter.close();
					table.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}

		}	 
//	String folder = "";
//	String file = "coursetable.txt";
//	String [] colPattern = {"q", "q", "q", "q"};
	String colPattern = "qqqq";	
//	String delimiter = "\\s";
//	TableFile tableFile = new TableFile(colPattern, delimiter);
//	TableFile tableFile = new TableFile(colPattern);
	TableFile tableFile = new TableFile();
	tableFile.writeSqlScript('q');
	System.out.println("The sql script to create " + tableFile.tableName + " is complete!");		
	}
}
