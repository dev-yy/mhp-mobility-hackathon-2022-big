# Introduction
This example demonstrates an self managed Kafka Broker on https://www.cloudkarafka.com/.
The lambda is deployed with serverless framework which subscribes on topic.
Every time data is arrived on topic the lambda function is invoked via Kafka trigger in Lambda Service.

