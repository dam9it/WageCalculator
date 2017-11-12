package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class Calculator {
	ArrayList<String> MultiShiftDays = new ArrayList<String>();		//Creates the array to handle already calculated work days for the people who have many shifts in one day
	double MultiShift;							//Variable to hold the information on the total length of multiple shifts during the same day

	public String ShiftPayCalculator(String workStartTime, String workEndTime, boolean calcOver) {		//Calculator to calculate the shift's pay
		double totalPay = 0;			//Makes the variable that we will use to calculate the total pay for that shift
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");		//Set the format we want to use while calculating shift pay
		try {
			java.util.Date startEveningTime = time.parse("18:00");		//Where does the evening pay start
			java.util.Date endEveningTime = time.parse("06:00");		//Where does the evening pay end
			java.util.Date StartTime = time.parse(workStartTime);		//Makes the workStartTime String into a date
			java.util.Date EndTime = time.parse(workEndTime);			//Makes the workEndTime String into a date

			double TotalTime = 0;
			double difference = 0;

			if (EndTime.before(StartTime)) {			//Since we don't have the whole date here we check if the shift has ended before it started...
				EndTime.setDate(EndTime.getDate() + 1);		 //...If so it means that the day has changed during the shift and we set the end time to the next day
			}
			TotalTime = EndTime.getTime() - StartTime.getTime();		//Gets the total time between the end time and the start time so that we can get total time in work
			TotalTime = TotalTime / 60000;						//We convert the total time we just got to hours.
			totalPay = (TotalTime / 60) * 3.75;				//We calculate the base salary for the time at work

			if (EndTime.after(startEveningTime) || EndTime.before(endEveningTime) || StartTime.after(startEveningTime)
					|| StartTime.before(endEveningTime)) {		//If any on the working time happens to land in the evening work hours
				if (StartTime.before(startEveningTime) && !StartTime.before(endEveningTime)) {		//If the shift starts before evening pay starts but not before the evening pay ends..
					StartTime = startEveningTime;			//...we set the StartTime to where the evening pay starts
				}
				if (EndTime.after(endEveningTime) && EndTime.before(startEveningTime)) {		//If end time is after the evening pay ends and before it starts...
					EndTime = endEveningTime;					//...we set the end time to where the evening pay ends
				}
				difference = EndTime.getTime() - StartTime.getTime();		//Now that we have filtered out the time that is not during the evening pay time
				difference = difference / 60000;						//Convert the evening time to hours
				totalPay = totalPay + ((difference / 60) * 1.15);		//Calculate the evening pay and add it to the total pay for the shift
			}
			if (calcOver) {			//If we have selected to calculate overtime...
				totalPay = OverTimeCalc(TotalTime, totalPay);		//...goes to calculate overtime
			}
			if (!calcOver) {		//If we have selected not to calculate overtime...
				MultiShift = MultiShift + TotalTime;		//...adds the shift length to MultiShift variable so that we can calculate the overtime later
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return String.valueOf(totalPay);		//Return the shifts pay
	}

	public double OverTimeCalc(double TotalTime, double totalPay) {		//Calculator for calculating the overtime
		double splitti = 0;					//We create a variable that holds the time in any overtime category
		if (TotalTime > 480) {				//If the working time is over 8 hours
			splitti = TotalTime - 480;		//We get how much over time the worker has done after the 8 hours
			if (TotalTime < 120) {		//if it's more than 2 hours...
				splitti = 120;			//...we only take the 2 hours for this overtime category
			}
			totalPay = totalPay + ((splitti / 60) * 4.6875);		//we calculate the overtime for this category and add it to the totalPay variable
		}
		if (TotalTime > 600) {			//Same as above but for the next 2 hours
			splitti = TotalTime - 600;
			if (TotalTime < 120) {
				splitti = 120;
			}
			totalPay = totalPay + ((splitti / 60) * 5.625);
		}
		if (TotalTime > 720) {			//Same as above but without the 2 hour limiter
			splitti = TotalTime - 720;
			totalPay = totalPay + ((splitti / 60) * 7.5);
		}
		return (double) Math.round(totalPay * 100d) / 100d;		//Returns the days pay rounded to two decimal points. This is where the pays are rounded even if they don't have any overtime
	}

	public String MultiShiftPayCalculator(List<String> manyShiftsPerDay) {			//Calculator for when the worker has more then one shift during that day
		int index = MultiShiftDays.size();				//Makes index the size of the already calculated multi shift days
		double totalPay = 0;					//Make variable for the total pay for that day
		MultiShift = 0;					//Resets the variable for total working time for that day
		if (!MultiShiftDays.containsAll(manyShiftsPerDay)) {		//If the working day for that worker has not been calculated..,
			MultiShiftDays.addAll(index, manyShiftsPerDay);	//...we add the current working day to the list of working day we have already go through...
			for (int i = 0; i < manyShiftsPerDay.size(); i++) {	//...we go through the list that we got from the FileHandler.java...
				String data = manyShiftsPerDay.get(i);			//...adds the line in question to a String named data...
				String[] dataArray = data.split(",");			//...splits the data String by comma...
				totalPay = totalPay + Double.parseDouble(ShiftPayCalculator(dataArray[3], dataArray[4], false));		//...we send the current shift to be calculated but we don't calculate the overtime...
			}
			totalPay = OverTimeCalc(MultiShift, totalPay);		//... now that each shift for that worker for that day has been calculated we can calculate the overtime...
			return String.valueOf(totalPay);		//... we return the days pay
		} else {
			return "Many shifts in one day";		//If the current working day for that worker has already been calculated we just return statement that we have done so
		}
	}

	public void CalculateFullSalary(DefaultTableModel Firsttable, DefaultTableModel Secondtable) {		//Calculator for going through the table of shift pays and calculating the months pay for each worker
		if (Secondtable.getRowCount() == 0) {				//If we haven't calculated any monthly salaries...
			Secondtable.addRow(new Object[] { Firsttable.getValueAt(0, 2), Firsttable.getValueAt(0, 1),
					Firsttable.getValueAt(0, 6) });		//... we just add the first line from shift table and add it to salary table
		}
		for (int i = 0; i < Firsttable.getRowCount(); i++) {		//Start going through the shift table
			String data = (String) Firsttable.getValueAt(i, 6);		//Takes the pay for that shift/day
			if (data.chars().noneMatch(Character::isAlphabetic)) {	//checks if the shift/day pay is a number. If it isn't then we have calculated that one already and can ignore it
				boolean updated = false;							//Resetting the variable that tells us if we have made an update to the salaries list
				for (int j = 0; j < Secondtable.getRowCount(); j++) {		//Start going through the list on salaries
					if (Secondtable.getValueAt(j, 0).equals(Firsttable.getValueAt(i, 2))) {		//If the id in the salaries table matches the id of the current line we are going through in the shift table
						double palkka = Double.valueOf(Firsttable.getValueAt(i, 6).toString());		//Take the shifts pay from the shift table
						double palkka2 = Double.valueOf(Secondtable.getValueAt(j, 2).toString());	//Take the months salary from the salaries table
						double palkka3 = palkka + palkka2;				//Adds the salary and the shifts pay together
						Secondtable.setValueAt(palkka3, j, 2);			//Replaces the current line in the salaries table with just calculated salary
						updated = true;				//Change the updates variable to true
					}
				}
				if (!updated) {		//If we didn't update the salaries table then we didn't find the current id there...
					Secondtable.addRow(new Object[] { Firsttable.getValueAt(i, 2), Firsttable.getValueAt(i, 1),
							Firsttable.getValueAt(i, 6) });		//...we simply add the current row data for the shift table to the salaries table
				}
			}
		}
	}
}