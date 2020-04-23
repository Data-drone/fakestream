from generators import SensorGenerator
from argparse import ArgumentParser
import datetime
import json
from confluent_kafka import Producer
import socket
from threading import Thread


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


def generate_sensor(sensor_name, 
                    tuples, config):

    gen = SensorGenerator(batch = tuples)
    producer = Producer(config)

    while True:
        for data in gen.emit():
            for element in data:
                data_dict = {'timestamp': element[0], 'value': element[1]}

                producer.produce('sensors-raw', key=sensor_name, 
                                        value=json.dumps(data_dict,default = myconverter), 
                                        callback = delivery_report)

                # clean up the queue to avert the buffer crash
                producer.poll(0)



def main(args):

    conf = {'bootstrap.servers': "kafka:9092",
        'queue.buffering.max.messages': 50000, # is this too small?
        'queue.buffering.max.ms': 60000, # is this too long?
        'client.id': socket.gethostname()}

    # rig it up for multi-sensors?

    print("starting sensor streamer")
    print("Starting {0} sensors".format(args.sensors))
    threads = []
    for i in range(args.sensors):
        name = 'sensor_{0}'.format(str(i))
        print("starting: {0}".format(name))

        process = Thread(target=generate_sensor, args=[name, args.tuples_per_emit, conf])
        process.start()

        threads.append(process)



if __name__ == '__main__':

    # we emit straight to kafka as we need the series to continue
    # Execute Process will recycle over and over
    

    parser = make_parser()
    args = parser.parse_args()

    main(args)