package de.uni.madnClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ActivityLobby extends Activity {

	private JPanel background;
	JTextField tfGameName;
	JLabel lTitle;
	JScrollPane scrollLobbys;
	JComboBox cBGameSize;
	JPanel createContainer;
	JButton createGame;
	public ActivityLobby() {
		super();
	}

	@Override
	public void load() {
		GridBagConstraints c = new GridBagConstraints();

		background = new JPanel();
		background.setLayout(new GridBagLayout());
		background.setBackground(new Color(255, 200, 200));

		mainFrame.add(background);

		lTitle = new JLabel();
		lTitle.setText("Lobby:");
		lTitle.setFont(new Font("ITALIC", 1, 100));

		c.gridx = 1;
		c.gridy = 0;
		// .....................o , l, u, r
		c.insets = new Insets(100, 300, 10, 300);
		background.add(lTitle, c);

		JPanel lobbyContainer = new JPanel();
		lobbyContainer.setLayout(new BoxLayout(lobbyContainer, BoxLayout.Y_AXIS));

		for (String s : Arguments.games) {
			lobbyContainer.add(new PanelRoom(s));
		}

		scrollLobbys = new JScrollPane(lobbyContainer);
		scrollLobbys.setPreferredSize(new Dimension(500, 300));

		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		background.add(scrollLobbys, c);
		
		JPanel createContainer = new JPanel();
		createContainer.setLayout(new BoxLayout(createContainer, BoxLayout.X_AXIS));
		
		
		tfGameName = new JTextField(35);
		tfGameName.setBackground(new Color(255, 255, 255));
		tfGameName.requestFocusInWindow(); 
		createContainer.add(tfGameName);
		String[] comboBoxListe = {"2","3","4"};
		cBGameSize = new JComboBox(comboBoxListe);
		createContainer.add(cBGameSize);
		
		
		JButton createGame = new JButton("Create!");
		createGame.addActionListener(ae -> {Arguments.mainController.createRoom(tfGameName.getText(), cBGameSize.getSelectedItem().toString());});
		createContainer.add(createGame);
		
		tfGameName.addKeyListener(new KeyAdapter() { 
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					Arguments.mainController.createRoom(tfGameName.getText(), cBGameSize.getSelectedItem().toString());
				}
			}
		});
		
		c.gridx = 1;
		c.gridy = 3;
		background.add(createContainer,c);

		

		mainFrame.pack();
		tfGameName.requestFocus();
	}

	@Override
	public void destroy() {
		mainFrame.remove(background);

	}

}
