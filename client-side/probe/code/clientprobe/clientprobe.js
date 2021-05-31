var url_probe = "http://" + hostname_probe + ":" + port_probe + "/probe"

async function prepareStatisticsForSending(data, event){
    if (probe_mode == "report_metrics") {
        var latency = Math.round(event.latency)
        var loading = Math.round(event.load)
        var parsing = Math.round(event.parsing)
        var buffering = Math.round(event.buffer)
        var bw = event.bw
        var session_id = data.frag.relurl.match(/\d+$/g)[0];
        metrics = {
                    "latency_ms":  latency,
                    "loading_ms":  loading,
                    "parsing_ms": parsing,
                    "buffering_ms": buffering,
                    "bw_kbps": bw,
                    "session_id": session_id
                    }
        sendStatistic(metrics)
        }
    }

function sendStatistic(data_in){
        console.debug(data_in)
        $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        url: url_probe,
        xhrFields: {
          crossDomain: true,
          withCredentials: true
        },
        data: JSON.stringify(data_in),
        success: function (data, status, jqXHR) {
            console.debug("post");
            console.debug(data);
        }
        })
        }


function doprobe(url) {
  var vid = document.getElementById("video");
  var start_time = new Date().getTime();
    $.ajax({
        type: "GET",
        url: url_probe,
        xhrFields: {
          withCredentials: true
        },
        beforeSend: function(xhr){
           var start_time = new Date().getTime();
        },
        crossDomain: true,
        dataType: "text",
        success: function (data, status, jqXHR) {
            var request_time = new Date().getTime() - start_time;
            console.debug("ajax call data: " + data);
            console.debug("ajax call duration: " + request_time + " ms");
            var data = {"latency_ms": request_time}
            sendStatistic(data)
        }
    });
}

$(function(){
    console.log("Client probe mode: " + probe_mode)
    console.log("Server probe url: " + url_probe)
    if (probe_mode == "check") {
        setInterval(doprobe, 500, url_probe);
    }
});
