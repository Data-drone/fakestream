from .generator_framework import DataGenerator
import json
from faker import Faker

class ClientGenerator(DataGenerator):
    """

    idea is that this will get generated first then we will use it to make transactions

    populates a fake client list with
      name
      address
      email
      user_name
      phone_number
      uuid as a client_id

      for a given locale

    """


    def __init__(self, locale: str='en_AU'):

        self.fake_gen = Faker()


    def emit(self, tuples_to_emit: int = 10000) -> list:

        # emits a list of dicts

        client_list = []
        for i in range(tuples_to_emit): 
            client_id = self.fake_gen.uuid4()
            name = self.fake_gen.name()
            address = self.fake_gen.address()
            email = self.fake_gen.email()
            username = self.fake_gen.user_name()
            phone = self.fake_gen.phone_number()
            
            client_list.append({'client_id': client_id, 'name': name, 'address': address, 
                        'email': email, 'username': username, 'phone': phone})

        return client_list