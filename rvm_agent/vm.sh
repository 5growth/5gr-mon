#!/bin/bash
curl --location --request POST 'http://192.168.100.1:8989/prom-manager/agent' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data-raw '{
"install_method": "cloud_init",
"description": "VNF_1",
"daemon_user": "ubuntu"
}' | \
python3 -c "import sys, json; print(json.load(sys.stdin)['cloud_init_script'])" > user_data.txt
chmod +x user_data.txt
bash user_data.txt
