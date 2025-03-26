# Kafka Banking Architecture

A multi-region event-driven architecture built with Apache Kafka and Spring Boot to process SWIFT MT/MX messages in real time across APAC, EMEA, and NAM.

## Modules
- kafka-banking-standard/
- kafka-banking-reactive/
- api-gateway-nginx/
- keycloak-docker/
- tls/
- consul/
- docs/

## Deployment
- Docker + systemd on Linux
- TLS/mTLS
- Consul for service registry
- Prometheus + Grafana monitoring
