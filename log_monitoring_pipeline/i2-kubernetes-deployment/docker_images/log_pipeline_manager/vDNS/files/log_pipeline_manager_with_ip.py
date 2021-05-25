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
import re
import uuid

app = Flask(__name__)
logger = logging.getLogger("LogPipelineManagerClient")


@app.route('/', methods=['GET'])
def server_status():
    logger.info("GET /")
    return '', 200


@app.route("/spec", methods=['GET'])
def spec():
    """
    Get swagger specification.
    ---
    describe: get swagger specification
    responses:
      swagger:
        description: swagger specification
    """
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

def delete_alert(alertId):
    logger.info("Time to delete the alert %s", alertId)
    # Transformation to Elast Alert Information Model

    r = requests.delete(url_elastalert + "/rules/" + alertId)
    logger.info("Response: Code %s", r)




@app.route('/job/<ns_id>', methods=['POST'])
def subscribe(ns_id):
    """
    Create topic associated to a ns_id.
    ---
    describe: Create topic associated to a ns_id.
    parameters:
      - in: path
        name: ns_id
        type: string
        description: ns_id and consequently the topic name
    responses:
      200:
        description: accepted request
        content:
            application/json:
              schema:
                type: object
                properties:
                  created:
                    type: string
                    description: the ns_id (and topic name) being created    
      400:
        description: error processing the request
    """ 
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
           response = json.loads(json.dumps({'created': [kafka_topic]}))
        else:
           logger.warning("The topic %s already exists in Kafka", kafka_topic)
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200


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
    alert_id = data["alertId"]
    index_elast = data["index"]
    receiver = data["target"]
    alert_type = data["kind"]
    num_events_and_unit = data["for"]
    filter_field = data["query"]

    r = re.compile("([0-9]+)([a-zA-Z]+)")
    m = r.match(num_events_and_unit)
    time_integer = m.group(1)
    time_unit = m.group(2)


    if time_unit == "s":
        unit = "seconds"
    elif time_unit == "m":
        unit = "minutes"
    elif time_unit == "h":
        unit = "hours"
    elif time_unit == "d":
        unit = "days"
    elif time_unit == "w":
        unit = "weeks"    
    elif time_unit == "y":
        unit = "years"
    else:
        unit = "seconds"

    if  alert_type == "not_match":
        string4 = "es_host: " + elasticsearch_pod + "\nes_port: 9200\nindex: " + index_elast +"\ntype: frequency\nnum_events: 1\ntimeframe:\n " + unit + ": " + time_integer +"\nfilter:\n- query:\n    query_string:\n      query: \"NOT message: " + filter_field.lower() +  "\"" +  "\nalert:\n- command\ncommand: [\"/opt/elastalert/rules/comando.sh\", \"" + alert_id + "\", \"" + receiver + "\"]"
    elif alert_type == "match":
        string4 = "es_host: " + elasticsearch_pod + "\nes_port: 9200\nindex: " + index_elast +"\ntype: frequency\nnum_events: 1\ntimeframe:\n  " + unit + ": " + time_integer +"\nfilter:\n- query:\n    wildcard:\n      message: \"" + filter_field.lower() + "\""  + "\nalert:\n- command\ncommand: [\"/opt/elastalert/rules/comando.sh\", \"" + alert_id + "\", \"" + receiver + "\"]"
   
    # es_host = ELASTICSEARCH ip (dns)
    # type = any (para comprobar que funciona)

    # @ Config Manager : target  es 10.9.8.154:8000/alert_receiver
    
    # string3 = "es_host: " + elasticsearch_pod + "\nes_port: 9200\nindex: " + index_elast +"\ntype: frequency\nnum_events: 1\ntimeframe:\n  " + unit + ": " + time_integer +  "\nalert:\n- command\ncommand: [\"/opt/elastalert/rules/comando.sh\", \"" + alert_name + "\", \"" + receiver + "\"]"
    # string4 = "es_host: " + elasticsearch_pod + "\nes_port: 9200\nindex: " + index_elast +"\ntype: frequency\nnum_events: 1\ntimeframe:\n  " + unit + ": " + time_integer +"\nfilter:\n- query:\n    wildcard:\n      message: \"" + filter_field.lower() + "\""  + "\nalert:\n- command\ncommand: [\"/opt/elastalert/rules/comando.sh\", \"" + alert_id + "\", \"" + receiver + "\"]"

    string2 = { "yaml": string4 }

    
    # data2 = { "yaml": "es_host: 10.9.8.154\nes_port: 9200\nname: FILEBEAT \ntype: frequency\nindex: uc.4.france_nice.application_metric.service_delay\nnum_events: 10\ntimeframe:\n seconds: 15\nalert:\n- command\ncommand: [\"curl\", \"10.9.8.23:8000\"]"}
    request_body = json.loads(json.dumps(string2))
    r = requests.post(url_elastalert + "/rules/" + alert_id, json=request_body)
    response = r.json()
    logger.info("Response from elastalert: created %s - id %s", response["created"], response["id"])
    return response

def send_to_logScraper(data):
    logger.info("Send data to logScraper. Data: %s ", data)
    request_body = json.loads(json.dumps(data))
    r = requests.post(url_dashboard_manager + "/logScraper", json=request_body)
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
    r = requests.delete(url_dashboard_manager + "/logScraper/" + logScraperId , json=request_body)
    logger.info("Response: Code %s", r)
    


@app.route('/job/<ns_id>', methods=['DELETE'])
def unsubscribe(ns_id):
    """
    Delete the topic associated to an ns_id.
    ---
    describe: Delete the topic associated to an ns_id
    parameters:
      - in: path
        name: ns_id
        type: string
        description: ns_id and consequently the topic name
    responses:
      200:
        description: accepted request
      400:
        description: error processing the request
 
    """      
    # Get the topic name from the ns_id in the URL
    kafka_topic = str(ns_id)
    logger.info("Request received - DELETE /job/%s", kafka_topic)
    
    try:
        if check_if_topic_exist(kafka_topic) == True:
            
            # Delete Logstash pipeline
            delete_logstash_pipeline(kafka_topic)   

            # Remove Elasticsearch index
            index_cleaner(kafka_topic.lower())
#            scheduled_thread = threading.Timer(timedelta(seconds=30).total_seconds(), index_cleaner, args = [kafka_topic.lower()])
#            scheduled_thread.start()
#            logger.info("Data removal for topic %s scheduled in 30 seconds", kafka_topic)

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
    """
    Create Kibana Dashboard associated to a ns_id.
    ---
    describe: create Kibana Dashboard associated to a ns_id
    parameters:
      - in: body
        name: kibana_description_data
        schema:
          id: kibana_description_data
          properties:
            dashboardTitle:
              type: string
              description: any dashboard Title
            ns_id:
              type: string
              description: corresponding ns_id associated to the Dashboard
            dashboard_type:
              type: string
              description: dashboard_type set to 'vm_logs'
    responses:
      200:
        description: accepted request
        content:
            application/json:
              schema:
                type: object
                properties:
                  dashboardId:
                    type: string
                    description: dashboardId created
                  dashboardTitle:
                    type: array
                    description: dashboard title
                  dashboard_type:
                    type: string
                    description: set to 'vm_logs'
                  ns_id:
                    type: string
                    description: corresponding ns_id associated to the Dashboard
                  url:
                    type: string
                    description: url associated to the dashboard created  
      400:
        description: error processing the request
    """
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
            data["ns_id"]=data["ns_id"].lower()
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
    """
    Delete Kibana Dashboard associated to a ns_id.
    ---
    describe: delete Kibana Dashboard associated to a ns_id.
    parameters:
      - in: path
        name: dashboardId
        type: string
        description: dashboardId to be deleted
    responses:
      200:
        description: accepted request
        content:
            application/json:
              schema:
                type: object
                properties:
                  deleted:
                    type: string
                    description: the dashboardId being removed    
      400:
        description: error processing the request
    """ 
    
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
    """
    Create Alert associated to a ns_id monitorization.
    ---
    describe: Create Alert associated to a ns_id monitorization
    parameters:
      - in: body
        name: alert_description_data
        schema:
          id: alert_description_data
          properties:
            alertName:
              type: string
              description: any alert name
            labels:
              type: array
              description: additional values that may be needed or set to '[]'
            query:
              type: string
              description: values to filter in the message
            severity:
              type: string
              description: not being considered 
            for:
              type: string
              description: timeframe to be set, in the form value and unit time
            target:
              type: string
              description: set to <ip_containing_the_alert_reveiver>:8000/alert_receiver
            kind:
              type: string
              description: set to 'frequency'
            index:
              type: string
              description: ns_id which is being monitorized for the alert to trigger
    responses:
      200:
        description: accepted request
        content:
            application/json:
              schema:
                type: object
                properties:
                  alertName:
                    type: string
                    description: the alert name created
                  labels:
                    type: array
                    description: additional possible parameters
                  query:
                    type: string
                    description: values to filter in the message
                  severity:
                    type: string
                    description: not being considered
                  for:
                    type: string
                    description: timeframe to be set, in the form value and unit time
                  target:
                    type: string
                    description: set to <ip_containing_the_alert_reveiver>:8000/alert_receiver       
                  kind:
                    type: string
                    description: set to 'frequency'                                                  
                  index:
                    type: string
                    description: ns_id which is being monitorized for the alert to trigger    
      400:
        description: error processing the request
    """
    logger.info("Request received - POST /alert")
    if not request.is_json:
        logger.warning("Format not valid")
        return 'Format not valid', 400
    try: 
        data = request.get_json()
        logger.info("Value received: alertName %s - labels %s - query %s - severity %s - for %s - target %s - kind %s - index %s", data["alertName"], data["labels"], data["query"], data["severity"], data["for"], data["target"], data["kind"], data["index"])
        
        # Create UUID to treat the alert with it from this moment on
        alertId_uuid = str(uuid.uuid1())
        data["alertId"] = alertId_uuid
        data["index"]=data["index"].lower()

        response1 = send_to_elastalert(data)

        if response1["created"] == True:
            response = json.loads(json.dumps({'alertId': data["alertId"], 'alertName': data["alertName"], 'labels': data["labels"], 'query': data["query"], 'severity': data["severity"],'for':  data["for"], 'target': data["target"], 'kind': data["kind"],'index':  data["index"]}))
            return response, 201
        else:
            response = json.loads(json.dumps({'ERROR': "EA did not create the alert properly."}))
            return response, 500
    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400

@app.route('/alert/<alert_id>', methods=['DELETE'])
def deletealert(alert_id):
    """
    Delete a concrete alert monitoring a ns_id.
    ---
    describe: delete a concrete alert monitoring a ns_id
    parameters:
      - in: path
        name: alert_id
        type: string
        description: alert_id created to monitor a ns_id
    responses:
      200:
        description: accepted request
        content:
            application/json:
              schema:
                type: object
                properties:
                  deleted:
                    type: string
                    description: the alert_id being removed    
      400:
        description: error processing the request  
    """ 
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
    """
    Create LogScrapper associated to a ns_id monitorization and a later kafka topic.
    ---
    describe: create LogScrapper associated to a ns_id monitorization and a later kafka topic
    parameters:
      - in: body
        name: logscraper_description_data
        schema:
          id: logscraper_description_data
          properties:
            nsid:
              type: string
              description: corresponding ns_id associated
            vnfid:
              type: array
              description: set to 'webserver'
            performanceMetric:
              type: string
              description: set to 'logs'
            kafkaTopic:
              type: string
              description: associated to the later publisher kafka topic 
            interval:
              type: integer
              description: timeframe expressed in seconds 
            expression:
              type: string
              description: regular expression      
    responses:
      200:
        description: accepted request
        content:
            application/json:
              schema:
                type: object
                properties:
                  scraper_id:
                    type: string
                    description: the scraper to be removed
                  nsid:
                    type: string
                    description: the ns_id (and topic) being scrapped 
                  vnfid:
                    type: string
                    description: set to 'webserver'
                  performanceMetric:
                    type: string
                    description: set to 'logs'
                  kafkaTopic:
                    type: string
                    description: the topic where the logs were being produced
                  interval:
                    type: integer
                    description: seconds that were scheduled         
                  expression:
                    type: string
                    description: regular expression applied on ElasticSearch                                                       
      400:
        description: error processing the request       
    """
        
    
    logger.info("Request received - POST /logScraper")
    if not request.is_json:
        logger.warning("Format not valid")
        return 'Format not valid', 400
    try: 
        data = request.get_json()
        logger.info("Value received: nsid %s - vnfid %s - performanceMetric %s - kafkaTopic %s - interval %s - expression %s ", data["nsid"], data["vnfid"], data["performanceMetric"], data["kafkaTopic"], data["interval"], data["expression"])
        data["nsid"]=data["nsid"].lower()

        response = send_to_logScraper(data)        

    except Exception as e:
        logger.error("Error while parsing request")
        logger.exception(e)
        return str(e), 400
    return response, 200



@app.route('/logScraper/<scraperId>', methods=['DELETE'])
def deletelogScraper(scraperId):
    """
    Delete a LogScraper monitoring a ns_id.
    ---
    describe: delete a LogScraper monitoring a ns_id
    parameters:
      - in: path
        name: scraperId
        type: string
        description: the scraperId which was created
    responses:
      200:
        description: accepted request
      400:
        description: error processing the request
    """ 
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
    # Usage: /usr/bin/python3 log_pipeline_manager_with_ip.py --create_kafka_topic_pod_port createkafkatopic:8190 --delete_kafka_topic_pod_port localhost:8290 --fetch_kafka_topic_pod_port fetchkafkatopic:8390 --log info --logstash_pipeline_manager_pod_port logstashpipelinemanager:8191 --elasticsearch_pod elasticsearch:9200 -kafka_consumer_pod_port kafkaconsumer:8291 --elk_password changeme
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--create_kafka_topic_pod_port",
        help='createKafkaTopic function pod:port',
        default='localhost:8190')
    parser.add_argument(
        "--delete_kafka_topic_pod_port",
        help='deleteKafkaTopic function pod:port',
        default='localhost:8290')
    parser.add_argument(
        "--fetch_kafka_topic_pod_port",
        help='listKafkaTopics function pod:port',
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
        "--logstash_pipeline_manager_pod_port",
        help='logstashPipelineManager service pod:port',
        default='localhost:8191')
    parser.add_argument(
        "--elasticsearch_pod",
        help='Elasticsearch pod',
        default='localhost')
    parser.add_argument(
        "--kafka_consumer_pod_port",
        help='kafkaConsumer function pod:port',
        default='localhost:8291')
    parser.add_argument(
        "--elk_password",
        help='ELK password')
    parser.add_argument(
        "--dashboard_manager_pod",
        help='Dashboard manager pod',
        default='localhost')
    parser.add_argument(
        "--elastalert_pod",
        help='ElastAlert pod',
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

    global create_kafka_topic_pod_port 
    create_kafka_topic_pod_port = str(args.create_kafka_topic_pod_port)
    global url_create_kafka_topic
    url_create_kafka_topic = "http://" + create_kafka_topic_pod_port + "/create_kafka_topic"
    
    global dashboard_manager_pod
    dashboard_manager_pod= str(args.dashboard_manager_pod)
    global url_dashboard_manager
    url_dashboard_manager = "http://" + dashboard_manager_pod + ":8080"
    
    global elastalert_pod 
    elastalert_pod = str(args.elastalert_pod)
    global url_elastalert
    url_elastalert = "http://" + elastalert_pod + ":3030"
    
    global delete_kafka_topic_pod_port
    delete_kafka_topic_pod_port = str(args.delete_kafka_topic_pod_port)
    global url_delete_kafka_topic
    url_delete_kafka_topic = "http://" + delete_kafka_topic_pod_port + "/delete_kafka_topic"
    
    global fetch_kafka_topic_pod_port 
    fetch_kafka_topic_pod_port = str(args.fetch_kafka_topic_pod_port)    
    global url_fetch_kafka_topic
    url_fetch_kafka_topic = "http://" + fetch_kafka_topic_pod_port + "/fetch_kafka_topic"

    global logstash_pipeline_manager_pod_port 
    logstash_pipeline_manager_pod_port = str(args.logstash_pipeline_manager_pod_port)
    global url_logstash_pipeline_manager
    url_logstash_pipeline_manager = "http://" + logstash_pipeline_manager_pod_port + "/logstash_pipeline_manager"
    
    global elasticsearch_pod 
    elasticsearch_pod = str(args.elasticsearch_pod)    
    global url_elasticsearch
    url_elasticsearch = "http://" + elasticsearch_pod + ":9200"

    global kafka_consumer_pod_port
    kafka_consumer_pod_port = str(args.kafka_consumer_pod_port)
    global url_kafka_consumer
    url_kafka_consumer = "http://" + kafka_consumer_pod_port + "/kafka_consumer"
    
    global elk_password
    elk_password= str(args.elk_password)

    logger.info("Serving Log Pipeline Manager on port 8987")

    serve(app, host='0.0.0.0', port=8987)
