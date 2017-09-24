import algorithm
import configparser
import os

foods = []

def convertfoods(dat):
    n = [0 for i in range(len(foods))]
    for key in dat:
        n[foods.index(key)] += int(dat[key])
    return n

inputdata = "data"

for mainloop in range(1,10):

    #inputdata = "data" + str(mainloop)

    foods = []
    cf = configparser.ConfigParser()
    cf.read(inputdata + "\\foods.cfg")
    for fooditem in cf[cf.sections()[0]]:
        foods.append(fooditem)

    trainingset = []

    for file in os.listdir(inputdata + "\\trainingset\\"):
        cf = configparser.ConfigParser()
        cf.read(inputdata + "\\trainingset\\" + file)
        insulinshot = float(cf["Shot Data"]["shot"])
        startbg = float(cf["BG Data"]["start bg"])
        endbg = float(cf["BG Data"]["end bg"])
        idealinsulin = algorithm.idealinsulin(startbg, endbg, insulinshot)
        mdat = convertfoods(cf["Meal Data"])
        trainingset.append((mdat,idealinsulin))

    testset = []

    for file in os.listdir(inputdata + "\\testset\\"):
        cf = configparser.ConfigParser()
        cf.read(inputdata + "\\testset\\" + file)
        insulinshot = float(cf["Shot Data"]["shot"])
        startbg = float(cf["BG Data"]["start bg"])
        endbg = float(cf["BG Data"]["end bg"])
        idealinsulin = algorithm.idealinsulin(startbg, endbg, insulinshot)
        mdat = convertfoods(cf["Meal Data"])
        testset.append((mdat,idealinsulin))

    params = algorithm.learnparams(trainingset, len(foods), 300, False)
    print(str(mainloop) + "\t" + str(algorithm.geterrornoreg(params, trainingset)) + "\t" + str(algorithm.geterrornoreg(params, testset)))
    break

#params = algorithm.learnparams(trainingset, len(foods))

#print("Learned parameters:")
#for i in range(len(foods)):
#    print(foods[i],int(1000*params[i])/1000.0)


# generate training curves
print("# of test cases\ttraining set error\ttest set error")
for i in range(1,len(trainingset)+1):
    subtrainset = trainingset[0:i]
    params = algorithm.learnparams(subtrainset, len(foods), 300, False)
    print(str(i) + "\t" + str(algorithm.geterrornoreg(params, subtrainset)) + "\t" + str(algorithm.geterrornoreg(params, testset)))
