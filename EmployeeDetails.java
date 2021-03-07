
/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ItemListener, DocumentListener, WindowListener {
	// decimal format for inactive currency text field
	public static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	// decimal format for active currency text field
	public static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	// hold object start position in file
	public long currentByteStart = 0;
	public RandomFile application = new RandomFile();
	// display files in File Chooser only with extension .dat
	public FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	// hold file name and path for current file in use
	public File file;
	// holds true or false if any changes are made for text fields
	public boolean change = false;
	// holds true or false if any changes are made for file content
	boolean changesMade = false;

	public static EmployeeDetails frame = new EmployeeDetails();
	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	String generatedFileName;
	Employee currentEmployee;
	String[] fullTime = { "", "Yes", "No" };
	EmployeeGUI gui = new EmployeeGUI();
	static EmployeeGUI gui2 = new EmployeeGUI();

	JMenuBar menuBar = gui.menuBar();

	// JMenuItem open = gui.open;
	JMenuItem save = gui.save;

	JMenuItem saveAs = gui.saveAs;

	JMenuItem create = gui.create;

	JMenuItem modify = gui.modify;

	JMenuItem delete = gui.delete;
	JMenuItem firstItem = gui.firstItem;
	JMenuItem lastItem = gui.lastItem;
	JMenuItem nextItem = gui.nextItem;
	JMenuItem prevItem = gui.prevItem;
	JMenuItem searchById = gui.searchById;
	JMenuItem searchBySurname = gui.searchBySurname;
	JMenuItem listAll = gui.listAll;
	JMenuItem closeApp = gui.closeApp;

	JPanel searchPanel = gui.searchPanel();
	JPanel navigPanel = gui.navigPanel();
	JPanel detailsPanel = gui.detailsPanel();
	JPanel buttonPanel = gui.buttonPanel();
	// JTextField searchByIdField = gui.searchByIdField;

	public void createActions() {
		gui.searchByIdField.addActionListener(IdSearchListener);
		gui.searchId.addActionListener(IdSearchListener);
		gui.first.addActionListener(firstRecordListener);
		gui.previous.addActionListener(prevRecordListener);
		gui.next.addActionListener(nextRecordListener);
		gui.last.addActionListener(lastRecordListener);
		gui.displayAll.addActionListener(displayAllListener);
		gui.searchSurname.addActionListener(surnameSearch);
		gui.cancelChange.addActionListener(cancelChangeListener);
		gui.saveChange.addActionListener(saveChangeListener);
		gui.add.addActionListener(addRecordListener);
		gui.edit.addActionListener(modifyRecordListener);
		gui.deleteButton.addActionListener(deleteRecordListener);
		gui.open.addActionListener(openListener);
		gui.save.addActionListener(saveListener);
		gui.saveAs.addActionListener(saveAsListener);
		gui.create.addActionListener(addRecordListener);
		gui.modify.addActionListener(modifyRecordListener);
		gui.delete.addActionListener(deleteRecordListener);
		gui.firstItem.addActionListener(firstRecordListener);
		gui.lastItem.addActionListener(firstRecordListener);
		gui.nextItem.addActionListener(nextRecordListener);
		gui.prevItem.addActionListener(prevRecordListener);
		gui.searchById.addActionListener(IdDialogListener);
		gui.searchBySurname.addActionListener(surnameDialogListener);
		gui.listAll.addActionListener(displayAllListener);
		gui.closeApp.addActionListener(closeListener);

	}

	public void displayRecords(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		gui.searchByIdField.setText("");
		gui.searchBySurnameField.setText("");
		// if Employee is null or ID is 0 do nothing else display Employee
		// details
		if (thisEmployee == null) {
		} else if (thisEmployee.getEmployeeId() == 0) {
		} else {
			// find corresponding gender combo box value to current employee
			while (!found && countGender < gui.gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gui.gender[countGender]))
					found = true;
				else
					countGender++;
			} // end while
			found = false;
			// find corresponding department combo box value to current employee
			while (!found && countDep < gui.department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(gui.department[countDep]))
					found = true;
				else
					countDep++;
			} // end while
			gui.idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			gui.ppsField.setText(thisEmployee.getPps().trim());
			gui.surnameField.setText(thisEmployee.getSurname().trim());
			gui.firstNameField.setText(thisEmployee.getFirstName());
			gui.genderCombo.setSelectedIndex(countGender);
			gui.departmentCombo.setSelectedIndex(countDep);
			gui.salaryField.setText(format.format(thisEmployee.getSalary()));
			// set corresponding full time combo box value to current employee
			if (thisEmployee.getFullTime() == true)
				gui.fullTimeCombo.setSelectedIndex(1);
			else
				gui.fullTimeCombo.setSelectedIndex(2);
		}
		change = false;
	}// end display records

	// find byte start in file for first active record
	public void firstRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for first record
			currentByteStart = application.getFirst();
			// assign current Employee to first record in file
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();// close file for reading
			// if first record is inactive look for next record
			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();// look for next record
		} // end if
	}// end firstRecord

	// find byte start in file for previous active record
	public void previousRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for previous record
			currentByteStart = application.getPrevious(currentByteStart);
			// assign current Employee to previous record in file
			currentEmployee = application.readRecords(currentByteStart);
			// loop to previous record until Employee is active - ID is not 0
			while (currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for previous record
				currentByteStart = application.getPrevious(currentByteStart);
				// assign current Employee to previous record in file
				currentEmployee = application.readRecords(currentByteStart);
			} // end while
			application.closeReadFile();// close file for reading
		}
	}// end previousRecord

	// find byte start in file for next active record
	public void nextRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for next record
			currentByteStart = application.getNext(currentByteStart);
			// assign current Employee to record in file
			currentEmployee = application.readRecords(currentByteStart);
			// loop to previous next until Employee is active - ID is not 0
			while (currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for next record
				currentByteStart = application.getNext(currentByteStart);
				// assign current Employee to next record in file
				currentEmployee = application.readRecords(currentByteStart);
			} // end while
			application.closeReadFile();// close file for reading
		} // end if
	}// end nextRecord

	// find byte start in file for last active record
	public void lastRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for last record
			currentByteStart = application.getLast();
			// assign current Employee to first record in file
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();// close file for reading
			// if last record is inactive look for previous record
			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();// look for previous record
		} // end if
	}// end lastRecord

	// search Employee by ID
	public void searchEmployeeById() {
		boolean found = false;

		try {// try to read correct correct from input
				// if any active Employee record search for ID else do nothing
			if (isSomeoneToDisplay()) {
				firstRecord();// look for first record
				int firstId = currentEmployee.getEmployeeId();
				// if ID to search is already displayed do nothing else loop
				// through records
				if (gui.searchByIdField.getText().trim().equals(gui.idField.getText().trim()))
					found = true;
				else if (gui.searchByIdField.getText().trim()
						.equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(currentEmployee);
				} // end else if
				else {
					nextRecord();// look for next record
					// loop until Employee found or until all Employees have
					// been checked
					while (firstId != currentEmployee.getEmployeeId()) {
						// if found break from loop and display Employee details
						// else look for next record
						if (Integer.parseInt(gui.searchByIdField.getText().trim()) == currentEmployee
								.getEmployeeId()) {
							found = true;
							displayRecords(currentEmployee);
							break;
						} else
							nextRecord();// look for next record
					} // end while
				} // end else
					// if Employee not found display message
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			} // end if
		} // end try
		catch (NumberFormatException e) {
			gui.searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		} // end catch
		gui.searchByIdField.setBackground(Color.WHITE);
		gui.searchByIdField.setText("");
	}// end searchEmployeeByID

	// search Employee by surname
	public void searchEmployeeBySurname() {
		boolean found = false;
		// if any active Employee record search for ID else do nothing
		if (isSomeoneToDisplay()) {
			firstRecord();// look for first record
			String firstSurname = currentEmployee.getSurname().trim();
			// if ID to search is already displayed do nothing else loop through
			// records
			if (gui.searchBySurnameField.getText().trim().equalsIgnoreCase(gui.surnameField.getText().trim()))
				found = true;
			else if (gui.searchBySurnameField.getText().trim()
					.equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				displayRecords(currentEmployee);
			} // end else if
			else {
				nextRecord();// look for next record
				// loop until Employee found or until all Employees have been
				// checked
				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
					// if found break from loop and display Employee details
					// else look for next record
					if (gui.searchBySurnameField.getText().trim()
							.equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						displayRecords(currentEmployee);
						break;
					} // end if
					else
						nextRecord();// look for next record
				} // end while
			} // end else
				// if Employee not found display message
			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		} // end if
		gui.searchBySurnameField.setText("");
	}// end searchEmployeeBySurname

	// get next free ID from Employees in the file
	public int getNextFreeId() {
		int nextFreeId = 0;
		// if file is empty or all records are empty start with ID 1 else look
		// for last active record
		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();// look for last active record
			// add 1 to last active records ID to get next ID
			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}// end getNextFreeId

	// get values from text fields and create Employee object
	public Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) gui.fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(gui.idField.getText()), gui.ppsField.getText().toUpperCase(),
				gui.surnameField.getText().toUpperCase(), gui.firstNameField.getText().toUpperCase(),
				gui.genderCombo.getSelectedItem().toString().charAt(0),
				gui.departmentCombo.getSelectedItem().toString(), Double.parseDouble(gui.salaryField.getText()),
				fullTime);

		return theEmployee;
	}// end getChangedDetails

	// add Employee object to fail
	public void addRecord(Employee newEmployee) {
		// open file for writing
		application.openWriteFile(file.getAbsolutePath());
		// write into a file
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();// close file for writing
	}// end addRecord

	// delete (make inactive - empty) record from file
	public void deleteRecord() {
		if (isSomeoneToDisplay()) {// if any active record in file display
			// message and delete record
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if answer yes delete (make inactive - empty) record
			if (returnVal == JOptionPane.YES_OPTION) {
				// open file for writing
				application.openWriteFile(file.getAbsolutePath());
				// delete (make inactive - empty) record in file proper position
				application.deleteRecords(currentByteStart);
				application.closeWriteFile();// close file for writing
				// if any active record in file display next record
				if (isSomeoneToDisplay()) {
					nextRecord();// look for next record
					displayRecords(currentEmployee);
				} // end if
			} // end if
		} // end if
	}// end deleteDecord

	// create vector of vectors with all Employee details
	public Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();// look for first record
		firstId = currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();// look for next record
		} while (firstId != currentEmployee.getEmployeeId());// end do - while
		currentByteStart = byteStart;

		return allEmployee;
	}// end getAllEmployees

	// activate field for editing
	public void editDetails() {
		// activate field for editing if there is records to display
		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			gui.salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);// enable text fields for editing
		} // end if
	}// end editDetails

	// ignore changes and set text field unenabled
	public void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}// end cancelChange

	// check if any of records in file is active - ID is not 0
	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		// open file for reading
		application.openReadFile(file.getAbsolutePath());
		// check if any of records in file is active - ID is not 0
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();// close file for reading
		// if no records found clear all text fields and display message
		if (!someoneToDisplay) {
			currentEmployee = null;
			gui.idField.setText("");
			gui.ppsField.setText("");
			gui.surnameField.setText("");
			gui.firstNameField.setText("");
			gui.salaryField.setText("");
			gui.genderCombo.setSelectedIndex(0);
			gui.departmentCombo.setSelectedIndex(0);
			gui.fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		return someoneToDisplay;
	}// end isSomeoneToDisplay

	// check for correct PPS format and look if PPS already in use
	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		boolean isNum = false;
		boolean isLet = false;
		// check for correct PPS format based on assignment description
		if (pps.length() == 8 || pps.length() == 9) {
			
			for (int i =0; i< 6; i++) {
			if	(Character.isDigit(pps.charAt(i))){
				isNum=true;
			}
			}

			if ( isNum==true && Character.isLetter(pps.charAt(7)) && (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				// open file for reading
				application.openReadFile(file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();// close file for reading
			} // end if
			else
				ppsExist = true;
		} // end if
		else
			ppsExist = true;

		return ppsExist;
	}// end correctPPS

	// check if file name has extension .dat
	public boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}// end checkFileName

	// check if any changes text field where made
	public boolean checkForChanges() {
		boolean anyChanges = false;
		// if changes where made, allow user to save there changes
		if (change) {
			saveChanges();// save changes
			anyChanges = true;
		} // end if
			// if no changes made, set text fields as unenabled and display
			// current Employee
		else {
			setEnabled(false);
			displayRecords(currentEmployee);
		} // end else

		return anyChanges;
	}// end checkForChanges

	// check for input in text fields

	public boolean checkInput() {
		boolean valid = true;
		// if any of inputs are in wrong format, colour text field and display
		// message
		if (gui.ppsField.isEditable() && gui.ppsField.getText().trim().isEmpty()) {
			gui.ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (gui.ppsField.isEditable() && correctPps(gui.ppsField.getText().trim(), currentByteStart)) {
			gui.ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (gui.surnameField.isEditable() && gui.surnameField.getText().trim().isEmpty()) {
			gui.surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (gui.firstNameField.isEditable() && gui.firstNameField.getText().trim().isEmpty()) {
			gui.firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (gui.genderCombo.getSelectedIndex() == 0 && gui.genderCombo.isEnabled()) {
			gui.genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (gui.departmentCombo.getSelectedIndex() == 0 && gui.departmentCombo.isEnabled()) {
			gui.departmentCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		try {// try to get values from text field
			Double.parseDouble(gui.salaryField.getText());
			// check if salary is greater than 0
			if (Double.parseDouble(gui.salaryField.getText()) < 0) {
				gui.salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} // end if
		} // end try
		catch (NumberFormatException num) {
			if (gui.salaryField.isEditable()) {
				gui.salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} // end if
		} // end catch
		if (gui.fullTimeCombo.getSelectedIndex() == 0 && gui.fullTimeCombo.isEnabled()) {
			gui.fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
			// display message if any input or format is wrong
		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
		// set text field to white colour if text fields are editable
		if (gui.ppsField.isEditable())
			setToWhite();

		return valid;
	}

	// set text field background colour to white
	public void setToWhite() {
		gui.ppsField.setBackground(UIManager.getColor("TextField.background"));
		gui.surnameField.setBackground(UIManager.getColor("TextField.background"));
		gui.firstNameField.setBackground(UIManager.getColor("TextField.background"));
		gui.salaryField.setBackground(UIManager.getColor("TextField.background"));
		gui.genderCombo.setBackground(UIManager.getColor("TextField.background"));
		gui.departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		gui.fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}// end setToWhite

	// enable text fields for editing
	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		gui.ppsField.setEditable(booleanValue);
		gui.surnameField.setEditable(booleanValue);
		gui.firstNameField.setEditable(booleanValue);
		gui.genderCombo.setEnabled(booleanValue);
		gui.departmentCombo.setEnabled(booleanValue);
		gui.salaryField.setEditable(booleanValue);
		gui.fullTimeCombo.setEnabled(booleanValue);
		gui.saveChange.setVisible(booleanValue);
		gui.cancelChange.setVisible(booleanValue);
		gui.searchByIdField.setEnabled(search);
		gui.searchBySurnameField.setEnabled(search);
		gui.searchId.setEnabled(search);
		gui.searchSurname.setEnabled(search);
	}// end setEnabled

	// open file
	public void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save
		// old file
		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();// save file
			} // end if
		} // end if

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open itsoundsoou
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name,
			// delete this file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			file = newFile;// assign opened file to file
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			firstRecord();// look for first record
			displayRecords(currentEmployee);
			application.closeReadFile();// close file for reading
		} // end if
	}// end openFile

	// save file
	public void saveFile() {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			// if changes has been made to text field offer user to save these
			// changes
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {
					// save changes if ID field is not empty
					if (!gui.idField.getText().equals("")) {
						// open file for writing
						application.openWriteFile(file.getAbsolutePath());
						// get changes for current Employee
						currentEmployee = getChangedDetails();
						// write changes to file for corresponding Employee
						// record
						application.changeRecords(currentEmployee, currentByteStart);
						application.closeWriteFile();// close file for writing
					} // end if
				} // end if
			} // end if

			displayRecords(currentEmployee);
			setEnabled(false);
		} // end else
	}// end saveFile

	// save changes to current Employee
	public void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {
			// open file for writing
			application.openWriteFile(file.getAbsolutePath());
			// get changes for current Employee
			currentEmployee = getChangedDetails();
			// write changes to file for corresponding Employee record
			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();// close file for writing
			changesMade = false;// state that all changes has bee saved
		} // end if
		displayRecords(currentEmployee);
		setEnabled(false);
	}// end saveChanges

	// save file as 'save as'
	public void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		// display files only with .dat extension
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// check for file name
			if (!checkFileName(newFile)) {
				// add .dat extension if it was not there
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				// create new file
				application.createFile(newFile.getAbsolutePath());
			} // end id
			else
				// create new file
				application.createFile(newFile.getAbsolutePath());

			try {// try to copy old file to new file
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				// if old file name was generated file name, delete it
				if (file.getName().equals(generatedFileName))
					file.delete();// delete file
				file = newFile;// assign new file to file
			} // end try
			catch (IOException e) {
			} // end catch
		} // end if
		changesMade = false;
	}// end saveFileAs

	// allow to save changes to file when exiting the application
	public void exitApp() {
		// if file is not empty allow to save changes
		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// if user chooses to save file, save file
				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();// save file
					// delete generated file if user saved details to other file
					if (file.getName().equals(generatedFileName))
						file.delete();// delete file
					System.exit(0);// exit application
				} // end if
					// else exit application
				else
					System.exit(0);// exit application
			} // end else
				// else exit application
		} else {
			// delete generated file if user chooses not to save file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			System.exit(0);// exit application
		} // end else
	}// end exitApp

	// generate 20 character long file name
	public String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		// loop until 20 character long file name is generated
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}// end getFileName

	// create file with generated file name when application is opened
	public void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		// assign generated file name to file
		file = new File(generatedFileName);
		// create file
		application.createFile(file.getName());
	}// end createRandomFile

	// content pane for main dialog
	public void createContentPane() {

		setTitle("Employee Details");
		createRandomFile();// create random file name
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar);// add menu bar to frame
		createActions();
		// add search panel to frame
		dialog.add(searchPanel, "width 400:400:400, growx, pushx");
		// add navigation panel to frame
		dialog.add(navigPanel, "width 150:150:150, wrap");
		// add button panel to frame
		dialog.add(buttonPanel, "growx, pushx, span 2,wrap");
		// add details panel to frame
		dialog.add(detailsPanel, "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}// end createContentPane

	// create and show main dialog

	// main method
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui2.createAndShowGUI(frame);
			}
		});
	}// end main

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
		// exit application
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	ActionListener closeListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (checkInput() && !checkForChanges())
				exitApp();

		}
	};
	ActionListener openListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			openFile();

		}
	};
	ActionListener saveListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (checkInput() && !checkForChanges())
				saveFile();

		}
	};
	ActionListener saveAsListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (checkInput() && !checkForChanges())
				saveFileAs();

		}
	};

	ActionListener displayAllListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges())
				if (isSomeoneToDisplay())
					if (isSomeoneToDisplay())
						new EmployeeSummaryDialog(getAllEmloyees());

		}
	};
	ActionListener addRecordListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
				new AddRecordDialog(EmployeeDetails.this);
			}

		}
	};
	ActionListener modifyRecordListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
				editDetails();
			}

		}
	};
	ActionListener saveChangeListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
			}

		}
	};
	ActionListener cancelChangeListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
			}

			cancelChange();
		}
	};

	ActionListener deleteRecordListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
				deleteRecord();
			}

		}
	};
	ActionListener firstRecordListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
				firstRecord();
				displayRecords(currentEmployee);
			}

		}
	};
	ActionListener prevRecordListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
				previousRecord();
				displayRecords(currentEmployee);
			}

		}
	};
	ActionListener nextRecordListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
				nextRecord();
				displayRecords(currentEmployee);
			}

		}
	};

	ActionListener lastRecordListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			if (checkInput() && !checkForChanges()) {
				lastRecord();
				displayRecords(currentEmployee);
			}

		}
	};

	ActionListener IdSearchListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			searchEmployeeById();

		}
	};

	ActionListener IdDialogListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			new SearchDialog(EmployeeDetails.this, "id");

		}
	};

	ActionListener surnameSearch = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			searchEmployeeBySurname();

		}
	};

	ActionListener surnameDialogListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			new SearchDialog(EmployeeDetails.this, "surname");

		}
	};
}// end class EmployeeDetails
