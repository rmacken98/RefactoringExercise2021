
/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements WindowListener {
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
	JPanel searchPanel = gui.searchPanel();
	JPanel navigPanel = gui.navigPanel();
	JPanel detailsPanel = gui.detailsPanel();
	JPanel buttonPanel = gui.buttonPanel();

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

		if (thisEmployee != null && thisEmployee.getEmployeeId() != 0) {
			// find corresponding gender combo box value to current employee
			while (!found && countGender < gui.gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gui.gender[countGender]))
					found = true;
				else
					countGender++;
			}

			found = false;
			// find corresponding department combo box value to current employee
			while (!found && countDep < gui.department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(gui.department[countDep]))
					found = true;
				else
					countDep++;
			}
			gui.idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			gui.ppsField.setText(thisEmployee.getPps().trim());
			gui.surnameField.setText(thisEmployee.getSurname().trim());
			gui.firstNameField.setText(thisEmployee.getFirstName());
			gui.genderCombo.setSelectedIndex(countGender);
			gui.departmentCombo.setSelectedIndex(countDep);
			gui.salaryField.setText(format.format(thisEmployee.getSalary()));
			if (thisEmployee.getFullTime() == true)
				gui.fullTimeCombo.setSelectedIndex(1);
			else
				gui.fullTimeCombo.setSelectedIndex(2);
		}
		change = false;
	}

	public void firstRecord() {

		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getFirst();

			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();

			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();
		}
	}

	public void previousRecord() {

		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getPrevious(currentByteStart);

			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {

				currentByteStart = application.getPrevious(currentByteStart);

				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	public void nextRecord() {

		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getNext(currentByteStart);

			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {

				currentByteStart = application.getNext(currentByteStart);

				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	public void lastRecord() {
		if (isSomeoneToDisplay()) {
			application.openReadFile(file.getAbsolutePath());
			currentByteStart = application.getLast();
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();
			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();
		}
	}

	public void search(JTextField field1, JTextField field2, String type) {
		boolean found = false;

		try {

			if (isSomeoneToDisplay()) {
				firstRecord();

				// if ID to search is already displayed do nothing else loop
				// through records
				if (field1.getText().trim().equals(field2.getText().trim()))
					found = true;
				else if (field1.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(currentEmployee);
				} // end else if
				else {
					nextRecord();

					searchLoops(type, found, field1);
				}

				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			}
		} catch (NumberFormatException e) {
			field1.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		}
		field1.setBackground(Color.WHITE);
		field1.setText("");
	}

	public void searchLoops(String type, boolean found, JTextField field1) {
		int firstId = currentEmployee.getEmployeeId();

		if (type.equalsIgnoreCase("id"))
			while (firstId != currentEmployee.getEmployeeId()) {

				if (Integer.parseInt(field1.getText().trim()) == currentEmployee.getEmployeeId()) {
					found = true;
					displayRecords(currentEmployee);
					break;
				} else
					nextRecord();
			}

		else {
			while (!field1.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {

				if (gui.searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
					found = true;
					displayRecords(currentEmployee);
					break;
				} else
					nextRecord();

			}

		}

	}

	public Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;

		int ID = Integer.parseInt(gui.idField.getText());
		String pps = gui.ppsField.getText().toUpperCase();
		String surname = gui.surnameField.getText().toUpperCase();
		String firstName = gui.firstNameField.getText().toUpperCase();

		char gender = gui.genderCombo.getSelectedItem().toString().charAt(0);
		String department = gui.departmentCombo.getSelectedItem().toString();
		Double salary = Double.parseDouble(gui.salaryField.getText());

		if (((String) gui.fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(ID, pps, surname, firstName, gender, department, salary, fullTime);

		return theEmployee;
	}

	public void addRecord(Employee newEmployee) {

		application.openWriteFile(file.getAbsolutePath());

		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();
	}

	public void deleteRecord() {
		if (isSomeoneToDisplay()) {

			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

			if (returnVal == JOptionPane.YES_OPTION) {

				application.openWriteFile(file.getAbsolutePath());

				application.deleteRecords(currentByteStart);
				application.closeWriteFile();

				if (isSomeoneToDisplay()) {
					nextRecord();
					displayRecords(currentEmployee);
				}
			}
		}
	}

	// create vector of vectors with all Employee details
	public Vector<Object> getAllEmloyees() {

		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();
		firstId = currentEmployee.getEmployeeId();

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
			nextRecord();
		} while (firstId != currentEmployee.getEmployeeId());// end do - while
		currentByteStart = byteStart;

		return allEmployee;
	}

	public void editDetails() {

		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			gui.salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);
		}
	}

	public void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}

	// check if any of records in file is active - ID is not 0
	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		application.openReadFile(file.getAbsolutePath());
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();// close file for reading
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
	}

	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		boolean isNum = false;
		boolean isLet = false;
		if (pps.length() == 8 || pps.length() == 9) {

			for (int i = 0; i < 6; i++) {
				if (Character.isDigit(pps.charAt(i))) {
					isNum = true;
				}
			}

			if (isNum == true && Character.isLetter(pps.charAt(7))
					&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				application.openReadFile(file.getAbsolutePath());
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();// close file for reading
			} else
				ppsExist = true;
		} else
			ppsExist = true;

		return ppsExist;
	}

	public boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}

	public boolean checkForChanges() {
		boolean anyChanges = false;
		// if changes where made, allow user to save there changes
		if (change) {
			saveChanges();
			anyChanges = true;
		} else {
			setEnabled(false);
			displayRecords(currentEmployee);
		}

		return anyChanges;
	}

	public boolean checkInput() {
		boolean valid = true;

		if (gui.ppsField.isEditable() && gui.ppsField.getText().trim().isEmpty()) {
			gui.ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (gui.ppsField.isEditable() && correctPps(gui.ppsField.getText().trim(), currentByteStart)) {
			gui.ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (gui.surnameField.isEditable() && gui.surnameField.getText().trim().isEmpty()) {
			gui.surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (gui.firstNameField.isEditable() && gui.firstNameField.getText().trim().isEmpty()) {
			gui.firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (gui.genderCombo.getSelectedIndex() == 0 && gui.genderCombo.isEnabled()) {
			gui.genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (gui.departmentCombo.getSelectedIndex() == 0 && gui.departmentCombo.isEnabled()) {
			gui.departmentCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		try {
			Double.parseDouble(gui.salaryField.getText());

			if (Double.parseDouble(gui.salaryField.getText()) < 0) {
				gui.salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			}
		} catch (NumberFormatException num) {
			if (gui.salaryField.isEditable()) {
				gui.salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			}
		}
		if (gui.fullTimeCombo.getSelectedIndex() == 0 && gui.fullTimeCombo.isEnabled()) {
			gui.fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");

		if (gui.ppsField.isEditable())
			setToWhite();

		return valid;
	}

	public void setToWhite() {
		gui.ppsField.setBackground(UIManager.getColor("TextField.background"));
		gui.surnameField.setBackground(UIManager.getColor("TextField.background"));
		gui.firstNameField.setBackground(UIManager.getColor("TextField.background"));
		gui.salaryField.setBackground(UIManager.getColor("TextField.background"));
		gui.genderCombo.setBackground(UIManager.getColor("TextField.background"));
		gui.departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		gui.fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}

	public int getNextFreeId() {
		int nextFreeId = 0;

		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();
			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}

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
	}

	public void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		fc.setFileFilter(datfilter);
		File newFile;

		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();
			}
		}

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();

			if (file.getName().equals(generatedFileName))
				file.delete();
			file = newFile;

			application.openReadFile(file.getAbsolutePath());
			firstRecord();
			displayRecords(currentEmployee);
			application.closeReadFile();
		}
	}

	public void saveFile() {

		if (file.getName().equals(generatedFileName))
			saveFileAs();
		else {

			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

				if (returnVal == JOptionPane.YES_OPTION) {

					if (!gui.idField.getText().equals("")) {

						application.openWriteFile(file.getAbsolutePath());

						currentEmployee = getChangedDetails();

						application.changeRecords(currentEmployee, currentByteStart);
						application.closeWriteFile();
					}
				}
			}

			displayRecords(currentEmployee);
			setEnabled(false);
		}
	}

	public void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if (returnVal == JOptionPane.YES_OPTION) {

			application.openWriteFile(file.getAbsolutePath());

			currentEmployee = getChangedDetails();

			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();
			changesMade = false;
		}
		displayRecords(currentEmployee);
		setEnabled(false);
	}

	public void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");

		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			if (!checkFileName(newFile)) {

				newFile = new File(newFile.getAbsolutePath() + ".dat");

				application.createFile(newFile.getAbsolutePath());
			} else

				application.createFile(newFile.getAbsolutePath());

			try {
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				if (file.getName().equals(generatedFileName))
					file.delete();
				file = newFile;
			} catch (IOException e) {
			}
		}
		changesMade = false;
	}

	public void exitApp() {

		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();

					if (file.getName().equals(generatedFileName))
						file.delete();
					System.exit(0);
				}

				else
					System.exit(0);
			}

		} else {

			if (file.getName().equals(generatedFileName))
				file.delete();
			System.exit(0);
		}
	}

	public String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();

		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}

	public void createRandomFile() {
		generatedFileName = getFileName() + ".dat";

		file = new File(generatedFileName);

		application.createFile(file.getName());
	}

	public void createContentPane() {

		setTitle("Employee Details");
		createRandomFile();
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar);
		createActions();

		dialog.add(searchPanel, "width 400:400:400, growx, pushx");
		dialog.add(navigPanel, "width 150:150:150, wrap");
		dialog.add(buttonPanel, "growx, pushx, span 2,wrap");
		dialog.add(detailsPanel, "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}

	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui2.createAndShowGUI(frame);
			}
		});
	}

	public void windowClosing(WindowEvent e) {
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

			search(gui.searchByIdField, gui.idField, "ID");

		}
	};

	ActionListener IdDialogListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			new SearchDialog(EmployeeDetails.this, "id");

		}
	};

	ActionListener surnameSearch = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			search(gui.searchBySurnameField, gui.surnameField, "surname");

		}
	};

	ActionListener surnameDialogListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {

			new SearchDialog(EmployeeDetails.this, "surname");

		}
	};
}
