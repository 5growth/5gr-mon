#!/bin/bash
curl --location --request POST 'http://127.0.0.1:8989/prom-manager/agent' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data-raw '{
"install_method": "cloud_init",
"description": "VNF_1",
"daemon_user": "ubuntu"
}' | \
    python3 -c "import sys, json; print(json.load(sys.stdin)['cloud_init_script'])" > user_data.txt

#python init_script2.py > user_data.txt
openstack server create VM_INSTANCE2 --image SPR2_open --flavor flavor_spr2 --network 	private --key-name cloudify-key --user-data user_data.txt