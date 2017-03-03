package de.uni.madnClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JPanel;

public class SpielbrettPanel extends JPanel {

	public SpielbrettPanel() {
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					int fieldNum = Arguments.fieldLocation[e.getY() / Arguments.fieldSize][e.getX() / Arguments.fieldSize];
					if(fieldNum < 0 || fieldNum > 55)
						fieldNum = -1;
					Arguments.mainController.movePlayer(fieldNum);
				} catch (Exception ex) {

				}

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()){
					case KeyEvent.VK_ESCAPE: Arguments.mainController.leaveGame();break;
				}
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		
		
		});
	}

	private Point getFieldPosition(int i) {
		for (int row = 0; row < Arguments.fieldLocation.length; row++) {
			for (int col = 0; col < Arguments.fieldLocation[row].length; col++) {
				if (Arguments.fieldLocation[row][col] == i)
					return new Point(col * Arguments.fieldSize, row * Arguments.fieldSize);
			}
		}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		paintLines(g2d);
		paintFields(g2d);
		paintPlayers(g2d);
		paintWuerfelText(g2d);

	}

	private void paintLines(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		Point lastEl = getFieldPosition(39);
		for (int i = 0; i < 40; i++) {
			Point curPoint = getFieldPosition(i);

			Rectangle line = new Rectangle(lastEl);
			line.add(curPoint);

			if (line.width == 0) {
				line.width = 2;
				line.x -= 1;
			} else {
				line.height = 2;
				line.y -= 1;
			}

			line.x += Arguments.fieldSize / 2;
			line.y += Arguments.fieldSize / 2;

			g2d.fillRect(line.x, line.y, line.width, line.height);
			lastEl = curPoint;
		}
	}

	private void paintFields(Graphics2D g2d) {
		for (int row = 0; row < Arguments.fieldLocation.length; row++) {
			for (int col = 0; col < Arguments.fieldLocation[0].length; col++) {
				int curF = Arguments.fieldLocation[row][col];
				if (curF < -1) {
					g2d.setColor(Arguments.teamColor[curF / -100 - 1]);
				} else if (curF <= 55 && curF >= 40) {
					g2d.setColor(Arguments.teamColor[(curF - 40) / 4]);
				} else if (curF % 10 == 0) {
					g2d.setColor(Arguments.teamColor[curF / 10]);
				} else {
					g2d.setColor(Color.WHITE);
				}
				if (curF != -1) {
					g2d.fillOval(col * Arguments.fieldSize + (Arguments.fieldSize - Arguments.circleSize) / 2,
							row * Arguments.fieldSize + (Arguments.fieldSize - Arguments.circleSize) / 2,
							Arguments.circleSize, Arguments.circleSize);
					g2d.setColor(Color.BLACK);
					g2d.setStroke(new BasicStroke(3));
					g2d.drawOval(col * Arguments.fieldSize + (Arguments.fieldSize - Arguments.circleSize) / 2,
							row * Arguments.fieldSize + (Arguments.fieldSize - Arguments.circleSize) / 2,
							Arguments.circleSize, Arguments.circleSize);
				}

				if (Arguments.debug) {
					g2d.setColor(Color.BLACK);
					g2d.drawString("" + curF, col * Arguments.fieldSize + 10, row * Arguments.fieldSize + 20);
				}
			}
		}
	}

	private void paintPlayers(Graphics2D g2d) {
		for (int row = 0; row < Arguments.playerPositions.length; row++) {
			for (int col = 0; col < Arguments.playerPositions[0].length; col++) {
				Point curPoint = getFieldPosition(Arguments.playerPositions[row][col]);
				g2d.setColor(Arguments.teamColor[row]);
				g2d.fillOval(curPoint.x + Arguments.playerSize / 2, curPoint.y + Arguments.playerSize / 2,
						Arguments.playerSize, Arguments.playerSize);
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(5));
				g2d.drawOval(curPoint.x + Arguments.playerSize / 2, curPoint.y + Arguments.playerSize / 2,
						Arguments.playerSize, Arguments.playerSize);

			}
		}
	}

	private void paintWuerfelText(Graphics2D g2d) {
		g2d.drawString(Arguments.gewuerfelt[0] + " hat eine " +Arguments.gewuerfelt[1] + " gewuefelt!", 12* Arguments.fieldSize , 6 * Arguments.fieldSize);
	}
}