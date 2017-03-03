# -*- encoding: utf-8 -*-

class Player:
	def __init__(self, socket, name):
		self.socket = socket
		self.name = name
		self.game = None

	def set_game(self, game):
		self.game = game