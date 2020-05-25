# RVM agent

RVM agent features:
- Bash script code execution on remote VM and providing the execution result
- Keepalive mechanism
- RVM agent Prometheus Collectors management (create/delete)
- RVM agent configuration management (avoid config loss after restart)

## Building

In the `create_package` folder, run `makearchive.sh` (requires docker, python 3)

## Copy

In the same root folder copy files:
ubuntu-bionic-agent.tar.gz
ubuntu-xenial-agent.tar.gz
to 5 gr-mon folder `fileserver/agents`
