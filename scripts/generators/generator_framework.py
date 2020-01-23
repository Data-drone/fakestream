# generic generator framework
# methods
# emit to push out data

class DataGenerator:

    @abstractmethod
    def __init__(self, tuples_to_emit_per_call: int):

        self.tuples_per_emit = tuples_to_emit_per_call

    @abstractmethod
    def emit(self) -> list:
        pass