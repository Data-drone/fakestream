###
# Python App to emit data
# use logger to write to logs for other apps to pick up
# what if I reuse the other files?
# make a generic class that we can call on startup?
# 

import logging
import logging.handlers
from generators.generator_bank import BankGenerator
from argparse import ArgumentParser
import json

#TODO
# construct logger object
# with log rotation
# csv?

# call generators
# and emit
LOG_FILENAME = 'log/data.log'

logger = logging.getLogger('bank_generator')
logger.setLevel(logging.INFO)
fh = logging.handlers.RotatingFileHandler(LOG_FILENAME, maxBytes=10*1024*1024, backupCount=5)
fh.setLevel(logging.INFO)
formatter = logging.Formatter('%(message)s')
fh.setFormatter(formatter)

logger.addHandler(fh)

def make_parser():

    """
    argument parser for main function
    """

    parser = ArgumentParser(description="Create dummy data for streaming examples")
    parser.add_argument('--tuples-per-emit', '-t', type=int, default=1,
                            help='number of tuples to emit at once')

    return parser




if __name__ == '__main__':

    parser = make_parser()
    args = parser.parse_args()
    
    new_generator = BankGenerator(tuples_per_emit=args.tuples_per_emit)
    stop = False

    while(stop!=True):

        results = new_generator.emit()
        
        for result in results:
            logger.info(json.dumps(result))
        #logger.info(results)