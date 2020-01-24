# generic generator framework
# methods
# emit to push out data

class DataGenerator:

    @abstractmethod
    def __init__(self):
        pass
        
    @abstractmethod
    def emit(self) -> dict:
        pass