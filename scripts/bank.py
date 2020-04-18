# generate something that looks like a fake card transaction
# keywords / time / credit card / person

from faker import Faker
from numpy.random import default_rng, binomial
import datetime
import json
from argparse import ArgumentParser
from generators import BankGenerator

def make_parser():
    """
    
    argument parser for executing the script in different ways

    """

    parser = ArgumentParser(description="Create dummy data for streaming examples")
    parser.add_argument('--tuples-per-emit', '-t', type=int, default=1,
                            help='number of tuples to emit at once')

    return parser


def main(args):

    """

    We should make a fake client list then do transactions from clients only?
      - make a fake client list
      - make fake cards and assign them to clients 1+ cards per client?

      - make transactions!

    """

    generator_module = BankGenerator(tuples_per_emit = args.tuples_per_emit)
    
    print(json.dumps(generator_module.emit()))


if __name__ == '__main__':

    parser = make_parser()
    args = parser.parse_args()

    main(args)