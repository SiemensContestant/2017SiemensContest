#!/usr/bin/python3
import algorithm
import random
import os

# ([name], [real ratio], [carbs in 1 serving])

foodnames = ["Milk",
             "Multigrain Cereal",
             "Grapefruit",
             "Waffle",
             "PB&J Sandwich",
             "Lays Chips", # 15 g
             "Pita Chips",
             "Apple",
             "Banana",
             "Chicken Tenders",
             "Slice of Bread",
             "Slice of Pizza",
             "Tortilla",
             "White Rice",
             "Brown Rice",
             "Spaghetti",
             "Piece of Baguette",
             "Fried Cod",
             "Ice Cream Cone",
             "Ice Cream Bar",
             "Fruit Bar",
             "Smoothie"]

foodmealfreq = [(1.0,0.3,0.9),
                (1.0,0.0,0.0),
                (0.9,0.05,0.05),
                (1.0,0.05,0.05),
                (0.0,0.6,0.0),
                (0.0,0.3,0.1),
                (0.0,0.85,0.2),
                (0.2,0.7,0.3),
                (0.1,0.4,0.2), #banana
                (0.0,0.5,0.4),
                (0.1,0.2,0.2),
                (0.0,0.3,0.3), #pizza
                (0.0,0.1,0.2), 
                (0.0,0.05,0.4),
                (0.0,0.05,0.4),
                (0.0,0.15,0.3),
                (0.0,0.2,0.2), #baguette
                (0.0,0.3,0.4),
                (0.0,0.05,0.05),
                (0.0,0.0,0.2),
                (0.0,0.0,0.1),
                (0.0,0.0,0.1)]
                
             
foodrealratios = [1.0,
                  1.4,
                  0.9,
                  1.2,
                  1.0,
                  1.0,
                  1.0,
                  1.1,
                  1.8,
                  0.9,
                  1.2,
                  1.4,
                  1.1,
                  2.1,
                  1.2,
                  1.2,
                  1.0,
                  1.0,
                  2.0,
                  1.7,
                  1.2,
                  1.1]

    
foodservingsizes = [(13,13),
                    (30,40),
                    (14,14),
                    (13,14),
                    (30,32),
                    (15,15),
                    (29,29),
                    (25,25),
                    (27,27),
                    (15,20),
                    (11,12),
                    (36,43),
                    (18,22),
                    (30,45),
                    (30,45),
                    (45,60),
                    (10,15),
                    (30,45),
                    (23,25),
                    (22,24),
                    (9,9),
                    (25,30)]

def displayfoodinfo():
    for i in range(len(foodnames)):
        print(foodnames[i] + "\n\tratio is " + str(foodrealratios[i]) +
              "\n\t\tstandard serving size is " + str(foodservingsizes[i]) +
              "\n\t\t\ttimes of day are " + str(foodmealfreq[i]))


#ideal carb range for a meal is 80-120
def generatemeal(which): #which is 0 -> breakfast, 1-> lunch, 2-> dinner
    numcarbs = 0
    while(numcarbs < 50): 
        mealitems = []
        #first choose foods
        for i in range(len(foodmealfreq)):
            if(random.random() < foodmealfreq[i][which]):
                mealitems.append(i)
        meal = []
        #now choose amount of each
        for mitem in mealitems:
            servings = random.randint(1,3)/2.0 #0.5, 1.0, or 1.5
            amount = int(servings * random.randint(*foodservingsizes[mitem])) #usually int amounts
            meal.append((mitem,amount))
        numcarbs = 0
        for i in meal:
            numcarbs += i[1]
        while(numcarbs > 120):
            meal.pop(random.randint(0,len(meal)-1))
            numcarbs = 0
            for i in meal:
                numcarbs += i[1]
    return meal

trainingsetsize = 70 # represents ~3 weeks worth of data
testsetsize = 30

dataoutput = "data_10f"

def main(num = -1):

    if(num != -1):
        dataoutput = "data" + str(num)

    ## randomize the ratios

    for i in range(len(foodrealratios)):
        foodrealratios[i] = random.randint(75,205)/100.0

    ## okay done with that


    curmeal = 0
    trainingset = []
    for i in range(trainingsetsize):
        trainingset.append(generatemeal(curmeal))
        curmeal = (curmeal+1)%3
        
    testset = []
    for i in range(testsetsize):
        testset.append(generatemeal(curmeal))
        curmeal = (curmeal+1)%3

    if not os.path.isdir(dataoutput):
        os.makedirs(dataoutput)
    else:
        print("Already exists!")
        return
    f = open(dataoutput + "\\actual_ratios.cfg","w")
    f.write("[Food Ratios]\n")
    for i in range(len(foodnames)):
        f.write(foodnames[i] + ": " + str(foodrealratios[i]) + "\n")
    f.close()

    f = open(dataoutput + "\\foods.cfg","w")
    f.write("[Foods]\n")
    for i in range(len(foodnames)):
        f.write(foodnames[i] + ": \n")
    f.close()
    os.makedirs(dataoutput + "\\trainingset")
    for j in range(len(trainingset)): #datapt in trainingset:
        datapt = trainingset[j]
        # need to create some insulin injection data
        totaleffcarbs = 0
        for i in range(len(datapt)):
            totaleffcarbs += datapt[i][1] * foodrealratios[datapt[i][0]]
        idealinsulin = totaleffcarbs / algorithm.myfactor # for now the latter is hardcoded to 7 (mine)
        startbg = random.randint(80,150)
        endbg = 0
        myinsulin = 0
        while(endbg < 70 or endbg > 200): #want reasonable data
            startbg = random.randint(80,150)
            myinsulin = int(idealinsulin + ((random.random()*4)-2.0))
            diff = idealinsulin - myinsulin # if positive, we undershot
            endbg = startbg + (50*diff) # -> blood sugar ends up higher
            endbg += random.randint(-15,15) # little bit of random variation
            #(note this is about the precision of glucose reader)
            # other way around if we overshot and it's negative
        tfname = "meal"
        if(j < 10):
            tfname += "0" + str(j)
        else:
            tfname += str(j)
        f = open(dataoutput + "\\trainingset\\" + tfname + ".cfg","w")
        f.write("[Meal Data]\n")
        for item in datapt:
            f.write(foodnames[item[0]] + ": " + str(item[1]) + "\n")
        f.write("[Shot Data]\nShot: " + str(myinsulin) + "\n")
        f.write("[BG Data]\nStart BG: " + str(startbg) + "\nEnd BG: " + str(endbg) + "\n")
        f.close()
        
    os.makedirs(dataoutput + "\\testset")
    for j in range(len(testset)): #datapt in trainingset:
        datapt = testset[j]
        # need to create some insulin injection data
        totaleffcarbs = 0
        for i in range(len(datapt)):
            totaleffcarbs += datapt[i][1] * foodrealratios[datapt[i][0]]
        idealinsulin = totaleffcarbs / algorithm.myfactor # for now the latter is hardcoded to 7 (mine)
        startbg = random.randint(80,150)
        endbg = 0
        myinsulin = 0
        while(endbg < 70 or endbg > 200): #want reasonable data
            startbg = random.randint(80,150)
            myinsulin = int(idealinsulin + ((random.random()*4)-2.0))
            diff = idealinsulin - myinsulin # if positive, we undershot
            endbg = startbg + (50*diff) # -> blood sugar ends up higher
            # other way around if we overshot and it's negative
        tfname = "meal" + str(len(trainingset)+j)
        f = open(dataoutput + "\\testset\\" + tfname + ".cfg","w")
        f.write("[Meal Data]\n")
        for item in datapt:
            f.write(foodnames[item[0]] + ": " + str(item[1]) + "\n")
        f.write("[Shot Data]\nShot: " + str(myinsulin) + "\n")
        f.write("[BG Data]\nStart BG: " + str(startbg) + "\nEnd BG: " + str(endbg) + "\n")
        f.close()
        
if __name__ == "__main__":
    main()
