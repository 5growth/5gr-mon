# HTTP server image

Docker image containing the HTTP server, microservice that allows to test alerts are received correctly.

## Build the image

```sh
$ docker build -t http_server .
```

## Run the image

```sh
$ docker run --name <container_name> -p 8000:8000 -t -d http_server
```

Where:

* **container_name:** name for the container to be deployed.

## Checking the correct deployment of the service

By executing the following command, the correct deployment of the service can be checked. In case of not receiving a message, it means that everything went fine.

```sh
$ curl --location --request GET 'http://<container_ip>:8000'
```
