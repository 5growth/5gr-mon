# ElastAlert Docker image

Docker image with [ElastAlert](https://elastalert.readthedocs.io/en/latest/) installed, to manage alarms based on data saved in Elasticsearch.

## Build the image

```sh
$ docker build -t elastalert:v3 .
```

Built w/o using local config files. Intended to use k8s ConfigMaps
