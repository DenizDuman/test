package de.uni.madnClient;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelRoom extends JPanel {

	private JLabel lbName;
	private JButton btJoin;

	public PanelRoom(String roomName) {
		super(new GridLayout(1, 3));
		this.setBorder(BorderFactory.createLineBorder(Color.pink));

		lbName = new JLabel(" " + roomName);
		lbName.setBackground(new Color(24, 255, 255));
		this.add(lbName);
		lbName.setOpaque(true);

		JLabel lbSpace = new JLabel();
		lbSpace.setBackground(new Color(24, 255, 255));
		lbSpace.setOpaque(true);
		this.add(lbSpace);

		btJoin = new JButton("Join!");
		btJoin.setBackground(new Color(0, 229, 255));
		btJoin.addActionListener(ae -> {
			Arguments.mainController.joinRoom(roomName);
			Arguments.fenster.loadActivity(new ActivitySpiel());
		});
		this.add(btJoin);

	}
}
