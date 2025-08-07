import logging


logging.basicConfig(
    filename="LLMmakerule.log",  
    level=logging.INFO, 
    format="%(asctime)s - %(levelname)s - %(message)s" 
)

def log_and_print(message):
    print(message)
    logging.info(message)