import logging
import os
from logging.handlers import RotatingFileHandler


def get_logger(name = None):
    agent_work_dir = os.environ['AGENT_WORK_DIR']
    logger = logging.getLogger(name)
    if len(logger.handlers) > 0:
        return logger
    if name is None:
        formatter = logging.Formatter('%(asctime)s %(levelname)s %(filename)s Procedure: %(funcName)s %(message)s')
    else:
        formatter = logging.Formatter('%(asctime)s %(levelname)s %(filename)s Class: %(name)s %(funcName)s %(message)s')
    logger.setLevel(logging.INFO)
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    console.setFormatter(formatter)
    logfile_path = os.path.join(agent_work_dir, 'logger.log')
    filehandler = RotatingFileHandler(logfile_path, maxBytes=20000, backupCount=5)
    filehandler.setLevel(logging.INFO)
    filehandler.setFormatter(formatter)
    logger.addHandler(console)
    logger.addHandler(filehandler)
    return logger