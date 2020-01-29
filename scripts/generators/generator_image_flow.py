
from .generator_framework import DataGenerator
from faker import Faker
import random
import datetime
import json
from argparse import ArgumentParser
import uuid
import numpy as np
from typing import List

class ImageFlow(DataGenerator):

        
    def __init__(self, images_per_emit: int=1, image_size: List[int]=[1290, 720]):
            
            self.images_per_emit = 1
            self.image_size = image_size
            self.fake_gen = Faker()
        
    
    def emit(self) -> dict:
        
        # generate an id
        unique_id = str(uuid.uuid4())

        current_time = datetime.datetime.utcnow().strftime("%Y%m%dT%H%M%S.%fZ")

        xml_item = {'item_id': unique_id, 'scan_time': current_time, 
                    'height': random.randint(50,100), 'width': random.randint(50,100), 'depth': random.randint(50,100),
                    'weight': random.randint(100,2000)}

        images = []

        for i in range(0, self.images_per_emit):

            image_thingy = np.random.randint(255, size=(self.image_size[0], self.image_size[1], 3), dtype=np.uint8)

            images.append(image_thingy)
        # fake dimensions
        # 

        return {'xml_item': xml_item, 'images': images}