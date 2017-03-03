# -*- encoding: utf-8 -*-
import random

class Game:
	def __init__(self, name, number_of_players):
		self.current_player = 0
		self.name = name
		self.number_of_players = number_of_players
		self.player_list = []
		self.players_out_of_game = []
		self.field = dict()
		# for i in range(44, 47):
		# 	self.field[i] = 1
		# self.field[9] = 1
		self.current_die_number = random.randint(1, 6)
		# self.current_die_number = 1

	def add_player(self, player):
		self.player_list.append(player)

	def remove_player(self, player):
		#self.player_list.remove(player)
		self.players_out_of_game.append(self.player_list.index(player))
		for x in self.field.keys():
			if self.field[x] == self.player_list.index(player):
				del self.field[x]
		if self.current_player == self.player_list.index(player):
			self.set_next_player()
			self.roll_die()
			return True
		return False

	def roll_die(self):
		self.current_die_number = random.randint(1, 6)
		# self.current_die_number = 1

	def set_next_player(self):
		if self.current_die_number != 6:
			self.current_player += 1
			if self.current_player >= self.number_of_players:
				self.current_player -= self.number_of_players
			while self.current_player in self.players_out_of_game:
				self.current_player += 1
				if self.current_player >= self.number_of_players:
					self.current_player -= self.number_of_players

	def has_won(self):
		for p in range(0, self.number_of_players):
			won = True
			for i in range(40 + p * 4, 44 + p * 4):
				if i not in self.field:
					print(str(i) + " not in field")
					won = False
			print(str(p) + " " + str(won))
			if won:
				return self.player_list[p].name
		return None



	def move(self, field):
		# return bool: erfolgreich gesetzt = True 
		can_move = []
		figures = 0
		for x in self.field.keys():
			if self.field[x] == self.current_player:
				figures += 1
				if self.current_player == 0:
					new_field = x + self.current_die_number
					if new_field >= 44:
						continue
					elif new_field > 39 and new_field < 44:
						found_one = True
						for i in range(max(x + 1, 40), new_field + 1):
							if i in self.field:
								found_one = False
						if found_one:
							can_move.append(x)
					elif new_field not in self.field or new_field in self.field and self.field[new_field] != self.current_player:
						can_move.append(x)
				else:
					new_field = x + self.current_die_number
					if new_field > 39 and x < 40:
						new_field -= 40
					if x < self.current_player * 10 and new_field >= self.current_player * 10 or x > 40:
						house_start_pos = 39 + self.current_player * 4
						if x < 40:
							house_pos = new_field - (self.current_player * 10 - 1)
						else:
							house_pos = x - house_start_pos + self.current_die_number
						print("new house_pos: " + str(house_start_pos) + " " + str(house_pos))
						if house_pos > 4:
							continue
						# do not jump
						found_one = True
						for i in range(max(house_start_pos + 1, x + 1), house_start_pos + house_pos + 1):
							if i in self.field:
								found_one = False
						if found_one:
							can_move.append(x)
					else:
						if new_field not in self.field or new_field in self.field and self.field[new_field] != self.current_player:
							can_move.append(x)
		if figures < 4 and self.current_die_number == 6:
			if self.current_player * 10 not in self.field or self.current_player * 10 in self.field and self.field[current_player * 10] != self.current_player:
				can_move.append(-1)

		print(can_move)

		# Player cannot move - accept everything and move on
		if len(can_move) == 0:
			self.set_next_player()
			self.roll_die()
			return True


		if field in self.field:
			# Player tried to set figur that does not exist
			if self.field[field] != self.current_player:
				return False

			# Player on starting place, moveable, but not chosen
			if self.current_player * 10 in self.field and self.field[self.current_player * 10] == self.player_list[self.current_player] and field != self.current_player * 10 and (self.current_player * 10 + self.current_die_number not in self.field or self.current_player * 10 + self.current_die_number in self.field and self.field[self.current_player * 10 + self.current_die_number] != self.player_list[self.current_player]):
				return False

			# has to kick someone out
			has_to_kick = []
			can_move = []
			for x in self.field.keys():
				if x > 39:
					continue
				if self.field[x] == self.current_player:
					new_field = x + self.current_die_number
					if new_field > 40:
						if self.current_player == 0:
							continue
						else:
							new_field -= 40
					if new_field >= self.current_player * 10 and x < self.current_player * 10:
						continue
					if new_field in self.field and self.field[new_field] != self.current_player:
						has_to_kick.append(field)

			new_field = field + self.current_die_number
			if new_field > 39: 
				# Player 0 goes into house
				if self.current_player != 0 or field > 40:
					new_field -= 40
				else:
					if new_field >= 44:
						return False
					# do not jump over each other in house
					for i in range(max(40, field + 1), new_field + 1):
						if i in self.field:
							return False

			# other players move into house
			if (new_field > self.current_player * 10 - 1 and field <= self.current_player * 10 - 1 or field > 40) and self.current_player != 0:
				house_start_pos = 39 + self.current_player * 4
				if field < 40:
					house_pos = new_field - (self.current_player * 10 - 1)
				else:
					house_pos = field - house_start_pos + self.current_die_number
				if house_pos > 4:
					return False
				# do not jump
				for i in range(max(house_start_pos + 1, field + 1), house_start_pos + house_pos + 1):
					if i in self.field:
						return False
				new_field = house_start_pos + house_pos

			# move!
			del self.field[field]
			self.field[new_field] = self.current_player

			if field not in has_to_kick:
				for x in has_to_kick:
					del self.field[x]

			self.set_next_player()
			self.roll_die()
			return True

		elif field == -1:
			figures = 0
			for x in self.field.keys():
				if self.field[x] == self.current_player:
					figures += 1
			if figures >= 4:
				return False

			if self.current_die_number != 6 and figures < 4:
				return False

			if self.current_player * 10 in self.field and self.field[self.current_player * 10] == self.current_player:
				return False

			self.field[self.current_player * 10] = self.current_player
			self.roll_die()
			return True

		else:
			return False
