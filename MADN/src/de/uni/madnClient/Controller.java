package de.uni.madnClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {
	public MADNSocket socket;

	public Controller() {
		Arguments.mainController = this;
		socket = new MADNSocket();
		socket.start();

	}

	public void startGameloop() {
		while (Arguments.running) {
			String reply;
			while ((reply = socket.getReply()) != null) {
				this.evaluateString(reply);
				Arguments.mainFrame.repaint();
			}

		}
	}

	public void serverLogin(String name) {
		socket.sendString("anmelden;" + name);
	}

	public void joinRoom(String room) {
		socket.sendString("beitreten;" + room);
	}

	public void movePlayer(int place) {
		socket.sendString("ziehen;" + place);
	}
	public void leaveGame() {
		Arguments.fenster.loadActivity(new ActivityLobby()); 
		socket.sendString("spiel_verlassen");
	}
	private void evaluateString(String todo) {
		System.out.println(todo);

		String[] parts = todo.split(";");

		if (parts[0].equals("SPIELE")) {
			String[] games = parts[1].split("[',\\[\\] ]");

			List<String> list = new ArrayList<String>(Arrays.asList(games));
			list.removeAll(Arrays.asList("", null));
			games = (String[]) list.toArray(new String[0]);

			if (games != null)
				Arguments.games = games;
			if (Arguments.fenster.currActivity instanceof ActivityLobby)
				try {
				Arguments.fenster.currActivity.update();
				} catch (Exception e) {
					
				}
			else
				try {
					Arguments.fenster.loadActivity(new ActivityLobby());
				} catch (Exception e) {
					System.err.println("Failed to load ActivityLobby");
				}
	
		}else if (parts[0].startsWith("{")) {
			Arguments.playerPositions = new int[][]{{-100,-101,-102,-103},{-200,-201,-202,-203},{-300,-301,-302,-303},{-400,-401,-402,-403}};
			Arguments.gewuerfelt = new String[]{parts[parts.length - 2], parts[parts.length - 1]};
			String[] playerPairs = parts[0].replace("{", "").replace("}", "").replace(" ", "").split(",");
			System.out.println(Arrays.toString(playerPairs));
			
			int[] counter = { 0, 0, 0, 0 };
			for (String playerPair : playerPairs) {
				String[] pair = playerPair.split(":");
				try {
				int fieldNum = Integer.parseInt(pair[0].trim());
				int playerId = Integer.parseInt(pair[1].trim());
				Arguments.playerPositions[playerId][counter[playerId]++] = fieldNum;
				} catch(Exception e) {
				}
			}
		} else if(parts[0].equals("START")){
			Arguments.gewuerfelt = new String[]{"Niemand","Zahl"};
			Arguments.playerPositions = new int[][]{{-100,-101,-102,-103},{-200,-201,-202,-203},{-300,-301,-302,-303},{-400,-401,-402,-403}};
			Arguments.gewuerfelt = new String[]{parts[parts.length - 3], parts[parts.length - 1]};
		} else if(parts[0].equals("END")){
			try {
				Arguments.fenster.loadActivity(new ActivityLobby());
			} catch (Exception e) {
				System.err.println("Failed to load ActivityLobby");
			}
		}
	}

	public void createRoom(String name, String numberOfPlayer) {
		socket.sendString("neues_spiel;" + name + ";" + numberOfPlayer);
	}

}
