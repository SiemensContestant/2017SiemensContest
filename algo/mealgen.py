#!/usr/bin/python3
import random
import algorithm as algo
import os

mealRatios = {}

with open('ratios.txt', 'r') as fin:
	for line in fin:
		a = line.split()
		mealRatios[' '.join(a[:-1])] = float(a[-1])

def main():
	for m in range(1, 6):
		os.makedirs('meals{:d}'.format(m))
		for i in range(1, 51):
			with open('meals{:d}/meal{:d}.cfg'.format(m, i), 'w') as f:
				f.write("[Food Data]\n")
				ecarbs = 0
				carbs = 0
				for key in mealRatios:
					if random.random() <= 0.9:
						a = int(random.gauss(15, 10))
						carbs += a
						ecarbs += mealRatios[key] * a
						f.write("{}: {:f}\n".format(key, a))
				ideal_shot = ecarbs / 10
				shot = carbs / 10
				f.write('[Shot Data]\nShot: {:.5f}\n'.format(shot))
				b = (shot - ideal_shot)*50
				f.write("[Blood Sugar]\n")
				startbg = random.gauss(140, 20)
				f.write("Start BG: {:.5f}\n".format(startbg))
				f.write("End BG: {:.5f}\n".format(startbg + b))

main()			
