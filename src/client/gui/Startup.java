package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Startup extends JFrame {
	private JRadioButton rdbtnRememberAll;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField;
	private Properties setting = new Properties();

	public Startup() {
		setIconImage(new ImageIcon(Startup.class.getResource("face1.jpeg"))
				.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("JChat by guyifan");
		setSize(338, 129);
		getContentPane().setLayout(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveSetting();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				saveSetting();
			}
		});

		JLabel lblUsername = new JLabel("username");
		lblUsername.setBounds(12, 12, 80, 15);
		getContentPane().add(lblUsername);

		textField = new JTextField();
		textField.setToolTipText("user name can't contain space or tab");
		textField.setBounds(93, 10, 230, 19);
		getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblHost = new JLabel("host");
		lblHost.setBounds(12, 43, 39, 15);
		getContentPane().add(lblHost);

		textField_1 = new JTextField();
		textField_1.setBounds(51, 41, 162, 19);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);

		JLabel lblPort = new JLabel("port");
		lblPort.setBounds(218, 41, 39, 15);
		getContentPane().add(lblPort);

		textField_2 = new JTextField();
		textField_2.setText("12700");
		textField_2.setBounds(252, 41, 71, 19);
		getContentPane().add(textField_2);
		textField_2.setColumns(10);

		rdbtnRememberAll = new JRadioButton("remember all settings");
		rdbtnRememberAll.setBounds(12, 66, 188, 23);
		getContentPane().add(rdbtnRememberAll);

		JButton btnLogin = new JButton("login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoginClicked();
			}
		});
		btnLogin.setBounds(206, 65, 117, 25);
		getContentPane().add(btnLogin);

		readSetting();
	}

	private void btnLoginClicked() {
		String name = textField.getText().trim();
		if (name.contains(" ") || name.contains("\t")) {
			JOptionPane.showMessageDialog(this,
					"user name can't contain space or tab!", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String host = textField_1.getText().trim();
		int port;
		try {
			port = Integer.parseInt(textField_2.getText().trim());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "invalid port!", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		// TODO login
	}

	public void saveSetting() {
		boolean rememberSetting = rdbtnRememberAll.isSelected();
		setting.setProperty("rememberSetting", rememberSetting + "");
		if (rememberSetting) {
			setting.setProperty("startX", getX() + "");
			setting.setProperty("startY", getY() + "");
			setting.setProperty("username", textField.getText().trim());
			setting.setProperty("host", textField_1.getText().trim());
			setting.setProperty("port", textField_2.getText().trim());
			// TODO set main frame setting
		}
		try {
			setting.store(new FileOutputStream("config.ini"), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readSetting() {
		try {
			setting.load(new FileInputStream("config.ini"));
			if (Boolean.parseBoolean(setting.getProperty("rememberSetting",
					"false"))) {
				textField.setText(setting.getProperty("username", ""));
				textField_1.setText(setting.getProperty("host", ""));
				textField_2.setText(setting.getProperty("port", "12700"));
				rdbtnRememberAll.setSelected(true);
				setBounds(Integer.parseInt(setting.getProperty("startX", "0")),
						Integer.parseInt(setting.getProperty("startY", "0")),
						getWidth(), getHeight());
				// TODO get main frame setting
			}
		} catch (Exception e) {
			return;
		}
	}
}
