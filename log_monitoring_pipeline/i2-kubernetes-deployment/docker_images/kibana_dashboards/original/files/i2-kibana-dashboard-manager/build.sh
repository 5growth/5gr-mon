#!/bin/bash
cd kibana-dashboards
mvn clean install -DskipTests
cd ..
mvn clean install -DskipTests