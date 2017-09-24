#!/usr/bin/python3

LEARNING_RATE = 0.002
REGULARIZATION_COEFFICIENT = 1.0

myfactor = 7 #temporarily hard-coded

# predicts the effective number of carbs
def predict(params, meal):
    total = 0
    for i in range(len(meal)):
        total += params[i] * meal[i]
    return total

def geterrornoreg(params, data):
    error = 0.0
    for dpoint in data:
        expectedins = predict(params, dpoint[0])/myfactor
        error += ((expectedins - dpoint[1])**2) # squared error
    #return 0.5 * error/len(data)
    return (0.5 / len(data)) * (error)


def geterror(params, data):
    error = 0.0
    for dpoint in data:
        expectedins = predict(params, dpoint[0])/myfactor
        error += ((expectedins - dpoint[1])**2) # squared error
    #return 0.5 * error/len(data)
    return (0.5 / len(data)) * (error + getregularizationerror(params, REGULARIZATION_COEFFICIENT))

def getregularizationerror(params, regcoeff):
    # regularize around 1.0
    regerror = 0.0
    for param in params:
        regerror += (param-1.0)**2
    return regcoeff * regerror

def getregularizationdelta(params, regcoeff):
    regdelta = [0.0 for i in params]
    for paramnum in range(len(params)):
        regdelta[paramnum] += 2 * regcoeff * (params[paramnum]-1.0)
    return regdelta

def getparamdelta(params, data):
    paramdelta = [0.0 for i in params]
    for paramnum in range(len(params)):
        for dpoint in data:
            expectedins = predict(params, dpoint[0])/myfactor
            paramdelta[paramnum] += dpoint[0][paramnum] * (expectedins - dpoint[1])
    regdelta = getregularizationdelta(params, REGULARIZATION_COEFFICIENT)
    totaldelta = [paramdelta[i] + regdelta[i] for i in range(len(params))]
    return [x/len(data) for x in totaldelta]
    #return [x/len(data) for x in paramdelta]

# returns the ideal 
def idealinsulin(startbg, endbg, shot, correctionfactor=50):
    return shot + (endbg-startbg)/(correctionfactor)

def learnparams(data, numfoods=-1, its=200, printStuff=True, learningrate = 0.002):
    if(numfoods == -1):
        numfoods = len(data[0][0])
    params = [1.0 for i in range(numfoods)]
    if(printStuff):
        print("Iteration,Error")
    lasterror = float("inf")
    lastparams = [x for x in params]
    for i in range(its):
        #print("Iteration",i)
        #print("Params:",params)
        if(printStuff):
            print(str(i) + "," + str(geterror(params, data)))
        t_error = geterror(params, data)
        if(t_error > lasterror):
            return lastparams
        lasterror = t_error
        lastparams = [x for x in params]
        
        paramdelta = getparamdelta(params, data)
        for i in range(len(params)):
            params[i] = params[i] - learningrate * paramdelta[i]
    return params
    

#numfoods = 3#10


#theta = [1.0 for i in range(numfoods)]


# data is stored as list of tuples, where each tuple element contains
# a data point, index 0 being a tuple representing carbs of each food
# type (index in that tuple is the food ID), or meal data, and index 1
# being the ideal number of units of insulin shot


#data = [((19, 24, 0), 5.25), ((0, 10, 0), 1.0), ((0, 0, 11), 2.2),
#        ((0, 0, 20), 4.0), ((22, 0, 25), 8.3), ((18, 16, 0), 4.3),
#        ((20, 14, 0), 4.4), ((15, 6, 8), 4.45), ((0, 0, 6), 1.2),
#        ((10, 9, 22), 6.8), ((17, 0, 0), 2.55), ((6, 0, 23), 5.5),
#        ((12, 0, 0), 1.8), ((11, 23, 23), 8.55), ((0, 13, 0), 1.3),
#        ((6, 16, 0), 2.5), ((9, 0, 10), 3.35), ((0, 16, 0), 1.6),
#        ((20, 17, 0), 4.7), ((19, 22, 0), 5.05)]

#ratio for 0: 1.5
#ratio for 1: 1.0
#ratio for 2: 2.0
