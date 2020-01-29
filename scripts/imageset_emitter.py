###
# Python App to emit data
# use logger to write to logs for other apps to pick up
# what if I reuse the other files?
# make a generic class that we can call on startup?
# 

from generators.generator_image_flow import ImageFlow
from argparse import ArgumentParser
from dict2xml import dict2xml as xmlify 
import os
from pathlib import Path
from PIL import Image

#TODO
# construct logger object
# with log rotation
# csv?

# call generators
# and emit
ROOT_DIR = os.path.join('log', 'image')
Path(ROOT_DIR).mkdir(parents=True, exist_ok=True)


def make_parser():

    """
    argument parser for main function
    """

    parser = ArgumentParser(description="Create dummy data for streaming examples")
    parser.add_argument('--tuples-per-emit', '-t', type=int, default=1,
                            help='number of tuples to emit at once')
    parser.add_argument('--image-size', '-is', type=list, default=[1280, 720],
                            help='size of image')
    

    return parser




if __name__ == '__main__':

    parser = make_parser()
    args = parser.parse_args()
    
    new_generator = ImageFlow(images_per_emit=args.tuples_per_emit,
                                image_size=args.image_size)

    stop = False

    while(stop!=True):

        results = new_generator.emit()
        
        xml = results['xml_item']
        images = results['images']

        uuid = xml['item_id']

        dat = xmlify(xml, wrap="consignment", indent="    ")

        path = os.path.join(ROOT_DIR, uuid + '.xml')
        xmlfile = open(path, "w")
        xmlfile.write(dat)
            
        for i, img in enumerate(images):
            name = os.path.join(ROOT_DIR, '{0}_{1}.jpg'.format(uuid, i))
            Image.fromarray(img).convert("RGB").save(name, subsampling=0, quality=100)

        #logger.info(results)