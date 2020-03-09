# from https://towardsdatascience.com/the-poisson-process-everything-you-need-to-know-322aa0ab9e9a
# queuing processing stats generator

# TODO - clean up as a event generator

import random
import math

_lambda = 5
_num_arrivals = 100
_arrival_time = 0

print('RAND,INTER_ARRV_T,ARRV_T')

for i in range(_num_arrivals):
	#Get the next probability value from Uniform(0,1)
	p = random.random()

	#Plug it into the inverse of the CDF of Exponential(_lamnbda)
	_inter_arrival_time = -math.log(1.0 - p)/_lambda

	#Add the inter-arrival time to the running sum
	_arrival_time = _arrival_time + _inter_arrival_time

	#print it all out
	print(str(p)+','+str(_inter_arrival_time)+','+str(_arrival_time))