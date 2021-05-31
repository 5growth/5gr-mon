import datetime
import multiprocessing

from flask import Flask, request, session

from server_probe_prometheus import start_prometheus_server

app = Flask(__name__ )
app.secret_key = b'_5#y2L"F4Q8z\n\xec]/'
app.permanent_session_lifetime = datetime.timedelta(hours=4)
session_id = 0
queue = None

def probe_get():
    if 'visits' in session:
        session['visits'] = session.get('visits') + 1
    else:
        global session_id
        session['visits'] = 1
        session_id = session_id + 1
        session['session_id'] = session_id
    return "Total visits: {} session_id {}".format(session.get('visits'), session.get('session_id'))

def probe_post():
    data = request.json
    session_id = session.get('session_id')
    if 'session_id' in data.keys():
        session_id = data["session_id"]
    data.update({"session_id": session_id})
    data.update({"host": request.remote_addr})
    queue.put(data)
    return data

@app.route('/probe', methods=['GET', 'POST', "OPTIONS"])
def probe():
    if request.method == 'GET':
        return probe_get()
    if request.method == 'POST':
        return probe_post()
    return ""

@app.after_request
def after_request(response):

    host = request.host
    ip_address = host.split(":")[0]
    response.headers.add('Access-Control-Allow-Origin', 'http://' + ip_address)
    response.headers.add('Access-Control-Allow-Credentials', 'true')
    response.headers.add('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS')
    response.headers.add("Access-Control-Allow-Headers",
                     "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    return response


def start_server_probe(in_queue):
    global queue
    queue = in_queue
    app.run(host= "0.0.0.0", port=11001, debug=True, use_reloader=False)

if __name__ == "__main__":

    queue = multiprocessing.Queue()

    proccess_prometheus_server = multiprocessing.Process(target=start_prometheus_server, args=(queue,))
    proccess_prometheus_server.start()
    start_server_probe(queue)
