# ElastAlert Docker image

Docker image with [ElastAlert](https://elastalert.readthedocs.io/en/latest/) installed, to manage alarms based on data saved in Elasticsearch.

## Build the image

```sh
$ docker build -t elastalert:v2.6 .

## Run the image

```sh
$ docker run --name <container_name> -t -d elastalert:v2.6
```

Where:

* **container_name:** name for the container to be deployed.
