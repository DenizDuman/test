package de.uni.madnClient;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class Fenster {
	JFrame mainFrame;
	Activity currActivity;

	public Fenster() {
		Arguments.fenster = this;

		this.mainFrame = new JFrame();
		Arguments.mainFrame = this.mainFrame;
		this.mainFrame.setResizable(false);
		this.mainFrame.setPreferredSize(new Dimension(1080, 720));
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setVisible(true);
		mainFrame.addWindowStateListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Arguments.mainController.socket.close();
			}
		});

		this.currActivity = new ActivityLogInScreen();
		this.currActivity.load();
	}

	public void loadActivity(Activity a) {
		this.currActivity.destroy();
		this.currActivity = a;
		a.load();
	}
}
