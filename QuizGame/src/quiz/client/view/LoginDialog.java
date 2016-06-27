package quiz.client.view;

import static quiz.Constants.USERNAME_FILE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;

/**
 * 
 * @author Eric
 * @version 3.05.16
 */
public class LoginDialog extends JDialog implements ItemListener, ActionListener, IView {

	private JCheckBox login, keepUsername;
	private JButton okButton;
	private JTextField username;
	private JPasswordField password, repeatPassword;
	private JLabel[] loginLabels = new JLabel[3];
	private IModel model;
	private IControl control;
	private GameFrame gameFrame;

	/**
	 * Creates a new LoginDialog.
	 */
	public LoginDialog(GameFrame gameFrame, IControl control, IModel model) {
		this.gameFrame = gameFrame;
		this.control = control;
		this.model = model;

		model.addView(this);
		setTitle("Anmelden");
		setModal(true);
		Dimension size = new Dimension(250, 300);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		initComponents();
		initListeners();

		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void onChange(ChangeType type, Status status) {
		if (type == ChangeType.ACCOUNT) {
			if (status == Status.INVALID_LOGIN_DETAILS) {
				exceptionMessage("Ung�ltige Kombination von Benutzername und Passwort! Bitte versuche es erneut!");
				return;
			}

			else if (status == Status.INVALID_REGISTER_DETAILS) {
				exceptionMessage("Es ist ein Fehler bei der Registrierung aufgetreten!");
				return;
			}

			else if (status == Status.SUCCESS) {
				Account user = model.getAccount();

				gameFrame.setUser(user);
				dispose();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getSource() == login) {
			if (login.isSelected()) {
				setTitle("Registrieren");
				setSize(getWidth(), getHeight() + 60);
				loginLabels[2].setVisible(true);
				repeatPassword.setVisible(true);
				okButton.setText("Registrieren");
			} else {
				setTitle("Anmelden");
				setSize(getWidth(), getHeight() - 60);
				loginLabels[2].setVisible(false);
				repeatPassword.setVisible(false);
				okButton.setText("Anmelden");
			}
		} else if (event.getSource() == keepUsername) {
			if (!keepUsername.isSelected()) {
				try {
					Files.deleteIfExists(USERNAME_FILE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (username.getText().isEmpty() || password.getPassword().length == 0
				|| repeatPassword.getPassword().length == 0 && login.isSelected()) {
			return;
		}

		if (login.isSelected()) {
			char[] pw1 = password.getPassword();
			char[] pw2 = repeatPassword.getPassword();

			if (pw1.length == pw2.length) {
				for (int i = 0; i < pw1.length; i++) {
					if (pw1[i] != pw2[i]) {
						exceptionMessage("Bitte verwende zweimal das gleiche Passwort!");
						return;
					}
				}
			} else
				exceptionMessage("Bitte verwende zweimal das gleiche Passwort!");
		}


		if (keepUsername.isSelected()) {
			try (BufferedWriter bf = Files.newBufferedWriter(USERNAME_FILE, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
				bf.write(username.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (login.isSelected())
			control.register(username.getText(), new String(password.getPassword()));
		else
			control.login(username.getText(), new String(password.getPassword()));
	}

	private void initComponents() {
		add(Box.createVerticalGlue());
		add(loginLabels[0] = new JLabel("Benutzername:"));
		add(username = new JTextField());

		add(Box.createVerticalGlue());
		add(loginLabels[1] = new JLabel("Passwort:"));
		add(password = new JPasswordField());

		add(Box.createVerticalGlue());
		add(loginLabels[2] = new JLabel("Passwort wiederholen:"));
		add(repeatPassword = new JPasswordField());

		loginLabels[2].setVisible(false);
		repeatPassword.setVisible(false);

		GameFrame.setProperties(new Dimension(100, 20), new Dimension(150, 30), new Dimension(200, 40), username,
				password, repeatPassword);

		add(keepUsername = new JCheckBox("Benutzernamen merken"));
		add(login = new JCheckBox("Registrieren"));
		add(Box.createVerticalGlue());
		add(new JSeparator());
		add(Box.createVerticalGlue());
		add(okButton = new JButton("Anmelden"));
		getRootPane().setDefaultButton(okButton);
		add(Box.createVerticalGlue());

		GameFrame.setProperties(new Dimension(100, 20), new Dimension(150, 30), new Dimension(200, 40), okButton, login,
				keepUsername, loginLabels[0], loginLabels[1], loginLabels[2]);
		add(Box.createVerticalGlue());

		try {
			if (Files.exists(USERNAME_FILE)) {
				BufferedReader br = Files.newBufferedReader(USERNAME_FILE, StandardCharsets.UTF_8);
				username.setText(br.readLine());
				keepUsername.setSelected(true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initListeners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(1);
			}
		});

		login.addItemListener(this);
		keepUsername.addItemListener(this);
		okButton.addActionListener(this);
	}

	private void exceptionMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Fehler", JOptionPane.ERROR_MESSAGE);
	}
}
