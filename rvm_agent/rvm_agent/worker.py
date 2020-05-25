import copy
import json
import os
import subprocess
import tempfile
import threading
from time import sleep

import click
from confluent_kafka import Consumer, Producer
import requests

from rvm_agent.utils import get_logger


class ThreadKeepAlive(threading.Thread):
    """
        The class sends keepalive messages to the server.
        If RVM agent has errors this information includes keepalive messages
    """

    def __init__(self, interval, producer, backward_topic, prometheus_collector_dict):
        """

        :type interval: Integer time between keepalive messages unit seconds
        :type producer: Asynchronous Kafka Producer
        :type backward_topic: String Topic for data from RVM agent to Configure Manager Server
        :type prometheus_collector_dict: {String:ThreadPrometheusCollector}
        Example
        <class 'dict'>: {'192.168.100.5:8000': <ThreadPrometheusCollector(Thread-8, started 140264723158784)>}
        """
        threading.Thread.__init__(self)
        self.prometheus_collector_dict = prometheus_collector_dict
        self.logger = get_logger("ThreadKeepAlive")
        self.producer = producer
        self._running = True
        self.interval = interval
        self.backward_topic = backward_topic

    def run(self):
        self.logger.info("Agent started keepalive proccess")
        while self._running:
            self.producer.poll(0)
            keepalive_message = {"object_type": "keepalive",
                                 "status": "OK"}
            # Checking if present erros in prometheus_collector_dict
            for key, value in self.prometheus_collector_dict.items():
                if value.get_error() is None:
                    continue
                # if present change keepalive_message add information about
                self.logger.error(value.get_error())
                keepalive_message["status"] = "Error"
                # adds field  errors_messages to keepalive_message
                """
                <class 'list'>: [{'object_type': 'Error_prometheus_collector',
                                'prometheus_collector_id': '192.168.100.5:8001', 
                                'url': 'http://192.168.100.5:8001/metrics', 
                                'error': "HTTPConnectionPool(host='172.18.194.42', port=8001): Max retries.......
                                'prometheus_url_suffix': '/metrics/job/kafka_job2/instance/192.168.100.1:5000'}]
                """
                if "errors_messages" not in keepalive_message.keys():
                    keepalive_message.update({"errors_messages": []})
                keepalive_message['errors_messages'].append(value.get_error())
            data = json.dumps(keepalive_message)
            self.producer.produce(self.backward_topic, data.encode('utf-8'), callback=self.delivery_report)
            self.logger.debug("Agent send keepalive")
            self.producer.flush()
            # wait for "inteval" unit seconds
            sleep(self.interval)

    def delivery_report(self, err, msg):
        """
            Reports message delivery status to Kafka server
        """
        if err is not None:
            self.logger.error('Message delivery failed: {}'.format(err))
        else:
            self.logger.debug('Message delivered to {} [{}]'.format(self.backward_topic, msg.partition()))

    def terminate(self):
        """
            Call this method to stop current thread
        """
        self._running = False


class ThreadPrometheusCollector(threading.Thread):
    """
        The class takes information from configurated Prometheus node exporter and send data to Kafka Server
    """
    def __init__(self, config):
        """
        :type config: The dictionary which contains values to start this class
            <class 'dict'>: {'host': '192.168.100.5',
                             'interval': '1',
                             'prometheus_topic': 'prometheus',
                             'labels': [{'key': 'instance',
                                         'value': '192.168.100.1:5000'}],
                             'port': '8000',
                             'node_url_suffix': '/metrics',
                             'prometheus_job': 'kafka_job'}
        """
        threading.Thread.__init__(self)
        self.host = config['host']
        self.port = config['port']
        self.local_logger = get_logger("ThreadPrometheusCollector: " + self.host + ":" + self.port)
        self.local_logger.info("Started")
        self.interval = config['interval']
        self.node_url_suffix = config['node_url_suffix']
        self.url = "http://" + self.host + ":" + self.port + config['node_url_suffix']
        self._running = True
        self.producer = None
        self.prometheus_topic = config['prometheus_topic']
        self.labels = config['labels']
        self.prometheus_job = config['prometheus_job']
        self.error = None

    def run(self):
        """
        This method requests information from Prometheus node exporter
        and puts received information to Kafka Topic

        """
        msg = "\nPrometheus collector started with parameters\n" \
              "host: {}\n" \
              "port: {}\n" \
              "interval: {}\n" \
              "Prometheus Job: {}\n" \
              "Kafka Topic: {}\n" \
              "Labels: {}".format(self.host, self.port, self.interval,
                                  self.prometheus_job, self.prometheus_topic,
                                  json.dumps(self.labels, indent=4))
        self.local_logger.info(msg)

        prometheus_url_suffix = "/metrics/job/" + self.prometheus_job
        for label in self.labels:
            prometheus_url_suffix = prometheus_url_suffix + "/" + label['key'] + "/" + label['value']
        while self._running:
            try:
                # Requests to Prometheus Node exporter
                metrics = requests.get(self.url)
            except Exception as e:
                # If the result of the request to Prometheus node exporter is error
                key = str(self.host) + ":" + str(self.port)
                error_message = {'object_type': 'Error_prometheus_collector',
                                 'prometheus_collector_id': key,
                                 'url': self.url,
                                 'error': str(e),
                                 'prometheus_url_suffix': prometheus_url_suffix}
                self.error = error_message
            else:
                # If the result of the request to Prometheus node exporter is without error
                self.error = None
                kafka_message = {'key': prometheus_url_suffix,
                                 'value': metrics.text}
                topic = self.prometheus_topic
                # Sends received data from Prometheus node exporter to Kafka
                self.producer.poll(0)
                data = json.dumps(kafka_message)
                self.producer.produce(topic, data.encode('utf-8'), callback=self.delivery_report)
                self.producer.flush()
                sleep(int(self.interval))

    def get_error(self):
        """
            returns current error
        """
        return self.error

    def terminate(self):
        """
            Call this method to stop current thread
        """
        self._running = False

    def set_producer(self, producer):
        """
            sets kafka Procedure
        """
        self.producer = producer

    def delivery_report(self, err, msg):
        """
            Reports message delivery status to Kafka server
        """
        if err is not None:
            self.local_logger.error('Message delivery failed: {}'.format(err))
        else:
            self.local_logger.debug('Message delivered to {} [{}]'.format(self.prometheus_topic, msg.partition()))


class RVMAgent(object):
    """
        The main class of RVM agent

    """
    def __init__(self, agent_config):
        """
        :type agent_config: String Contains path to configuration file
        Example of configuration file config.json
            {
            "name": "vm_agent_1",
            "workdir": "/home/ubuntu/vm_agent_1/work",
            "user": "ubuntu",
            "logfile": "LOGFILE",
            "backward_topic": "vm_agent_1_backward",
            "forward_topic": "vm_agent_1_forward",
            "bootstrap_servers": "192.168.100.1",
            "collectors": {
                "192.168.100.5:8000": {
                    "host": "192.168.100.5",
                    "interval": "1",
                    "prometheus_topic": "prometheus",
                    "labels": [
                        {
                            "key": "instance",
                            "value": "192.168.100.1:5000"
                        }
                    ],
                    "port": "8000",
                    "node_url_suffix": "/metrics",
                    "prometheus_job": "kafka_job"
                }
             }
        """
        self.agent_config = agent_config
        self.lgr = get_logger("RVM_agent")
        with open(agent_config) as f:
            params = json.load(f)
        self.params = params
        self.bootstrap_servers = params.get('bootstrap_servers')
        self.forward_topic = params.get('forward_topic')
        self.backward_topic = params.get('backward_topic')
        self.log_file_name = params.get('logfile')
        self.prometheus_collector_dict = {}
        self.consumer = Consumer({
            'bootstrap.servers': self.bootstrap_servers,
            'group.id': self.forward_topic,
            'auto.offset.reset': 'earliest'
        })
        self.producer = Producer({'bootstrap.servers': self.bootstrap_servers})
        self.consumer.subscribe([self.forward_topic])
        self.lgr.info("Agent uses bootstrap servers: %s" % self.bootstrap_servers)
        self.lgr.info("Agent uses forward topic: %s" % self.forward_topic)
        self.lgr.info("Agent uses backward topic: %s" % self.backward_topic)
        self.lgr.info("Agent uses group.id: %s" % self.forward_topic)
        # Starts KeepAlive thread
        self.thread_keepalive = ThreadKeepAlive(2, self.producer, self.backward_topic, self.prometheus_collector_dict)
        self.thread_keepalive.start()
        # Loads configuration Prometheus collector configuration and starts  Prometheus collector thread
        self.load_prometheus_collectors()
        # Processes  received messages
        self.check_messages()

    def load_prometheus_collectors(self):
        """
           Loads configuration Prometheus collector configuration and starts  Prometheus collector thread
           loads configuration from self.params['collectors']
           Example of  self.params['collectors'] value
           <class 'dict'>: {'192.168.100.5:8000':
                                {'host': '192.168.100.5',
                                 'interval': '1',
                                  'prometheus_topic': 'prometheus',
                                   'labels': [{'key': 'instance', 'value': '192.168.100.1:5000'}],
                                   'port': '8000',
                                   'node_url_suffix': '/metrics',
                                   'prometheus_job': 'kafka_job'},
                            '192.168.100.5:8001':
                                {'host': '192.168.100.5',
                                 'interval': '1',
                                 'prometheus_topic': 'prometheus',
                                 'labels': [{'key': 'instance', 'value': '192.168.100.1:5000'}],
                                 'port': '8001',
                                 'node_url_suffix': '/metrics',
                                 'prometheus_job': 'kafka_job2'}
                             }
        """
        # Checks if the configuration is of Prometheus collectors
        if "collectors" in self.params.keys():

            for key, value in self.params['collectors'].items():
                prometheus_collector = ThreadPrometheusCollector(value)
                prometheus_collector.set_producer(self.producer)
                if key in self.prometheus_collector_dict.keys():
                    self.prometheus_collector_dict[key].terminate()
                    del (self.prometheus_collector_dict[key])
                self.prometheus_collector_dict.update({key: prometheus_collector})
                prometheus_collector.start()
            # deletes configuration for Prometheus collectors to avoid double loading and saving information about
            # Prometheus collectors
            del (self.params['collectors'])

    def save_configuration_to_file(self):
        """
            Saves Prometheus Collectors configuration to config.json
            Example of configuration file config.json
                {
                "name": "vm_agent_1",
                "workdir": "/home/ubuntu/vm_agent_1/work",
                "user": "ubuntu",
                "logfile": "LOGFILE",
                "backward_topic": "vm_agent_1_backward",
                "forward_topic": "vm_agent_1_forward",
                "bootstrap_servers": "192.168.100.1",
                "collectors": {
                    "192.168.100.5:8000": {
                        "host": "192.168.100.5",
                        "interval": "1",
                        "prometheus_topic": "prometheus",
                        "labels": [
                            {
                                "key": "instance",
                                "value": "192.168.100.1:5000"
                            }
                        ],
                        "port": "8000",
                        "node_url_suffix": "/metrics",
                        "prometheus_job": "kafka_job"
                    }
                 }
        """
        config_for_saving = copy.copy(self.params)
        prometheus_collector_dict = {}
        for key, value in self.prometheus_collector_dict.items():
            colector_parameters = {
                "host": value.host,
                "interval": value.interval,
                "prometheus_topic": value.prometheus_topic,
                "labels": value.labels,
                "port": value.port,
                "node_url_suffix": value.node_url_suffix,
                "prometheus_job": value.prometheus_job
            }
            prometheus_collector_dict.update({key: colector_parameters})
        config_for_saving.update({"collectors": prometheus_collector_dict})
        with open(self.agent_config, "w") as f:
            json.dump(config_for_saving, f, indent=4)

    def check_messages(self):
        """
            This method checks messages in Kafka Topic
        """
        self.lgr.info("Agent started")
        while True:
            msg = self.consumer.poll(1.0)
            if msg is None:
                self.lgr.debug("Agent don't receive message")
                continue
            if msg.error():
                self.lgr.warn("Consumer error: {}".format(msg.error()))
                continue
            message = json.loads(msg.value().decode('utf-8'))
            self.lgr.info("Agent received Message")
            self.lgr.info("\n" + json.dumps(message, indent=4))
            message_answer = self.analyze_message(message)
            self.lgr.info("Agent prepeared the answer on the message")
            self.lgr.info("\n" + json.dumps(message_answer, indent=4))
            self.return_to_server(message_answer)

    def execute_command(self, command_dict):
        """
        
        :type command_dict: Dictionary example
            <class 'dict'>: {'args': ['arg1_value', 'arg2_value'],
                             'env': {'MY_ENV_VARIABLE': 'MY_ENV_VARIABLE_VALUE'},
                             'cwd': '/tmp',
                             'body': ['#! /bin/bash -e',
                                      'date',
                                      'date',
                                      'echo $1',
                                      'exit 0'],
                             'agent_id': 'vm_agent_1',
                             'command_id': 1,
                             'type_message': 'bash_script',
                             'object_type': 'command'}
        :rtype: return_value example
        <class 'dict'>: {'returncode': 0,
                         'outs': 'Ср мар  4 11:32:51 EET 2020\nСр мар  4 11:32:51 EET 2020\narg1_value\n',
                         'errs': '',
                         'agent_id': 'vm_agent_1',
                         'command_id': 1,
                         'object_type': 'command_response'}
        """
        on_posix = True
        script_path = tempfile.mktemp()
        script_body = '\n'.join(command_dict['body'])
        with open(script_path, 'w') as f:
            f.write(script_body)
        os.chmod(script_path, 0o744)
        args = command_dict['args']
        command = ' '.join(["sudo " + script_path] + args)
        env = os.environ.copy()
        env.update(command_dict['env'])
        process = subprocess.Popen(
            args=command,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            env=env,
            cwd=command_dict['cwd'],
            bufsize=1,
            close_fds=on_posix)
        outs, errs = process.communicate()
        outs = outs.decode('utf-8')
        errs = errs.decode('utf-8')
        self.lgr.info('outs message: {}'.format(outs))
        self.lgr.info('errs message: {}'.format(errs))
        self.lgr.info('returncode message: {}'.format(process.returncode))
        process.stderr.close()
        process.stdout.close()
        return_value = {
            'returncode': process.returncode,
            'outs': outs,
            'errs': errs,
            'agent_id': command_dict['agent_id'],
            'command_id': command_dict['command_id'],
            'object_type': 'command_response'
        }
        return return_value

    def delivery_report(self, err, msg):
        """
            Reports message delivery status to Kafka server
        """
        if err is not None:
            self.lgr.info('Answer delivery failed: {}'.format(err))
        else:
            self.lgr.info('Answer delivered to {} [{}]'.format(self.backward_topic, msg.partition()))

    def return_to_server(self, result):
        """
            Sends a message to Backward Kafka Topic

        :type result: object The structure of the result can be different
        """
        self.producer.poll(0)
        data = json.dumps(result)
        self.producer.produce(self.backward_topic, data.encode('utf-8'), callback=self.delivery_report)
        self.lgr.info("Agent transmitted the answer")
        self.producer.flush()

    def add_prometheus_collector(self, config_prometheus_collector):
        """
        :type config_prometheus_collector Dictionary example
            <class 'dict'>: {'host': '127.0.0.1',
                            'port': '9100',
                            'interval': '1',
                            'labels': [{'key': 'instance',
                                        'value': 'vm_agent_1:9100'}],
                            'collector_id': '127.0.0.1:9100',
                            'agent_id': 'vm_agent_1',
                            'prometheus_topic': 'prometheus',
                            'node_url_suffix': '/metrics',
                            'prometheus_job': 'vm_agent_1',
                            'object_type': 'add_prometheus_collector'}

        :rtype: return_message Dictionary example
            {
                "host": "127.0.0.1",
                "port": "9100",
                "interval": "1",
                "labels": [
                    {
                        "key": "instance",
                        "value": "vm_agent_1:9100"
                    }
                ],
                "collector_id": "127.0.0.1:9100",
                "agent_id": "vm_agent_1",
                "prometheus_topic": "prometheus",
                "node_url_suffix": "/metrics",
                "prometheus_job": "vm_agent_1",
                "object_type": "added_prometheus_collector"
            }
        """
        host = config_prometheus_collector['host']
        port = config_prometheus_collector['port']
        # Creates Prometheus Collector Thread
        prometheus_collector = ThreadPrometheusCollector(config_prometheus_collector)
        prometheus_collector.set_producer(self.producer)
        key = str(host) + ":" + str(port)
        if key in self.prometheus_collector_dict.keys():
            self.prometheus_collector_dict[key].terminate()
            del (self.prometheus_collector_dict[key])
        self.prometheus_collector_dict.update({key: prometheus_collector})
        # Saves RVM agent configuration to file
        self.save_configuration_to_file()
        # Starts Prometheus Collector Thread
        prometheus_collector.start()
        return_message = copy.copy(config_prometheus_collector)
        return_message.update({'object_type': "added_prometheus_collector"})
        return return_message

    def delete_prometheus_collector(self, message):
        """
        :type message Dictionary example
            {
                "object_type": "delete_prometheus_collector",
                "agent_id": "vm_agent_1",
                "collector_id": "127.0.0.1:9100"
            }
        :rtype: object Dictionary example
            {
                "object_type": "deleted_prometheus_collector",
                "agent_id": "vm_agent_1",
                "collector_id": "127.0.0.1:9100"
            }
            or
            {
                "object_type": "deleted_prometheus_collector",
                "status": "Collector with ID:127.0.0.1:9100 wasn't found"
            }
        """
        collector_id = message['collector_id']
        if collector_id in self.prometheus_collector_dict.keys():
            self.prometheus_collector_dict[collector_id].terminate()
            del (self.prometheus_collector_dict[collector_id])
            self.save_configuration_to_file()
            retrun_message = {
                'object_type': "deleted_prometheus_collector",
                'agent_id': message['agent_id'],
                'collector_id': collector_id
            }
            return retrun_message
        retrun_message = {
            'object_type': "deleted_prometheus_collector",
            'status': "Collector with ID:{} wasn't found".format(collector_id)
        }
        return retrun_message

    def analyze_message(self, message):
        """
            Analyzes received message and call the appropriate method

        :type message: object The structure of the message can be different
        """
        if message.get('object_type') == "command":
            result = self.execute_command(message)
            return result
        elif message.get('object_type') == "add_prometheus_collector":
            result = self.add_prometheus_collector(message)
            return result
        elif message.get('object_type') == "delete_prometheus_collector":
            result = self.delete_prometheus_collector(message)
            return result
        else:
            self.lgr.error("This objest type is not known")


@click.option('--agent_config',
              help='The name of the daemon. [env {0}]'
              .format("AGENT_CONFIG"),
              required=True,
              envvar="AGENT_CONFIG")
@click.command()
def main(agent_config):
    if os.path.exists(agent_config) is not True:
        print("Error:" + agent_config + " file doesn't exist")
        exit(1)
    RVMAgent(agent_config)

if __name__ == "__main__":
    main()
