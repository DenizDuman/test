package de.uni.madnClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ActivityLogInScreen extends Activity {

	private JPanel background;
	JTextField tfName;
	JLabel lTitle;
	JButton btSubmit;

	public ActivityLogInScreen() {
		super();
	}

	public void load() {
		GridBagConstraints c = new GridBagConstraints();
		// c.weightx = 1;
		// c.weighty = 1;
		// c.anchor = GridBagConstraints.CENTER;

		background = new JPanel();
		background.setLayout(new GridBagLayout());
		background.setBackground(new Color(200, 200, 255));

		mainFrame.add(background);

		lTitle = new JLabel();
		lTitle.setText("Log In");
		lTitle.setFont(new Font("ITALIC", 1, 100));

		c.gridx = 1;
		c.gridy = 0;
		// .....................o , l, u, r
		c.insets = new Insets(100, 300, 10, 300);
		background.add(lTitle, c);

		tfName = new JTextField(20);
		tfName.setBackground(new Color(255, 255, 255));
		
		tfName.addKeyListener(new KeyAdapter() { 
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					logIn();
				}
			}
		});
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		background.add(tfName, c);

		btSubmit = new JButton("Submit");
		btSubmit.addActionListener(ae -> {
			logIn();
		});

		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		background.add(btSubmit, c);

		mainFrame.pack();
		
		tfName.requestFocus();

		


	}

	public void destroy() {
		this.mainFrame.remove(background);
	}
	
	private void logIn() {
		Arguments.playerName = tfName.getText();
		Arguments.mainController.serverLogin(Arguments.playerName);
		Arguments.fenster.loadActivity(new ActivityLobby());
	}
}
