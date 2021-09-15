# Configuration

This directory contains examples for data generation configuration.

As previously noted, it's important to know when to POST vs PUT to REST API and when
to wrap the configuration JSON in the `config` element or not.

POST = starting brand new data generation task and should be wrapped in `config` element
PUT = updating the configuration of an existing running generator and should not be wrapped in `config`

This follows Kafka Connect REST API mechanism.

