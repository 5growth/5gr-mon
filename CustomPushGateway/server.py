from flask import Flask, request, Response

app = Flask(__name__ )

datadase = {}

@app.route('/metrics/<path:labels>', methods=['POST'])
def receive_metrics(labels):
    labels_dict = {}
    labels_list = labels.split("/")
    for i in range(0, len(labels_list)-1, 2):
        labels_dict.update({labels_list[i]: labels_list[i + 1]})
    data = request.data
    datadase.update({labels_dict['instance']: {"data": data , "get_num": 4}})
    return "", 201

@app.route('/metrics/<string:instance>', methods=['GET'])
def transmit_metrics_to_prometheus(instance):
    if instance in datadase.keys():
        data_obj = datadase[instance]
        data_obj['get_num'] = data_obj['get_num'] - 1
        return_data = data_obj['data']
        if data_obj['get_num'] <= 0:
            del(datadase[instance])
        return return_data, 200, {'Content-Type': 'text/plain; charset=utf-8'}
    else:
        return "", 404


def cusctom_push_gateway():
    app.run(host= "0.0.0.0", port=9091, debug=True)

if __name__ == "__main__":
    cusctom_push_gateway()
