from .generator_framework import DataGenerator
import datetime
import json
from argparse import ArgumentParser
import numpy as np

# industrial sensor data generator

class SensorGenerator(DataGenerator):

    """

    testing using a full generator

    """

    def __init__(self, batch: int=1):

        self.batch = batch
        self.origin = np.zeros((1, 1))
        self.step_set = [-1, 0, 1]

    def emit(self) -> list:

        while True:
            step_shape = (self.batch, 1)
            steps = np.random.choice(a = self.step_set, size=step_shape)
            path = np.concatenate([self.origin, steps]).cumsum(0)
            self.origin = np.zeros((1,1)) + path[-1:]

            path = path[1:]
            
            output = []
            for i in range(len(path)):
                #print(path[i][0])
                #print(type(path[i][0]))
                # lets dump to json?
                output.append([datetime.datetime.utcnow(), path[i][0]])  

            yield output