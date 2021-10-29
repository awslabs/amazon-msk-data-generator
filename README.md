# Amazon MSK Data Generator

MSK Data Generator is a translation of the *awesome* Voluble Apache Kafka
data generator from Clojure to Java.  (Link in Resources Section below)

The killer feature is being able to generate
events which reference other generated events.  (AKA: cross-reference, reference-able, joinable, etc.)

For example, we can generate one stream of Order events containing a customer_id (as well as price, sku, quantity, etc.)
and at same time, we can generate a different stream of Customer events containing a customer_id (as well as first name, last name, location, etc.)
The dynamically generated Customer event customer_id can reference the Order event customer_id.

#### Why this matters?

Multiple streams of "joinable" data is especially useful when building
stream processor applications (in `Kinesis Data Analytics for Apache Flink` or `Kinesis Data
Analytics Studio` for example) which perform joins.

For an example, see AWS Big Data Blog [Query your Amazon MSK topics interactively using Amazon Kinesis Data Analytics Studio](https://aws.amazon.com/blogs/big-data/query-your-amazon-msk-topics-interactively-using-amazon-kinesis-data-analytics-studio/)

#### Why translate to Java?

By translating to Java, the hope is we open up the potential of wider community
collaboration.  (Nothing against Clojure mind you!  It's just more folks know Java.)

This project can likely be used outside of Amazon MSK, but to start at least, the focus will be making
this generator easy to use with Amazon MSK.

#### Further Context

MSK Data Generator is deployed and configured as a Kafka Connect _Source_,
so basic knowledge of Kafka Connect will be helpful.

Like many dynamic data generation projects, the key component is the use
of Java Faker library.  Knowing more about Java Faker capabilities and options will be helpful.  
See link in Resources section below.

## Getting Started

MSK Data Generator can be deployed in a variety of ways including:

* [Deploying in a container running in Elastic Container Service](./docs/msk-data-gen-container-deploy.md)

* [Deploying as a Kafka Connect source connector in MSK Connect](./docs/msk-connect-deploy.md)

## Customizing Data Generation Configuration

There are 5 essential constructs to understand when customizing key-value data generation:

1. **Directives** `genk`, `genkp`, `genv`, and `genvp`

2. **Generators** `with` or `matching`

3. **Attribute** the name of the field to generate data

4. **Qualifiers** `sometimes`

5. **Expressions** based on Java faker

For example, consider the configuration of the following:

```
"genkp.customer.with": "#{Internet.uuid}",
"genv.customer.name.with": "#{Name.full_name}",
"genv.customer.gender.with": "#{Demographic.sex}",
"genv.customer.favorite_beer.with": "#{Beer.name}",
"genv.customer.state.with": "#{Address.state}",

"genkp.order.with": "#{Internet.uuid}",
"genv.order.product_id.with": "#{number.number_between '101','109'}",
"genv.order.quantity.with": "#{number.number_between '1','5'}",
"genv.order.customer_id.matching": "customer.key"
```

This config will generate data to the `customer` and `customer` topics and _assumes_ the MSK cluster has been configured to allow auto topic creation OR the `customer` and `order` topics have already been created.

For example, the above configuration will create 2 events with every iteration similar to the following:

`customer` event with a key of `0c88cbb7-eb4a-44f0-83aa-00957761b3b6` (because Internet.uuid for random string from Java Faker) and JSON payload of

```
{
   "favorite_beer": "Weihenstephaner Hefeweissbier",
   "gender": "Male",
   "name": "Miss Gilbert Luettgen",
   "state": "Oregon"
}
```

`order` event with a random string key of `dc236186-9037-45a0-8b91-a3c2b50f0582` (again, because of Internet.uuid)
and a JSON payload of

```
{
   "quantity": "4",
   "product_id": "132",
   "customer_id": "0c88cbb7-eb4a-44f0-83aa-00957761b3b6"
}
```

Notice how the `order` event `customer_id` value references the previously generated `customer` key field?  (Hint: with this kind of data generation, we can test our join code!)

This also highlights the differences between `with` and `matching` in configuration.

In this example, `with` is utilizing and methods available from Java Faker see [API docs](https://dius.github.io/java-faker/apidocs/) and then compare the class methods with configuration above such as `Name.full_name`, `Beer.name`, etc.


With this example above and the 5 previously mentioned essential constructs in mind, the sequence is:

`directive.topic.attribute-or-qualifier.generator: expression`

For further information on data generation configuration options, check both the Voluble README as well as some of the
[examples in this repo](./examples/)


## External References

* Voluble (basis for this project) https://github.com/MichaelDrogalis/voluble

* Java Faker https://github.com/DiUS/java-faker

* Java Faker API docs https://dius.github.io/java-faker/apidocs/
