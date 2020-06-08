package minesweeper.frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import minesweeper.frame.CustomGameInfo.OpenPoint;
import java.awt.event.ActionListener;

public class CustomGameDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField xfield;
	private JTextField yfield;
	private JTextField minefield;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();

	private CustomGameInfo customGameInfo;
	private JRadioButton rdbtnLeftup;
	private JRadioButton rdbtnCenter;
	private JCheckBox openInSafeCheckBox;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CustomGameDialog dialog = new CustomGameDialog(new CustomGameInfo(30,16,99, OpenPoint.CENTER, true));
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CustomGameDialog(CustomGameInfo info) {
		customGameInfo = info;
		
		setBounds(100, 100, 299, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("X:");
		lblNewLabel.setBounds(5, 8, 42, 13);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		xfield = new JTextField();
		xfield.setBounds(59, 5, 96, 19);
		xfield.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Y:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(5, 39, 42, 13);
		
		yfield = new JTextField();
		yfield.setBounds(59, 34, 96, 19);
		yfield.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Mine:");
		lblNewLabel_2.setBounds(5, 76, 42, 13);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		minefield = new JTextField();
		minefield.setBounds(59, 73, 96, 19);
		minefield.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Open:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_3.setBounds(5, 114, 42, 13);
		
		openInSafeCheckBox = new JCheckBox("OpenInSafe");
		openInSafeCheckBox.setBounds(5, 149, 125, 21);
		
		rdbtnLeftup = new JRadioButton("leftup");
		rdbtnLeftup.setBounds(59, 110, 96, 21);
		buttonGroup.add(rdbtnLeftup);
		
		rdbtnCenter = new JRadioButton("center");
		rdbtnCenter.setBounds(155, 110, 113, 21);
		buttonGroup.add(rdbtnCenter);
		contentPanel.setLayout(null);
		contentPanel.add(lblNewLabel);
		contentPanel.add(xfield);
		contentPanel.add(lblNewLabel_1);
		contentPanel.add(yfield);
		contentPanel.add(lblNewLabel_2);
		contentPanel.add(minefield);
		contentPanel.add(lblNewLabel_3);
		contentPanel.add(rdbtnLeftup);
		contentPanel.add(rdbtnCenter);
		contentPanel.add(openInSafeCheckBox);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				okButton.setAction(action);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setAction(action_1);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		initParams();
	}

	private void initParams() {
		xfield.setText(String.valueOf(customGameInfo.getX()));
		yfield.setText(String.valueOf(customGameInfo.getY()));
		minefield.setText(String.valueOf(customGameInfo.getMine()));
		switch (customGameInfo.getOpenPoint()) {
		case CENTER: 
			rdbtnCenter.setSelected(true);break;
		case LEFTUP:
			rdbtnLeftup.setSelected(true);break;
		}
		openInSafeCheckBox.setSelected(customGameInfo.isSafeOpen());
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "OK");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			CustomGameInfo.OpenPoint p = null;
			if (rdbtnLeftup.isSelected()) {
				p = OpenPoint.LEFTUP;
			} else if (rdbtnCenter.isSelected()) {
				p = OpenPoint.CENTER;
			}
			
			customGameInfo.setParameters(
					Integer.parseInt(xfield.getText()), 
					Integer.parseInt(yfield.getText()), 
					Integer.parseInt(minefield.getText()), 
					p, openInSafeCheckBox.isSelected());
			CustomGameDialog.this.setVisible(false);
		}
	}
	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			CustomGameDialog.this.setVisible(false);
			initParams();
		}
	}
}
