# Data Generation Customization Configuration Examples

This directory contains examples for data generation configuration.

The examples in this directory depend on how the MSK Generator is being deployed such as

* [Deploying in a container](../docs/msk-data-gen-container-deploy.md)

* [Deploying in MSK Connect](../docs/msk-connect-deploy.md)

Examples in the this directory with file names ending in `.properties` are for MSK Connect deployment.

#### Notes about container deployment examples

When deploying in a container, we interact with the Data Generator with the REST API.  As previously noted, it's important to know when to POST vs PUT to REST API and when
to wrap the configuration JSON in the `config` element or not.

POST = starting brand new data generation task and should be wrapped in `config` element
PUT = updating the configuration of an existing running generator and should not be wrapped in `config`

This follows Kafka Connect REST API mechanism.
