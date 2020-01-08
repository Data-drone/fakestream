# generate something that looks like a fake card transaction
# keywords / time / credit card / person

from faker import Faker
from numpy.random import Generator, PCG64
import datetime

# ISO 8601 compact timestamp
current_time = datetime.datetime.utcnow().strftime("%Y%m%dT%H%M%S.%fZ")

fake = Faker()

# we will leave it as one string so that we can show off ETL maybe? ¯\_(ツ)_/¯
card_id = fake.credit_card_full(card_type=None)

# we will use some distributions so there is something statistically interesting
value_rng = Generator(PCG64())
value = value_rng.normal(mu=20, sigma=1, size=1) 

return_tuple = {'card_identifier': card_id, 'timestamp': current_time, 'value':value}

