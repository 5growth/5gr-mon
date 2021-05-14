# Deployments

This folder contains some scenarios to check the correct behaviour of the Monitoring platform in different deployments.

## Structure of the repository

Each folder corresponds to a specific deployment of the Dockerized environment.

* **[0_microservices_deployment_kubernetes_elastalert_demo](0_microservices_deployment_kubernetes_elastalert_demo):** Scenario used for the 5GROWTH WP2 meeting demo.
* **[1_microservices_deployment_kubernetes](1_microservices_deployment):** Scenario to deploy the full 5GROWTH Monitoring Platform - Log Monitoring Pipeline stack.
* **[2_microservices_deployment_kubernetes_dns](2_microservices_deployment_kubernetes_dns):** Scenario to deploy the full 5GROWTH Monitoring Platform - Log Monitoring Pipeline stack, using Kubernetes DNS feature.
* **[3_microservices_deployment_kubernetes_dns_automated](3_microservices_deployment_kubernetes_dns_automated):** Scenario to deploy the full 5GROWTH Monitoring Platform - Log Monitoring Pipeline stack, using Kubernetes DNS feature with all the commands provided in pods' specifications.
* **[4_microservices_deployment_kubernetes_dns_automated_production](4_microservices_deployment_kubernetes_dns_automated_production):** Scenario to deploy the full 5GROWTH Monitoring Platform - Log Monitoring Pipeline stack, using Kubernetes DNS feature with all the commands provided in pods' specifications. This scenario does not use Kafka, ZooKeeper and the HTTP Server, so it's ready to interact with external components in a production environment.
