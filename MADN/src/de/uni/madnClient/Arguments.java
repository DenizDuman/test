package de.uni.madnClient;

import java.awt.Color;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JFrame;

public class Arguments {
	public static JFrame mainFrame;
	public static Fenster fenster;
	public static String playerName;
	public static String[] games = {};
	public static int[][] fieldLocation = { { -203, -201, -1, -1, 18, 19, 20, -1, -1, -301, -303 },
			{ -202, -200, -1, -1, 17, 48, 21, -1, -1, -300, -302 }, { -1, -1, -1, -1, 16, 49, 22, -1, -1, -1, -1 },
			{ -1, -1, -1, -1, 15, 50, 23, -1, -1, -1, -1 }, { 10, 11, 12, 13, 14, 51, 24, 25, 26, 27, 28 },
			{ 9, 44, 45, 46, 47, -1, 55, 54, 53, 52, 29 }, { 8, 7, 6, 5, 4, 43, 34, 33, 32, 31, 30 },
			{ -1, -1, -1, -1, 3, 42, 35, -1, -1, -1, -1 }, { -1, -1, -1, -1, 2, 41, 36, -1, -1, -1, -1 },
			{ -103, -101, -1, -1, 1, 40, 37, -1, -1, -401, -403 },
			{ -102, -100, -1, -1, 0, 39, 38, -1, -1, -400, -402 } };
	public static Color[] teamColor = { Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN };
	public static int[][] playerPositions = {{-100,-101,-102,-103},{-200,-201,-202,-203},{-300,-301,-302,-303},{-400,-401,-402,-403}};
	public static int fieldSize = 50;
	public static int circleSize = 40; //diameter
	public static int playerSize = 25;
	public static String[] gewuerfelt = {"Niemand","Zahl"};
	public static boolean debug = false;
	public static Controller mainController;
	public static boolean running = true;
}