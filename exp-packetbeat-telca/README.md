#How to use
##Build
`sudo docker build -t packetbeat .`

##Run
Under Docker, Packetbeat runs as a non-root user, but requires some privileged network capabilities to operate correctly. Ensure that the NET_ADMIN capability is available to the container.

By default, Docker networking will connect the Packetbeat container to an isolated virtual network, with a limited view of network traffic. You may wish to connect the container directly to the host network in order to see traffic destined for, and originating from, the host system. With docker run, this can be achieved by specifying --network=host.

`sudo docker run --cap-add=NET_ADMIN --network=host packetbeat`
##Configuration
```
packetbeat.interfaces.device: eth0
output.kafka:
  hosts: ["kafka:9092"]
  topic: 'packetbeat'
```