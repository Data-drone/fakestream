# generate something that looks like a fake card transaction
# keywords / time / credit card / person

from faker import Faker
from numpy.random import Generator, PCG64, binomial
import datetime

# ISO 8601 compact timestamp
current_time = datetime.datetime.utcnow().strftime("%Y%m%dT%H%M%S.%fZ")

fake = Faker()

# we will leave it as one string so that we can show off ETL maybe? ¯\_(ツ)_/¯
card_id = fake.credit_card_full(card_type=None)

# we will use some distributions so there is something statistically interesting
value_rng = Generator(PCG64())
value_low = value_rng.normal(mu=20, sigma=1, size=1) 
value_high =  value_rng.normal(mu=1000, sigma=1, size=1)
# lets mix things up a little and have two possible values

binom_bit = binomial(1, 0.9)

if binom_bit==1:
    value = value_high
else:
    value = value_low


return_tuple = {'card_identifier': card_id, 'timestamp': current_time, 'value':value}

print(return_tuple)
