import time

from prometheus_client.core import GaugeMetricFamily, REGISTRY, CounterMetricFamily
from prometheus_client import start_http_server, CollectorRegistry

queue = None


class SummMessages(object):
    def __init__(self):
        self.dict_sum = {}
        self.dict_number = {}

    def add(self, object):
        session_id = ""
        host = object.get("host")
        del object["host"]
        if "session_id" in object:
            session_id = str(object.get("session_id"))
            del object["session_id"]
        for key, value in object.items():
            if key in self.dict_sum.keys():
                if host in self.dict_sum[key].keys():
                    if session_id in self.dict_sum[key][host].keys():
                        self.dict_sum[key][host][session_id] += value
                        self.dict_number[key][host][session_id] += 1
                    else:
                        self.dict_sum[key][host].update({session_id: value})
                        self.dict_number[key][host].update({session_id: 1})
                else:
                    self.dict_sum[key].update({host: {session_id: value}})
                    self.dict_number[key].update({host: {session_id: 1}})
            else:
                self.dict_sum.update({key: {host:{session_id:value}}})
                self.dict_number.update({key: {host: {session_id: 1}}})

    def get_result(self):
        dict_result = {}
        for parameter, value in self.dict_sum.items():
            for host, value2 in value.items():
                for session_id, value3 in value2.items():
                    number = self.dict_number[parameter][host][session_id]
                    result = round(value3 / number, 1)
                    if parameter in dict_result.keys():
                        dict_result[parameter].append({
                                            "host": host,
                                            "session_id": session_id,
                                            "value": result})
                    else:
                        dict_result.update({parameter: [{
                                            "host": host,
                                            "session_id": session_id,
                                            "value": result}]})
        return  dict_result




class CustomCollector(object):
    def __init__(self):
        pass

    def collect(self):
        summ_m = SummMessages()
        while not queue.empty():
            summ_m.add(queue.get())

        result = summ_m.get_result()
        metrics = []
        for parameter, values in result.items():
            gmf = GaugeMetricFamily(parameter, parameter, labels=['client_host', 'session_id'])
            for value in values:
                gmf.add_metric([value['host'], value['session_id']], value['value'])
            metrics.append(gmf)
        for metric in metrics:
            yield metric


def start_prometheus_server(in_queue):
    global queue

    queue = in_queue
    start_http_server(11000)
    REGISTRY.register(CustomCollector())
    while True:
        time.sleep(1)

if __name__ == '__main__':
    start_prometheus_server(None)