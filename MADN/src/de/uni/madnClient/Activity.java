package de.uni.madnClient;

import javax.swing.JFrame;

public abstract class Activity {
	protected JFrame mainFrame;

	public Activity() {
		this.mainFrame = Arguments.mainFrame;
	}

	public abstract void load();

	public abstract void destroy();
	
	public void update(){
		this.destroy();
		this.load();
	}
}
