package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.FileHandler;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class PalkanlaskentaGUI {

	private JFrame frame;
	private JTable table, salaryTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PalkanlaskentaGUI window = new PalkanlaskentaGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PalkanlaskentaGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 461);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JSplitPane splitPane = new JSplitPane();	//Creates the split layout for the top buttons
		frame.getContentPane().add(splitPane, BorderLayout.NORTH);	//Adds the split layout to the window frame
		
		DefaultTableModel model = new DefaultTableModel(); //Create the table for the work shifts and day's pay
		table = new JTable(model);
		model.addColumn("id"); 		//Adds the columns with the names for the table
		model.addColumn("Person Name"); 
		model.addColumn("Person ID");
		model.addColumn("Date"); 
		model.addColumn("Start");
		model.addColumn("End");
		model.addColumn("Days pay");
		
		DefaultTableModel model2 = new DefaultTableModel();  //Creates the table for the months salaries
		salaryTable = new JTable(model2);
		model2.addColumn("id"); 
		model2.addColumn("Person Name"); 
		model2.addColumn("Months salary");
		
		JButton btnSelectFile = new JButton("Select file");		//Creates the button for selecting the file

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);		//Creates the tab system where the tables are then placed
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);	//Adds the tab system to the window frame
		
		JScrollPane  scroll_table = new JScrollPane(table);  //Makes the table(named table) scrollable
	    scroll_table.setVisible(true);

	    tabbedPane.add("Data", scroll_table);	//Creates a tab called "Data" and adds the scrollable table

		JScrollPane  scroll_salary_table = new JScrollPane(salaryTable);  //Makes the table(named salaryTable) scrollable
	    scroll_table.setVisible(true);

	    tabbedPane.add("Salary calculations", scroll_salary_table);//Creates a tab called "Salary calculations" and adds the scrollable salary table
		
		splitPane.setLeftComponent(btnSelectFile); // adds the file selecting button to the top layout panel
		FileHandler filehandler = new FileHandler();
		btnSelectFile.addActionListener(new ActionListener() { 		//When the file selecting button is pressed...
			@Override
			public void actionPerformed(ActionEvent e) {
					try {
						filehandler.FilePicker(model, model2);		    	//...Sends the tables to the FileHandler.java and starts the salary calculations
				}catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		JButton btnDoStuffAnd = new JButton("Save monthly salaries as a .csv file");		//Creates the button for saving the file
		splitPane.setRightComponent(btnDoStuffAnd);						//Adds the saving button to the top panel
		btnDoStuffAnd.addActionListener(new ActionListener() {			//When the file saving button is pressed...
			@Override
			public void actionPerformed(ActionEvent e) {
				filehandler.FileSaver(model2);						//...Goes to the FileSaver of FileHandler.java with the table for the calculated monthly salaries
				
			}
		});
	}

}
