var port_probe = 11001
var hostname_probe = $(location).attr('hostname');
var probe_mode = "check"
//mode "check" client probe makes GET request, measures response time and reports data to probe server by using POST request
//mode "report_metrics" client probe sends video player's metrics to probe server