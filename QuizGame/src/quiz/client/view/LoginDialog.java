package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static quiz.Constants.USERNAME_FILE;

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
	private ResourceBundle localization = GameFrame.getLocalization();

	/**
	 * Creates a new LoginDialog.
	 */
	public LoginDialog(GameFrame gameFrame, IControl control, IModel model) {
		this.gameFrame = gameFrame;
		this.control = control;
		this.model = model;

		model.addView(this);
		setTitle(localization.getString("LOGIN"));
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
				gameFrame.showExceptionMessage(localization.getString("INVALID_USERNAME_AND_PW"));
				return;
			}

			else if (status == Status.INVALID_REGISTER_DETAILS) {
				gameFrame.showExceptionMessage(localization.getString("REGISTER_ERROR"));
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
				String register = localization.getString("REGISTER");
				setTitle(register);
				setSize(getWidth(), getHeight() + 60);
				loginLabels[2].setVisible(true);
				repeatPassword.setVisible(true);
				okButton.setText(register);
			} else {
				String login = localization.getString("LOGIN");
				setTitle(login);
				setSize(getWidth(), getHeight() - 60);
				loginLabels[2].setVisible(false);
				repeatPassword.setVisible(false);
				okButton.setText(login);
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

			boolean wrongPassword = false;
			if (pw1.length == pw2.length) {
				for (int i = 0; i < pw1.length; i++) {
					if (pw1[i] != pw2[i]) {
						wrongPassword = true;
						break;
					}
				}
			} else
				wrongPassword = true;

			if(wrongPassword) {
				gameFrame.showExceptionMessage(localization.getString("EXCEPTION_SAME_PW"));
				repeatPassword.setText("");
				return;
			}
		}


		if (keepUsername.isSelected()) {
			try (BufferedWriter bf = Files.newBufferedWriter(USERNAME_FILE, StandardCharsets.UTF_8)) {
				bf.write("");
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
		add(loginLabels[0] = new JLabel(localization.getString("USERNAME") + ":"));
		add(username = new JTextField());

		add(Box.createVerticalGlue());
		add(loginLabels[1] = new JLabel(localization.getString("PASSWORD") + ":"));
		add(password = new JPasswordField());

		add(Box.createVerticalGlue());
		add(loginLabels[2] = new JLabel(localization.getString("REPEAT_PASSWORD") + ":"));
		add(repeatPassword = new JPasswordField());

		loginLabels[2].setVisible(false);
		repeatPassword.setVisible(false);

		GameFrame.setProperties(new Dimension(100, 20), new Dimension(150, 30), new Dimension(200, 40), username,
				password, repeatPassword);

		add(keepUsername = new JCheckBox(localization.getString("KEEP_USERNAME")));
		add(login = new JCheckBox(localization.getString("REGISTER")));
		add(Box.createVerticalGlue());
		add(new JSeparator());
		add(Box.createVerticalGlue());
		add(okButton = new JButton(localization.getString("LOGIN")));
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
}
