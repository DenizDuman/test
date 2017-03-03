# -*- encoding: utf-8 -*-

import socket
from Queue import *
import threading
from player import *
from game import *

# TODO
# Spiele werden aktuell nur gelöscht, wenn sie fertig sind oder wenn alle Spieler ein angefangenes Spiel verlassen haben
# dict string can be decoded by json [replace ' with "] or yaml or eval or ast.literal_eval
# ziehen bevor spiel angefangen geht nicht
# 




import signal
import sys
import time
import select

class Server:

	ERROR_wrong_number_of_arguments = "ERROR falsche Anzahl Argumente"
	ERROR_not_assigned_player = "ERROR nicht angemeldet"
	ERROR_invalid_number_of_players = "ERROR Spieleranzahl ungueltig"
	ERROR_game_does_not_exist = "ERROR Spiel existiert nicht"
	ERROR_invalid_move = "ERROR Zug ungueltig"
	ERROR_invalid_command = "ERROR Nachricht ungueltig"
	ERROR_game_name_already_exists = "ERROR Spielname existiert bereits"
	ERROR_not_your_turn = "ERROR Du bist nicht dran"
	ERROR_name_already_exists = "ERROR Name existiert bereits"
	ACK_message = "ACK"


	def __init__(self):
		# game organization stuff
		self.player = dict() # (conn, player), game can be None
		self.open_games = dict() # (name, game)
		self.closed_games = dict() # (name, game)

		# communication stuff
		signal.signal(signal.SIGINT, self.signal_handler)
		self.to_send = Queue() # (conn, message)
		self.to_do = Queue()
		self.socket_list = []
		self.stopped = False

		self.TCP_IP = '192.168.43.66'
		self.TCP_PORT = 5005
		self.BUFFER_SIZE = 1024  # Normally 1024, but we want fast response
		 
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.s.bind((self.TCP_IP, self.TCP_PORT))
		print "started"
		self.s.listen(1)

		self.receive_thread = threading.Thread(target=self.receive)
		self.receive_thread.setDaemon(True)
		self.receive_thread.start()

		self.send_thread = threading.Thread(target=self.send)
		self.send_thread.setDaemon(True)
		self.send_thread.start()

		self.work_thread = threading.Thread(target=self.work)
		self.work_thread.setDaemon(True)
		self.work_thread.start()

		while True:
			conn, addr = self.s.accept()
			print 'Connection address:', addr
			self.socket_list.append(conn)


	''' stop server on Ctrl+C '''
	def signal_handler(self, signal, frame):
		print('You pressed Ctrl+C!')
		self.s.close()
		sys.exit(0)

	def send(self):
		while True:
			data = self.to_send.get()
			conn = data[0]
			message = data[1]
			if conn in self.socket_list:
				print("sending: " + message)
				conn.send(message + "\r\n")

	def receive(self):
		while True:
			ready = select.select(self.socket_list, [self.s], self.socket_list, 1)
			# print("Ready-list: " + str(ready))
			for conn in ready[0]:
				data = conn.recv(self.BUFFER_SIZE)
				if data:
					# if data == "close":
					# 	conn.send("close")
					# 	break
					print "received data:", data
					self.to_do.put((data, conn))
				else:
					if conn in self.player:
						game = self.player[conn].game
						if game != None:
							if game.name in self.open_games.keys():
								game.player_list.remove(self.player[conn])
							else:
								if game.remove_player(self.player[conn]):
									if len(game.players_out_of_game) == game.number_of_players:
										del self.closed_games[game.name]
									else:
										self.send_game_status_message(self.player[conn].game)
						print("removing player " + self.player[conn].name)
						del self.player[conn]
					conn.close()
					self.socket_list.remove(conn)
					

		conn.close()

	def send_current_game_player_list(self, game):
		message = "PLAYER" + ";"
		for player in game.get_player_list():
			message += player.name + ";"
		for player in game.player_list:
			self.to_send.put((player.socket, message[:-1]))

	def send_open_game_list(self):
		message = "SPIELE" + ";" + str(self.open_games.keys()) 
		for x in self.player.keys():
			if self.player[x].game == None:
				self.to_send.put((self.player[x].socket, message))

	def send_game_status_message(self, game):
		message = str(game.field) + ";" + str(game.player_list[game.current_player].name) + ";" + str(game.current_die_number)
		for player in game.get_player_list():
			self.to_send.put((player.socket, message))

	def send_game_start_message(self, game):
		message = "START" + ";"
		for player in game.get_player_list():
			message += player.name + ";"
		message += str(game.current_die_number)
		for player in game.player_list:
			self.to_send.put((player.socket, message))

	def broadcast_winner(self, game, winner):
		message = "END" + ";" + str(game.field) + ";" + winner
		for player in game.get_player_list():
			self.to_send.put((player.socket, message))

	def work(self):
		while True:
			data = self.to_do.get()
			conn = data[1]
			message = data[0].strip().split(";")
			messagetype = message[0]

			if conn not in self.player.keys() and messagetype != "anmelden":
				self.to_send.put((conn, self.ERROR_not_assigned_player))

			elif messagetype == "anmelden" and conn not in self.player.keys():
				# add new player to list
				if len(message) == 2:
					name_ok = True
					for p in self.player.keys():
						if self.player[p].name == message[1]:
							self.to_send.put((conn, self.ERROR_name_already_exists))
							name_ok = False
					if name_ok:
						new_player = Player(conn, message[1])
						self.player[conn] = new_player
						message = "SPIELE" + ";" + str(self.open_games.keys()) 
						self.to_send.put((conn, message))
				else:
					self.to_send.put((conn, self.ERROR_wrong_number_of_arguments))

			elif messagetype == "neues_spiel":
				if len(message) == 3:
					try:
						number_of_players = int(message[2])
						if number_of_players <= 1:
							raise ValueError
						game_name = message[1]
						if game_name in self.open_games or game_name in self.closed_games:
							self.to_send.put((conn, self.ERROR_game_name_already_exists))
						else:
							new_game = Game(game_name, number_of_players)
							self.open_games[game_name] = new_game
							self.send_open_game_list()
					except ValueError:
						self.to_send.put((conn, self.ERROR_invalid_number_of_players))
				else:
					self.to_send.put((conn, self.ERROR_wrong_number_of_arguments))

			elif messagetype == "beitreten":
				if len(message) == 2:
					game_name = message[1]
					try:
						# add player to game
						game = self.open_games[game_name]
						game.add_player(self.player[conn])
						# add game to player
						self.player[conn].set_game(game)
						# send current player list to all players
						self.send_current_game_player_list(game)
						# start game if number of players is full, game is now closed
						if game.number_of_players == len(game.player_list):
							# put game to closed games
							del self.open_games[game_name]
							self.closed_games[game_name] = game
							# start game
							self.send_game_start_message(game)
							# inform other players about current open games
							self.send_open_game_list()
					except KeyError:
						self.to_send.put((conn, self.ERROR_game_does_not_exist))
				else:
					self.to_send.put((conn, self.ERROR_wrong_number_of_arguments))

			elif messagetype == "ziehen":
				if len(message) == 2:
					try:
						field_to_go = int(message[1])
						game = self.player[conn].game
						if self.player[conn] != game.player_list[game.current_player]:
							self.to_send.put((conn, self.ERROR_not_your_turn))

						elif game.move(field_to_go):
							# check for end of game
							winner = game.has_won()
							if winner != None:
								self.broadcast_winner(game, winner)
								# put players back to "lobby"
								for player in game.get_player_list():
									player.game = None
									game.remove_player(self.player[conn])
									# if game is now empty, close it
									if len(game.players_out_of_game) == game.number_of_players:
										del self.closed_games[game.name]
									# inform players about current open games, but only players from finished game
									message = "SPIELE" + ";" + str(self.open_games.keys()) 
									self.to_send.put((conn, message))
							else:
								self.send_game_status_message(game)
						else:
							self.to_send.put((conn, self.ERROR_invalid_move))
					except ValueError:
						self.to_send.put((conn, self.ERROR_invalid_move))
				else:
					self.to_send.put((conn, self.ERROR_wrong_number_of_arguments))

			elif messagetype == "spiel_verlassen":
				if len(message) == 1:
					if self.player[conn].game != None:
						# remove player from game
						game = self.player[conn].game
						if game.name in self.open_games.keys():
							game.player_list.remove(self.player[conn])
						else:
							if game.remove_player(self.player[conn]):
								if len(game.players_out_of_game) == game.number_of_players:
									del self.closed_games[game.name]
								else:
									self.send_game_status_message(game)
						self.player[conn].game = None
						message = "SPIELE" + ";" + str(self.open_games.keys()) 
						self.to_send.put((conn, message))

					else:
						self.to_send.put((conn, self.ERROR_game_does_not_exist))
				else:
					self.to_send.put((conn, self.ERROR_wrong_number_of_arguments))

			elif messagetype == "quit" or messagetype == "close" or messagetype == "bye":
				if len(message) == 1:
					if self.player[conn].game != None:
						# remove player from game
						game = self.player[conn].game
						if game.name in self.open_games.keys():
							game.player_list.remove(self.player[conn])
						else:
							if game.remove_player(self.player[conn]):
								if len(game.players_out_of_game) == game.number_of_players:
									del self.closed_games[game.name]
								else:
									self.send_game_status_message(game)
						self.player[conn].game = None

					# delete player from server 
					del self.player[conn]
					conn.close()
					self.socket_list.remove(conn)

				else:
					self.to_send.put((conn, self.ERROR_wrong_number_of_arguments))

			else:
				self.to_send.put((conn, self.ERROR_invalid_command))

s = Server()