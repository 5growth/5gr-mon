import requests
import argparse
import logging
import coloredlogs
from flask import Flask, request, jsonify
from flask_swagger import swagger
from waitress import serve
import json
import threading
from threading import Thread
from threading import Timer
from datetime import timedelta
#import psycopg2
import time

app = Flask(__name__)
logger = logging.getLogger("LogPipelineManagerClient")


@app.route('/', methods=['GET'])
def server_status():
    logger.info("GET /")
    return '', 200


@app.route("/spec", methods=['GET'])
def spec():
    swag = swagger(app)
    swag['info']['version'] = "1.0"
    swag['info']['title'] = "Log Pipeline Manager REST API"
    return jsonify(swag)


def check_if_topic_exist(topic):
    logger.info("Checking if topic %s exists in Kafka", topic)
    request_body = json.loads(json.dumps({'topic': topic}))
    r = requests.get(url_fetch_kafka_topic, json=request_body)
    logger.info("Response: Code %s", r)

    topic_exist = False

    if r.status_code == 302:
        topic_exist = True
    elif r.status_code == 404:
        topic_exist = False
    else:
        logger.warning("Wrong operation")

    return topic_exist


def create_elasticsearch_index(topic):
    logger.info("Creating Elasticsearch index for topic %s (in lowercase)", topic)
    r = requests.put(url_elasticsearch + "/" + topic, auth=('elastic', elk_password))
    logger.info("Response: Code %s", r)



def create_kafka_topic(topic):
    logger.info("Creating topic %s in Kafka", topic)
    request_body = json.loads(json.dumps({'topic': topic}))
    r = requests.post(url_create_kafka_topic, json=request_body)
    logger.info("Response: Code %s", r)


def create_logstash_pipeline(topic):
    logger.info("Creating Logstash pipeline for topic %s", topic)
    request_body = json.loads(json.dumps({'topic': topic}))
    r = requests.post(url_logstash_pipeline_manager, json=request_body)
    logger.info("Response: Code %s", r)

    
def kafka_consumer(topic):
    logger.info("Creating Kafka consumer for topic %s", topic)
    request_body = json.loads(json.dumps({'topic': topic}))
    r = requests.post(url_kafka_consumer, json=request_body)
    logger.info("Response: Code %s", r)


def index_cleaner(topic):
    logger.info("Time to delete the Elasticsearch index for topic %s", topic)
    r_second = requests.delete(url_elasticsearch + "/" + topic, auth=('elastic', elk_password))
    logger.info("Response: Code %s", r_second)

def delete_dashboard(dashboardId):
    logger.info("Time to delete the dashboard for topic %s", dashboardId)
    r = requests.delete(url_dashboard_manager + "/dashboard", json=json.loads(json.dumps({'dashboardId': dashboardId})))
    logger.info("Response: Code %s", r)
    # TO BE DONE APAÑAR ESTA VUELTA
    # incluso tocar el 201 y 400 dentro de cada if y else, como la QUESTION del POST kibana

def delete_alert(alertId):
    logger.info("Time to delete the alert %s", alertId)
    # Transformation to Elast Alert Information Model

    r = requests.delete(url_elastalert + "/rules/" + alertId)
    logger.info("Response: Code %s", r)
    # TO BE DONE APAÑAR ESTA VUELTA
    # incluso tocar el 201 y 400 dentro de cada if y else, como la QUESTION del POST kibana





@app.route('/job/<ns_id>', methods=['POST'])
def subscribe(ns_id):
    # Get the topic name from the ns_id in the URL
    kafka_topic = str(ns_id)
    logger.info("Request received - POST /job/%s", kafka_topic)

    try: 
        if check_if_topic_exist(kafka_topic) == False:
           create_kafka_topic(kafka_topic)

           create_elasticsearch_index(kafka_topic.lower())

           create_logstash_pipeline(kafka_topic)

           # Create Kafka consumer to wait for the first message received in the topic and, then, refresh the dashboard.
           # Change needed in kafka_consumer parameter: only the topic is sent
           kafka_consumer(kafka_topic)
        else:
           logger.warning("The topic %s already exists in Kafka", kafka_topic)
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return '', 200


def delete_kafka_topic(topic):
    logger.info("Deleting topic %s in Kafka", topic)
    request_body = json.loads(json.dumps({'topic': topic}))
    r = requests.delete(url_delete_kafka_topic, json=request_body)
    logger.info("Response: Code %s", r)


def delete_logstash_pipeline(topic):
    logger.info("Deleting Logstash pipeline for topic %s", topic)
    request_body = json.loads(json.dumps({'topic': topic}))
    r = requests.delete(url_logstash_pipeline_manager, json=request_body)
    logger.info("Response: Code %s", r)


def send_to_elastalert(data):
    logger.info("Send data to Elastalert. Data: %s ", data)
    
    # Transformation into Elast Alert Information Model
    alert_name = data["alertName"]
    index_elast = data["index"]
    receiver = data["target"]
    alert_type = data["kind"]


    #curl --location --request POST 'http://10.9.8.154:3030/rules/FILEBEATagain' \
#--header 'Content-Type: application/json' \
#--data-raw '{
# "yaml": "es_host: 10.9.8.154\nes_port: 9200\nname: Prueba3 \ntype: frequency\nindex: uc.4.france_nice.application_metric.service_delay\nnum_events: 10\ntimeframe:\n seconds: 15\nalert:\n- command\ncommand: [\"curl\", \"10.9.8.23:8000\"]"https://meet.google.com/ocq-zbfv-afb
# }'

   
    # es_host = ELASTICSEARCH ip (dns)
    # type = any (para comprobar que funciona)

    # @ Config Manager : target  es 10.9.8.154:8000/alert_receiver
    
    string1 = "es_host: " + elasticsearch_ip_address + "\nes_port: 9200\nname: " + alert_name +"\ntype: " + alert_type+"\nindex: " + index_elast +"\nnum_events: 10\ntimeframe:\nseconds: 1\nalert:\n- command\ncommand: [FECHA=$(date); CURL_DATA=$(jq -n --arg an " + alert_name+ " --arg sa \"$FECHA\" '{alertname: $an, startsAt: $sa}'); curl -X POST -H \"Content-Type: application/json\"  -d \"$CURL_DATA\" " + receiver + "]"
    string2 = { "yaml": string1 }

    
    # data2 = { "yaml": "es_host: 10.9.8.154\nes_port: 9200\nname: FILEBEAT \ntype: frequency\nindex: uc.4.france_nice.application_metric.service_delay\nnum_events: 10\ntimeframe:\n seconds: 15\nalert:\n- command\ncommand: [\"curl\", \"10.9.8.23:8000\"]"}
    request_body = json.loads(json.dumps(string2))
    r = requests.post(url_elastalert + "/rules/" + alert_name, json=request_body)
    response = r.json()
    logger.info("Response FROM ELAST ALERT: Code %s", r)
    return response

def send_to_logScraper(data):
    logger.info("Send data to logScraper. Data: %s ", data)
    request_body = json.loads(json.dumps(data))
    r = requests.post(logScraper, json=request_body)
    response = r.json()
    logger.info("Response: scraper_id %s - nsid %s - vnfid %s - performanceMetric %s - kafkaTopic %s - interval %s - expression %s ", response["scraper_id"], data["nsid"], data["vnfid"], data["performanceMetric"], data["kafkaTopic"], data["interval"], data["expression"])
    return response

def create_dashboard(data):
    # Dashboard CREATION
    logger.info("Creating dashboard for topic: %s", data["ns_id"]) 
    # Transformation to Kibana Information Model
    data2 = {"value": [ data] }
    request_body = json.loads(json.dumps(data2))
    r = requests.post(url_dashboard_manager + "/dashboard", json=request_body)
        
    # Response of Kibana Dashboard 
    # logger.info("Response: Code %s", r)
    response = r.json()
    logger.info("Response: dashboardId %s - url %s - dashboardTitle %s - ns id %s - dashboard type %s ", response["dashboardId"], response["url"], response["dashboardTitle"], response["ns_id"], response["dashboard_type"])
    return response 

def delete_logScraper(logScraperId):
    logger.info("Time to delete logScraper %s", logScraperId)
    request_body = json.loads(json.dumps({'logScraperId': logScraperId}))
    r = requests.delete(logScraper  + "/xxxx" , json=request_body)
    logger.info("Response: Code %s", r)
    


@app.route('/job/<ns_id>', methods=['DELETE'])
def unsubscribe(ns_id):
    # Get the topic name from the ns_id in the URL
    kafka_topic = str(ns_id)
    logger.info("Request received - DELETE /job/%s", kafka_topic)
    
    try:
        if check_if_topic_exist(kafka_topic) == True:
            
            # Delete Logstash pipeline
            delete_logstash_pipeline(kafka_topic)   

            # Schedule the removal of Kibana dashboard and Elasticsearch index (30 seconds)
            scheduled_thread = threading.Timer(timedelta(seconds=30).total_seconds(), index_cleaner, args = [kafka_topic.lower()])
            scheduled_thread.start()
            logger.info("Data removal for topic %s scheduled in 30 seconds", kafka_topic)


            delete_kafka_topic(kafka_topic)
            response = json.loads(json.dumps({'deleted': [kafka_topic]}))
        else:
           logger.warning("The topic %s does not exist in Kafka", kafka_topic)
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200




@app.route('/kibanaDashboard', methods=['POST'])
def create_kibana():
    logger.info("Request received - POST /kibanaDashboard")
    if not request.is_json:
        logger.warning("Format not valid")
        return 'Format not valid', 400
    try: 
        data = request.get_json()
        logger.info("Value received: dashboardTitle %s - ns id %s - dashboard type %s ", data["dashboardTitle"], data["ns_id"], data["dashboard_type"])
        kafka_topic = data["ns_id"]

        if check_if_topic_exist(kafka_topic) == True:
            logger.info("Topic for Dashboard creation exists OK")

            # Create extra JSON field name topic
            data["topic"]=data["ns_id"]
            response = create_dashboard(data)
        else:
            # QUESTION: What should we do here then? Error 400?
            logger.info("Topic for Dashboard does not exist. Dashboard cannot be created")
            # response = 0
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200




@app.route('/kibanaDashboard/<dashboardId>', methods=['DELETE'])
def delete_kibana(dashboardId):
    # Get the Dashboard id from the parameter in the URL
    dashboardId = str(dashboardId)
    logger.info("Request received - DELETE /kibanaDashboard/%s", dashboardId)
    try:
        delete_dashboard(dashboardId)
        response = json.loads(json.dumps({'deleted': [dashboardId]}))
        # TO BE DONE: apañar vuelta del delete kibana
        # if r==201:
        # else:
        #    logger.warning("The dashboard %s does not exist in Kibana", dashboardId)
        #    response = 0
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200



@app.route('/alert', methods=['POST'])
def create_alert():
    logger.info("Request received - POST /alert")
    if not request.is_json:
        logger.warning("Format not valid")
        return 'Format not valid', 400
    try: 
        data = request.get_json()
        logger.info("Value received: alertName %s - labels %s - query %s - severity %s - for %s - target %s - kind %s - index %s", data["alertName"], data["labels"], data["query"], data["severity"], data["for"], data["target"], data["kind"], data["index"])
        response1 = send_to_elastalert(data)        
        logger.info("RESPONSE FROM ELASTALERT (new 15 abril): " , response1)
        # if r==201
        response = json.loads(json.dumps({'alertId': data["alertName"], 'alertName': data["alertName"], 'labels': data["labels"], 'query': data["query"], 'severity': data["severity"],'for':  data["for"], 'target': data["target"], 'kind': data["kind"],'index':  data["index"]}))
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 201

@app.route('/alert/<alert_id>', methods=['DELETE'])
def deletealert(alert_id):
    # Get the Dashboard id from the parameter in the URL
    alertId = str(alert_id)
    logger.info("Request received - DELETE /alert/%s", alertId)
    try:
        delete_alert(alertId)
        response = json.loads(json.dumps({'deleted': [alertId]}))
        # TO BE DONE: apañar vuelta del ElastAlert
        # if r==201:
        # else:
        #    logger.warning("The dashboard %s does not exist in Kibana", dashboardId)
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200


@app.route('/logScraper', methods=['POST'])
def create_logScraper():
    logger.info("Request received - POST /logScraper")
    if not request.is_json:
        logger.warning("Format not valid")
        return 'Format not valid', 400
    try: 
        data = request.get_json()
        logger.info("Value received: nsid %s - vnfid %s - performanceMetric %s - kafkaTopic %s - interval %s - expression %s ", data["nsid"], data["vnfid"], data["performanceMetric"], data["kafkaTopic"], data["interval"], data["expression"])
        
        response = send_to_logScraper(data)        

    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200



@app.route('/logScraper/<scraperId>', methods=['DELETE'])
def deletelogScraper(scraperId):
    # Get the Dashboard id from the parameter in the URL
    scraperId = str(scraperId)
    logger.info("Request received - DELETE /logScraper/%s", scraperId)
    try:
        delete_logScraper(scraperId)
        response = json.loads(json.dumps({'deleted': [scraperId]}))
        # TO BE DONE: apañar vuelta del ElastAlert
        # if r==201:
        # else:
        #   logger.warning("The logScraper %s does not exist", scraperId)
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200





if __name__ == "__main__":
    # Usage: /usr/bin/python3 log_pipeline_manager_with_ip.py --create_kafka_topic_ip_port localhost:8190 --delete_kafka_topic_ip_port localhost:8290 --fetch_kafka_topic_ip_port localhost:8390 --log info --logstash_pipeline_manager_ip_port localhost:8191 --elasticsearch_ip_address localhost:9200 -kafka_consumer_ip_port localhost:8291 --elk_password changeme
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--create_kafka_topic_ip_port",
        help='createKafkaTopic function IP:port',
        default='localhost:8190')
    parser.add_argument(
        "--delete_kafka_topic_ip_port",
        help='deleteKafkaTopic function IP:port',
        default='localhost:8290')
    parser.add_argument(
        "--fetch_kafka_topic_ip_port",
        help='listKafkaTopics function IP:port',
        default='localhost:8390')
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
    parser.add_argument(
        "--logstash_pipeline_manager_ip_port",
        help='logstashPipelineManager service IP:port',
        default='localhost:8191')
    parser.add_argument(
        "--elasticsearch_ip_address",
        help='Elasticsearch IP address',
        default='localhost')
    parser.add_argument(
        "--kafka_consumer_ip_port",
        help='kafkaConsumer function IP:port',
        default='localhost:8291')
    parser.add_argument(
        "--elk_password",
        help='ELK password')
    parser.add_argument(
        "--dashboard_manager_ip_address",
        help='Dashboard manager IP address',
        default='localhost')
    parser.add_argument(
        "--elastalert_ip_address",
        help='ElastAlert IP address',
        default='localhost')
    args = parser.parse_args()
    numeric_level = getattr(logging, str(args.log).upper(), None)
    if not isinstance(numeric_level, int):
        raise ValueError('Invalid log level: %s' % loglevel)
    coloredlogs.install(
        fmt='%(asctime)s %(levelname)s %(message)s',
        datefmt='%d/%m/%Y %H:%M:%S',
        level=numeric_level)
    logging.getLogger("LogPipelineManagerClient").setLevel(numeric_level)
    logging.getLogger("requests.packages.urllib3").setLevel(logging.ERROR)

    global create_kafka_topic_ip_port 
    create_kafka_topic_ip_port = str(args.create_kafka_topic_ip_port)
    global url_create_kafka_topic
    url_create_kafka_topic = "http://" + create_kafka_topic_ip_port + "/create_kafka_topic"
    
    global dashboard_manager_ip_address 
    dashboard_manager_ip_address = str(args.dashboard_manager_ip_address)
    global url_dashboard_manager
    url_dashboard_manager = "http://" + dashboard_manager_ip_address + ":8080"
    
    global elastalert_ip_address 
    elastalert_ip_address = str(args.elastalert_ip_address)
    global url_elastalert
    url_elastalert = "http://" + elastalert_ip_address + ":3030"
    
    global delete_kafka_topic_ip_port
    delete_kafka_topic_ip_port = str(args.delete_kafka_topic_ip_port)
    global url_delete_kafka_topic
    url_delete_kafka_topic = "http://" + delete_kafka_topic_ip_port + "/delete_kafka_topic"
    
    global fetch_kafka_topic_ip_port 
    fetch_kafka_topic_ip_port = str(args.fetch_kafka_topic_ip_port)    
    global url_fetch_kafka_topic
    url_fetch_kafka_topic = "http://" + fetch_kafka_topic_ip_port + "/fetch_kafka_topic"

    global logstash_pipeline_manager_ip_port 
    logstash_pipeline_manager_ip_port = str(args.logstash_pipeline_manager_ip_port)
    global url_logstash_pipeline_manager
    url_logstash_pipeline_manager = "http://" + logstash_pipeline_manager_ip_port + "/logstash_pipeline_manager"
    
    global elasticsearch_ip_address 
    elasticsearch_ip_address = str(args.elasticsearch_ip_address)    
    global url_elasticsearch
    url_elasticsearch = "http://" + elasticsearch_ip_address + ":9200"

    global kafka_consumer_ip_port
    kafka_consumer_ip_port = str(args.kafka_consumer_ip_port)
    global url_kafka_consumer
    url_kafka_consumer = "http://" + kafka_consumer_ip_port + "/kafka_consumer"
    
    global elk_password
    elk_password= str(args.elk_password)

    logger.info("Serving Log Pipeline Manager on port 8987")

    serve(app, host='0.0.0.0', port=8987)
