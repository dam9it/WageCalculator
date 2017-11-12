package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

public class FileHandler {

	JFileChooser filechooser = new JFileChooser();
	ArrayList<String> WorkDays = new ArrayList<String>();

	public void FilePicker(DefaultTableModel model, DefaultTableModel model2) throws Exception {
		if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			Scanner input = new Scanner(file);
			while (input.hasNext()) {
				WorkDays.add(input.nextLine());		//Goes through the selected file line by line and adds the values to WorkDays array
			}
			input.close();
		}

		Calculator calculator = new Calculator();
		for (int i = 1; i < WorkDays.size(); i++) {		//Goes through the WorkDays array line by line
			String data = WorkDays.get(i);				//Adds the line in question to a String named data

			String[] dataArray = data.split(",");		//Splits the data String by comma

			List<String> manyShiftsPerDay = WorkDays.stream().filter(it -> it.contains("," + dataArray[1] + "," + dataArray[2])) //Goes through the WorkDays array to find if the worker id currently being handled has any other shifts during the same day
					.collect(Collectors.toList());

			if (manyShiftsPerDay.size() > 1) {		//If worker has more then one shift per that day...

				model.addRow(new Object[] { i, dataArray[0], dataArray[1], dataArray[2], dataArray[3], dataArray[4],
						calculator.MultiShiftPayCalculator(manyShiftsPerDay) });			//...adds the shift data to the table, but sends the data for all shifts for that worker for that day to a special calculator in Calculator.java
			} else {		//if the worker only had one shift during that day...

				model.addRow(new Object[] { i, dataArray[0], dataArray[1], dataArray[2], dataArray[3], dataArray[4],
						calculator.ShiftPayCalculator(dataArray[3], dataArray[4], true) });		//...adds the shift data to the table after calculating the shift wage in Calculator.java with instructions to calculate the overtime
			}
		}
		calculator.CalculateFullSalary(model, model2);		//Calculates the months salaries for every one on the list
	}

	public void FileSaver(DefaultTableModel model2) {
		if (filechooser.showSaveDialog(filechooser) == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			if (!file.toString().contains("."))				//If the selected name for the file does not contain a dot...
				file = new File(file.toString() + ".csv");		//...we add .csv to the file
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(file);
				StringBuilder builder = new StringBuilder();
				String ColumnNamesList = "Id,Name,Salary";		//Adds column names to the String that will be printed to the file
				builder.append(ColumnNamesList + "\n");			//Adds next line to the String

				for (int i = 0; i < model2.getRowCount(); i++) {		//Goes through the table with the monthly salaries one by one
					builder.append(model2.getValueAt(i, 0) + "," + model2.getValueAt(i, 1) + ",$"	//Adds every line to the String that will be printed to the file
							+ model2.getValueAt(i, 2) + '\n');
				}
				pw.write(builder.toString());			//Writes the String to the file
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

	}
}
