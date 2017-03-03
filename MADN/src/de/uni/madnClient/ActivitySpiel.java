package de.uni.madnClient;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ActivitySpiel extends Activity{
	JPanel spielbrett;
	@Override
	public void load() {
		
		spielbrett = new SpielbrettPanel();
		mainFrame.add(spielbrett);
		spielbrett.setFocusable(true);
		spielbrett.requestFocusInWindow();
		mainFrame.pack();
	}

	@Override
	public void destroy() {
		mainFrame.remove(spielbrett);
	}
	
}
