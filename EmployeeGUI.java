import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

public class EmployeePanels {

	
    Font font1 = new Font("SansSerif", Font.BOLD, 16);
boolean change= false;


JMenuBar menuBar = new JMenuBar();
JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

    public JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
    searchBySurname, listAll, closeApp;
public JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
    saveChange, cancelChange;
public JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
public JTextField idField, ppsField, surnameField, firstNameField, salaryField, searchByIdField, searchBySurnameField;
String[] gender = { "", "M", "F" };
String[] fullTime = { "", "Yes", "No" };
String[] department = { "", "Administration", "Production", "Transport", "Management" };
//EmployeeDetails ed = new EmployeeDetails();



public JMenuBar menuBar() {
	JMenuBar menuBar = new JMenuBar();
	//JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

	fileMenu = new JMenu("File");
	fileMenu.setMnemonic(KeyEvent.VK_F);
	recordMenu = new JMenu("Records");
	recordMenu.setMnemonic(KeyEvent.VK_R);
	navigateMenu = new JMenu("Navigate");
	navigateMenu.setMnemonic(KeyEvent.VK_N);
	closeMenu = new JMenu("Exit");
	closeMenu.setMnemonic(KeyEvent.VK_E);

	menuBar.add(fileMenu);
	menuBar.add(recordMenu);
	menuBar.add(navigateMenu);
	menuBar.add(closeMenu);

	fileMenu.add(open = new JMenuItem("Open"));
	//.addActionListener(openListener);
	open.setMnemonic(KeyEvent.VK_O);
	open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	fileMenu.add(save = new JMenuItem("Save"));
	//.addActionListener(saveListener);
	save.setMnemonic(KeyEvent.VK_S);
	save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	fileMenu.add(saveAs = new JMenuItem("Save As"));
	//.addActionListener(saveAsListener);
	saveAs.setMnemonic(KeyEvent.VK_F2);
	saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

	recordMenu.add(create = new JMenuItem("Create new Record"));
	//.addActionListener(addRecordListener);
	create.setMnemonic(KeyEvent.VK_N);
	create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	recordMenu.add(modify = new JMenuItem("Modify Record"));
	//.addActionListener(modifyRecordListener);
	modify.setMnemonic(KeyEvent.VK_E);
	modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
	recordMenu.add(delete = new JMenuItem("Delete Record"));
	//.addActionListener(deleteRecordListener);

	navigateMenu.add(firstItem = new JMenuItem("First"));
	
	navigateMenu.add(prevItem = new JMenuItem("Previous"));
	
	navigateMenu.add(nextItem = new JMenuItem("Next"));
	
	navigateMenu.add(lastItem = new JMenuItem("Last"));
	
	navigateMenu.addSeparator();
	navigateMenu.add(searchById = new JMenuItem("Search by ID"));
	//.addActionListener(IdSearchListener);
	navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname"));
	navigateMenu.add(listAll = new JMenuItem("List all Records"));

	closeMenu.add(closeApp = new JMenuItem("Close"));
	closeApp.setMnemonic(KeyEvent.VK_F4);
	closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));
	
	


	return menuBar;
}// end menuBar




	public JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
		searchPanel.add(searchByIdField = new JTextField(20), "width 200:200:200, growx, pushx");
		
		searchByIdField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(searchId = new JButton(new ImageIcon(
				new ImageIcon("imgres.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		
		searchId.setToolTipText("Search Employee By ID");

		searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
		searchPanel.add(searchBySurnameField = new JTextField(20), "width 200:200:200, growx, pushx");
		
		searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(
				searchSurname = new JButton(new ImageIcon(new ImageIcon("imgres.png").getImage()
						.getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		
		searchSurname.setToolTipText("Search Employee By Surname");
	

		
		

		return searchPanel;
	}

	
	public JPanel navigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		navigPanel.add(first = new JButton(new ImageIcon(
				new ImageIcon("first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		
		first.setToolTipText("Display first Record");

		navigPanel.add(previous = new JButton(new ImageIcon(new ImageIcon("previous.png").getImage()
				.getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		previous.setPreferredSize(new Dimension(17, 17));
		
		previous.setToolTipText("Display previous Record");

		navigPanel.add(next = new JButton(new ImageIcon(
				new ImageIcon("next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		next.setPreferredSize(new Dimension(17, 17));
	
		next.setToolTipText("Display next Record");

		navigPanel.add(last = new JButton(new ImageIcon(
				new ImageIcon("last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		last.setPreferredSize(new Dimension(17, 17));
		
		last.setToolTipText("Display last Record");
		
		
		
		
		
		
		
		

		return navigPanel;
	}
	
	public static void createAndShowGUI(EmployeeDetails frame) {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();// add content pane to frame
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}// end createAndShowGUI
	
	public JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), "growx, pushx");
		
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), "growx, pushx");
		
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), "growx, pushx, wrap");
	
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), "growx, pushx");
		
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}
	public JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), "growx, pushx");
		empDetails.add(idField = new JTextField(20), "growx, pushx, wrap");
		idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), "growx, pushx");
		empDetails.add(ppsField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Surname:"), "growx, pushx");
		empDetails.add(surnameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("First Name:"), "growx, pushx");
		empDetails.add(firstNameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Gender:"), "growx, pushx");
		empDetails.add(genderCombo = new JComboBox<String>(gender), "growx, pushx, wrap");

		empDetails.add(new JLabel("Department:"), "growx, pushx");
		empDetails.add(departmentCombo = new JComboBox<String>(department), "growx, pushx, wrap");

		empDetails.add(new JLabel("Salary:"), "growx, pushx");
		empDetails.add(salaryField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), "growx, pushx");
		empDetails.add(fullTimeCombo = new JComboBox<String>(fullTime), "growx, pushx, wrap");

		buttonPanel.add(saveChange = new JButton("Save"));
		//saveChange.addActionListener(saveChangeListener);
		saveChange.setVisible(false);
		saveChange.setToolTipText("Save changes");
		buttonPanel.add(cancelChange = new JButton("Cancel"));
		//cancelChange.addActionListener(cancelChangeListener);
		cancelChange.setVisible(false);
		cancelChange.setToolTipText("Cancel edit");

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

		// loop through panel components and add listeners and format
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
					field.setDocument(new JTextFieldLimit(20));
				//field.getDocument().addDocumentListener(this);
			} // end if
			else if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
				empDetails.getComponent(i).setEnabled(false);
			//	((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {
					// set foregroung to combo boxes
					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}// end paint
				});
			} // end else if
		} // end for
		return empDetails;
	}// end detailsPanel
	

	

	
	
	
	
	
	
}
