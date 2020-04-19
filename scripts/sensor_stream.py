from generators import SensorGenerator
from argparse import ArgumentParser
import datetime
import json
from confluent_kafka import Producer
import socket



def make_parser():
    """

    argument parser for controlling the number of streams

    """

    parser = ArgumentParser(description="Create dummy sensor stream esque data")
    parser.add_argument('--tuples-per-emit', '-t', type=int, default=1,
                            help='number of tuples to emit at once')

    return parser

def myconverter(o):
    if isinstance(o, datetime.datetime):
        return o.__str__()

def main(args):


    conf = {'bootstrap.servers': "kafka:9092",
            'client.id': socket.gethostname()}

    producer = Producer(conf)

    generator_module = SensorGenerator(batch = args.tuples_per_emit)

    while True:
        #print()
        for data in generator_module.emit():
            producer.produce('sensors_raw', json.dumps(data,default = myconverter) )
        #producer.produce('sensors_raw', key="key", 
        #                value=json.dumps(next(generator_module.emit()), default = myconverter))



if __name__ == '__main__':

    # we emit straight to kafka as we need the series to continue
    # Execute Process will recycle over and over
    

    parser = make_parser()
    args = parser.parse_args()

    main(args)