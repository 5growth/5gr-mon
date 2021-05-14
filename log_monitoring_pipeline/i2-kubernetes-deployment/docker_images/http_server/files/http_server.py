import requests
import argparse
import logging
import coloredlogs
from flask import Flask, request, jsonify
from flask_swagger import swagger
from waitress import serve
import json


app = Flask(__name__)
logger = logging.getLogger("HttpServer")


@app.route('/', methods=['GET'])
def server_status():
    logger.info("GET /")
    return '', 200


@app.route("/spec", methods=['GET'])
def spec():
    swag = swagger(app)
    swag['info']['version'] = "1.0"
    swag['info']['title'] = "HttpServer REST API"
    return jsonify(swag)

@app.route('/alert_receiver', methods=['POST'])
def alert_receiver():
    logger.info("Request received - POST /alert_receiver")
    if not request.is_json:
        logger.warning("Format not valid")
        return 'Format not valid', 400
    try:
        # Parse JSON
        data = request.get_json()
        logger.info("Data received: %s", data)
        print("Data received: ", data)

    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return '', 201


if __name__ == "__main__":
    # Usage: /usr/bin/python3 http_server.py  --log info
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--log",
        help='Sets the Log Level output, default level is "info"',
        choices=[
            "info",
            "debug",
            "error",
            "warning"],
        nargs='?',
        default='info')

    args = parser.parse_args()
    numeric_level = getattr(logging, str(args.log).upper(), None)
    if not isinstance(numeric_level, int):
        raise ValueError('Invalid log level: %s' % loglevel)
    coloredlogs.install(
        fmt='%(asctime)s %(levelname)s %(message)s',
        datefmt='%d/%m/%Y %H:%M:%S',
        level=numeric_level)
    logging.getLogger("HttpServer").setLevel(numeric_level)
    logging.getLogger("requests.packages.urllib3").setLevel(logging.ERROR)

    logger.info("Serving HttpServer on port 8000")
    serve(app, host='0.0.0.0', port=8000)
