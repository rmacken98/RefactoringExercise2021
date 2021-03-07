/*
 * 
 * This is a dialog for adding new Employees and saving records to file
 * 
 * */

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class AddRecordDialog extends JDialog implements ActionListener {
	JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	JButton save, cancel;
	EmployeeDetails parent;

	// constructor for add record dialog
	public AddRecordDialog(EmployeeDetails parent) {
		setTitle("Add Record");
		setModal(true);
		this.parent = parent;
		this.parent.setEnabled(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(dialogPane());
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(save);

		setSize(500, 370);
		setLocation(350, 250);
		setVisible(true);
	}// end AddRecordDialog

	// initialize dialog container
	public Container dialogPane() {
		JPanel empDetails, buttonPanel;
		empDetails = new JPanel(new MigLayout());
		buttonPanel = new JPanel();
		String JLabelConstraint = "growx, pushx";
		String ComponentConstraint = "growx, pushx, wrap";

		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), JLabelConstraint);
		empDetails.add(idField = new JTextField(20), ComponentConstraint);
		idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), JLabelConstraint);
		empDetails.add(ppsField = new JTextField(20), ComponentConstraint);

		empDetails.add(new JLabel("Surname:"), JLabelConstraint);
		empDetails.add(surnameField = new JTextField(20), ComponentConstraint);

		empDetails.add(new JLabel("First Name:"), JLabelConstraint);
		empDetails.add(firstNameField = new JTextField(20), ComponentConstraint);

		empDetails.add(new JLabel("Gender:"), JLabelConstraint);
		empDetails.add(genderCombo = new JComboBox<String>(this.parent.gender), ComponentConstraint);

		empDetails.add(new JLabel("Department:"), JLabelConstraint);
		empDetails.add(departmentCombo = new JComboBox<String>(this.parent.department), ComponentConstraint);

		empDetails.add(new JLabel("Salary:"), JLabelConstraint);
		empDetails.add(salaryField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), JLabelConstraint);
		empDetails.add(fullTimeCombo = new JComboBox<String>(this.parent.fullTime), ComponentConstraint);

		buttonPanel.add(save = new JButton("Save"));
		save.addActionListener(this);
		save.requestFocus();
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);

		empDetails.add(buttonPanel, "span 2, " + ComponentConstraint);
		// loop through all panel components and add fonts and listeners
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(this.parent.font1);
			if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
			} // end if
			else if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
					field.setDocument(new JTextFieldLimit(20));
			} // end else if
		} // end for
		idField.setText(Integer.toString(this.parent.getNextFreeId()));
		return empDetails;
	}

	public String getFieldText(JTextField t) {
		return t.getText().toUpperCase();
	}

	// add record to file
	public void addRecord() {
		boolean fullTime = false;
		Employee theEmployee;

		int EmpId = Integer.parseInt(idField.getText());
		String pps = getFieldText(ppsField);
		String surname = getFieldText(surnameField);
		String firstName = getFieldText(firstNameField);
		char gender = genderCombo.getSelectedItem().toString().charAt(0);
		String department = departmentCombo.getSelectedItem().toString();
		Double salary = Double.parseDouble(salaryField.getText());
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;
		// create new Employee record with details from text fields
		theEmployee = new Employee(EmpId, pps, surname, firstName, gender, department, salary, fullTime);
		;
		this.parent.currentEmployee = theEmployee;
		this.parent.addRecord(theEmployee);
		this.parent.displayRecords(theEmployee);
	}

	public void isFieldEmpty(JTextField field, Boolean v) {

		if (field.getText().isEmpty()) {
			field.setBackground(new Color(255, 150, 150));
			v = false;
		}
	}

	public void isComboFieldEmpty(JComboBox field, Boolean v) {

		if (field.getSelectedIndex() == 0) {
			field.setBackground(new Color(255, 150, 150));
			v = false;
		}
	}

	// check for input in text fields
	public boolean checkInput() {
		boolean valid = true;

		isFieldEmpty(ppsField, valid);
		if (this.parent.correctPps(this.ppsField.getText().trim(), -1)) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		isFieldEmpty(surnameField, valid);
		isFieldEmpty(firstNameField, valid);
		isComboFieldEmpty(genderCombo, valid);
		isComboFieldEmpty(departmentCombo, valid);

		try {// try to get values from text field
			Double salary = Double.parseDouble(salaryField.getText());
			// check if salary is greater than 0
			if (salary < 0) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} // end if
		} // end try
		catch (NumberFormatException num) {
			salaryField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		// end catch
		isComboFieldEmpty(fullTimeCombo, valid);

		return valid;
	}// end checkInput

	// set text field to white colour
	public void setToWhite() {
		ppsField.setBackground(Color.WHITE);
		surnameField.setBackground(Color.WHITE);
		firstNameField.setBackground(Color.WHITE);
		salaryField.setBackground(Color.WHITE);
		genderCombo.setBackground(Color.WHITE);
		departmentCombo.setBackground(Color.WHITE);
		fullTimeCombo.setBackground(Color.WHITE);
	}// end setToWhite

	// action performed
	public void actionPerformed(ActionEvent e) {
		// if chosen option save, save record to file
		if (e.getSource() == save) {
			// if inputs correct, save record
			if (checkInput()) {
				addRecord();// add record to file
				dispose();// dispose dialog
				this.parent.changesMade = true;
			} // end if
				// else display message and set text fields to white colour
			else {
				JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
				setToWhite();
			} // end else
		} // end if
		else if (e.getSource() == cancel)
			dispose();// dispose dialog
	}// end actionPerformed
}// end class AddRecordDialog