# banking generator framework
# 
# 

from generator_framework import DataGenerator
from faker import Faker
from numpy.random import default_rng, binomial
import datetime
import json
from argparse import ArgumentParser


class BankGenerator(DataGenerator):

    def __init__(self, tuples_per_emit: int=1,
                    norm_dist_1: list=[20,3], 
                    norm_dist_2: list=[1000,100], 
                    binom_dist: list=[1, 0.9]):

        self.tuples_per_emit = tuples_per_emit
        self.fake_gen = Faker()

        self.norm_1 = norm_dist_1
        self.norm_2 = norm_dist_2
        self.binom = binom_dist
    
     
        self.binom_dist = binom_dist

    def emit(self) -> dict:

        # this emits a single dict 
        # TODO - look at refactoring into a generator?
        # 
        # generates the data and makes it into typles

        for i in range(self.tuples_per_emit):

            current_time = datetime.datetime.utcnow().strftime("%Y%m%dT%H%M%S.%fZ")

            card_id = self.fake_gen.credit_card_full(card_type=None)

            dist_1 = default_rng().normal(norm_dist_1[0], norm_dist_1[1], size=self.tuples_per_emit)
            dist_2 = default_rng().normal(norm_dist_2[0], norm_dist_2[1], size=self.tuples_per_emit)
    
            binom_dist = binomial(self.binom_dist[0], self.binom_dist[1])

            if binom_dist != 1:
                value = dist_2
            else:
                value = dist_1

            data_tuple = {'card_identifier': card_id, 'timestamp': current_time, 'value':value}

        return data_tuple
        