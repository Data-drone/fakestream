from generators import SensorGenerator
from argparse import ArgumentParser
import datetime
import json
from confluent_kafka import Producer
import socket
from threading import Thread

conf = {'bootstrap.servers': "kafka:9092",
        'queue.buffering.max.messages': 500000, # is this too small?
        'queue.buffering.max.ms': 60000, # is this too long?
        'client.id': socket.gethostname()}


def make_parser():
    """

    argument parser for controlling the number of streams

    """

    parser = ArgumentParser(description="Create dummy sensor stream esque data")
    parser.add_argument('--tuples-per-emit', '-t', type=int, default=1,
                            help='number of tuples to emit at once')
    parser.add_argument('--sensors', '-s', type=int, default=1,
                            help='number of sensors to generate')

    return parser

def delivery_report(err, msg):
    """ Called once for each message produced to indicate delivery result.
        Triggered by poll() or flush(). """
    if err is not None:
        print('Message delivery failed: {}'.format(err))
    else:
        print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))


def myconverter(o):
    """

    json converter to make sure the date time is parsed properly

    """
    if isinstance(o, datetime.datetime):
        return o.__str__()


def generate_sensor(sensor_name = 'sensor1', 
                    tuples = 1):

    gen = SensorGenerator(batch = tuples)
    producer = Producer(conf)

    while True:
        for data in gen.emit():
            producer.produce('sensors_raw', key=sensor_name, 
                                    value=json.dumps(data,default = myconverter), 
                                    callback = delivery_report)

            # clean up the queue to avert the buffer crash
            producer.poll(0)



def main(args):

    # rig it up for multi-sensors?

    threads = []
    for i in range(args.sensors):
        name = 'sensor_{0}'.format(str(i))
        process = Thread(target=generate_sensor, args=[name, args.tuples_per_emit])
        process.start()

        threads.append(process)



if __name__ == '__main__':

    # we emit straight to kafka as we need the series to continue
    # Execute Process will recycle over and over
    

    parser = make_parser()
    args = parser.parse_args()

    main(args)