#!/bin/sh

#export KAFKA_OPTS=-javaagent:/opt/jmx_prometheus_javaagent.jar=3600:/opt/prom-jmx-config.yml
# Strating Kafka connect

# Echo for debugging. You can comment/delete the 1st and 3rd lines once everything is working.
echo -e "Current file contents:\n $(cat /etc/hosts)"
echo "$DETECTED_IP $DETECTED_HOSTNAME" >> /etc/hosts
echo -e "\n\n\nUpdated file contents:\n $(cat /etc/hosts)"

echo "BOOT: $CONNECT_BOOTSTRAP_SERVERS USER: $USERNAME PASS: ${PASSWORD} GROUP: $CONNECT_GROUP_ID"
sed -i "s/BROKERS/${CONNECT_BOOTSTRAP_SERVERS}/g" /opt/connect-distributed.properties
sed -i "s/GROUP/${CONNECT_GROUP_ID}/g" /opt/connect-distributed.properties
sed -i "s/USERNAME/${USERNAME}/g" /opt/connect-distributed.properties
sed -i "s/PASSWORD/${PASSWORD}/g" /opt/connect-distributed.properties

#if [[ -z "$BOOTSTRAP_SERVERS" ]]; then
#    # Look for any environment variables set by Docker container linking. For example, if the container
#    # running Kafka were aliased to 'kafka' in this container, then Docker should have created several envs,
#    # such as 'KAFKA_PORT_9092_TCP'. If so, then use that to automatically set the 'bootstrap.servers' property.
#    BOOTSTRAP_SERVERS=$(env | grep .*PORT_9092_TCP= | sed -e 's|.*tcp://||' | uniq | paste -sd ,)
#fi
#
#if [[ "x$BOOTSTRAP_SERVERS" = "x" ]]; then
#    # export BOOTSTRAP_SERVERS=0.0.0.0:9092
#		export BOOTSTRAP_SERVERS=kafka1:19092
#
#fi
#
#echo "Using BOOTSTRAP_SERVERS=$BOOTSTRAP_SERVERS"

echo Starting Kafka connect

: ${REST_PORT:=8083}
: ${REST_HOST_NAME:=$HOST_NAME}
: ${ADVERTISED_PORT:=8083}
: ${ADVERTISED_HOST_NAME:=$HOST_NAME}
: ${GROUP_ID:=1}
: ${OFFSET_FLUSH_INTERVAL_MS:=60000}
: ${OFFSET_FLUSH_TIMEOUT_MS:=5000}
: ${SHUTDOWN_TIMEOUT:=10000}
: ${KEY_CONVERTER:=org.apache.kafka.connect.json.JsonConverter}
: ${VALUE_CONVERTER:=org.apache.kafka.connect.json.JsonConverter}
: ${INTERNAL_KEY_CONVERTER:=org.apache.kafka.connect.json.JsonConverter}
: ${INTERNAL_VALUE_CONVERTER:=org.apache.kafka.connect.json.JsonConverter}
: ${OFFSET_STORAGE_TOPIC:=connect-offsets-1}
: ${CONFIG_STORAGE_TOPIC:=connect-configs-1}

export CONNECT_REST_ADVERTISED_PORT=$ADVERTISED_PORT
export CONNECT_REST_ADVERTISED_HOST_NAME=$ADVERTISED_HOST_NAME
export CONNECT_REST_PORT=$REST_PORT
export CONNECT_REST_HOST_NAME=$REST_HOST_NAME
export CONNECT_BOOTSTRAP_SERVERS=$CONNECT_BOOTSTRAP_SERVERS
export CONNECT_GROUP_ID=$GROUP_ID
export CONNECT_CONFIG_STORAGE_TOPIC=$CONFIG_STORAGE_TOPIC
export CONNECT_OFFSET_STORAGE_TOPIC=$OFFSET_STORAGE_TOPIC
if [[ -n "$STATUS_STORAGE_TOPIC" ]]; then
    export CONNECT_STATUS_STORAGE_TOPIC=$STATUS_STORAGE_TOPIC
fi
export CONNECT_KEY_CONVERTER=$KEY_CONVERTER
export CONNECT_VALUE_CONVERTER=$VALUE_CONVERTER
export CONNECT_INTERNAL_KEY_CONVERTER=$INTERNAL_KEY_CONVERTER
export CONNECT_INTERNAL_VALUE_CONVERTER=$INTERNAL_VALUE_CONVERTER
export CONNECT_TASK_SHUTDOWN_GRACEFUL_TIMEOUT_MS=$SHUTDOWN_TIMEOUT
export CONNECT_OFFSET_FLUSH_INTERVAL_MS=$OFFSET_FLUSH_INTERVAL_MS
export CONNECT_OFFSET_FLUSH_TIMEOUT_MS=$OFFSET_FLUSH_TIMEOUT_MS
if [[ -n "$HEAP_OPTS" ]]; then
    export KAFKA_HEAP_OPTS=$HEAP_OPTS
fi
unset HOST_NAME
unset REST_PORT
unset REST_HOST_NAME
unset ADVERTISED_PORT
unset ADVERTISED_HOST_NAME
unset GROUP_ID
unset OFFSET_FLUSH_INTERVAL_MS
unset OFFSET_FLUSH_TIMEOUT_MS
unset SHUTDOWN_TIMEOUT
unset KEY_CONVERTER
unset VALUE_CONVERTER
unset INTERNAL_KEY_CONVERTER
unset INTERNAL_VALUE_CONVERTER
unset HEAP_OPTS
unset MD5HASH
unset SCALA_VERSION


case $1 in
    start)
        if [[ "x$CONNECT_BOOTSTRAP_SERVERS" = "x" ]]; then
            echo "The BOOTSTRAP_SERVERS variable must be set, or the container must be linked to one that runs Kafka."
            exit 1
        fi

        if [[ "x$CONNECT_GROUP_ID" = "x" ]]; then
            echo "The GROUP_ID must be set to an ID that uniquely identifies the Kafka Connect cluster these workers belong to."
            echo "Ensure this is unique for all groups that work with a Kafka cluster."
            exit 1
        fi

        if [[ "x$CONNECT_CONFIG_STORAGE_TOPIC" = "x" ]]; then
            echo "The CONFIG_STORAGE_TOPIC variable must be set to the name of the topic where connector configurations will be stored."
            echo "This topic must have a single partition, be highly replicated (e.g., 3x or more) and should be configured for compaction."
            exit 1
        fi

        if [[ "x$CONNECT_OFFSET_STORAGE_TOPIC" = "x" ]]; then
            echo "The OFFSET_STORAGE_TOPIC variable must be set to the name of the topic where connector offsets will be stored."
            echo "This topic should have many partitions (e.g., 25 or 50), be highly replicated (e.g., 3x or more) and be configured for compaction."
            exit 1
        fi

        if [[ "x$CONNECT_STATUS_STORAGE_TOPIC" = "x" ]]; then
            echo "WARNING: it is recommended to specify the STATUS_STORAGE_TOPIC variable for defining the name of the topic where connector statuses will be stored."
            echo "This topic may have multiple partitions, be highly replicated (e.g., 3x or more) and should be configured for compaction."
            echo "As no value is given, the default of 'connect-status' will be used."
        fi

        echo "Using the following environment variables:"
        echo "      GROUP_ID=$CONNECT_GROUP_ID"
        echo "      CONFIG_STORAGE_TOPIC=$CONNECT_CONFIG_STORAGE_TOPIC"
        echo "      OFFSET_STORAGE_TOPIC=$CONNECT_OFFSET_STORAGE_TOPIC"
        if [[ "x$CONNECT_STATUS_STORAGE_TOPIC" != "x" ]]; then
            echo "      STATUS_STORAGE_TOPIC=$CONNECT_STATUS_STORAGE_TOPIC"
        fi
        echo "      BOOTSTRAP_SERVERS=$CONNECT_BOOTSTRAP_SERVERS"
        echo "      REST_HOST_NAME=$CONNECT_REST_HOST_NAME"
        echo "      REST_PORT=$CONNECT_REST_PORT"
        echo "      ADVERTISED_HOST_NAME=$CONNECT_REST_ADVERTISED_HOST_NAME"
        echo "      ADVERTISED_PORT=$CONNECT_REST_ADVERTISED_PORT"
        echo "      KEY_CONVERTER=$CONNECT_KEY_CONVERTER"
        echo "      VALUE_CONVERTER=$CONNECT_VALUE_CONVERTER"
        echo "      INTERNAL_KEY_CONVERTER=$CONNECT_INTERNAL_KEY_CONVERTER"
        echo "      INTERNAL_VALUE_CONVERTER=$CONNECT_INTERNAL_VALUE_CONVERTER"
        echo "      OFFSET_FLUSH_INTERVAL_MS=$CONNECT_OFFSET_FLUSH_INTERVAL_MS"
        echo "      OFFSET_FLUSH_TIMEOUT_MS=$CONNECT_OFFSET_FLUSH_TIMEOUT_MS"
        echo "      SHUTDOWN_TIMEOUT=$CONNECT_TASK_SHUTDOWN_GRACEFUL_TIMEOUT_MS"

        # Copy config files if not provided in volume
        cp -rn $KAFKA_HOME/config.orig/* $KAFKA_HOME/config

        #
        # Configure the log files ...
        #
        if [[ -n "$CONNECT_LOG4J_LOGGERS" ]]; then
            sed -i -r -e "s|^(log4j.rootLogger)=.*|\1=${CONNECT_LOG4J_LOGGERS}|g" $KAFKA_HOME/config/log4j.properties
            unset CONNECT_LOG4J_LOGGERS
        fi
        env | grep '^CONNECT_LOG4J' | while read -r VAR;
        do
          env_var=`echo "$VAR" | sed -r "s/([^=]*)=.*/\1/g"`
          prop_name=`echo "$VAR" | sed -r "s/^CONNECT_([^=]*)=.*/\1/g" | tr '[:upper:]' '[:lower:]' | tr _ .`
          prop_value=`echo "$VAR" | sed -r "s/^CONNECT_[^=]*=(.*)/\1/g"`
          if egrep -q "(^|^#)$prop_name=" $KAFKA_HOME/config/log4j.properties; then
              #note that no config names or values may contain an '@' char
              sed -r -i "s@(^|^#)($prop_name)=(.*)@\2=${prop_value}@g" $KAFKA_HOME/config/log4j.properties
          else
              echo "$prop_name=${prop_value}" >> $KAFKA_HOME/config/log4j.properties
          fi
          if [[ "$SENSITIVE_PROPERTIES" = *"$env_var"* ]]; then
              echo "--- Setting logging property from $env_var: $prop_name=[hidden]"
          else
             echo "--- Setting logging property from $env_var: $prop_name=${prop_value}"
          fi
          unset $env_var
        done
        if [[ -n "$LOG_LEVEL" ]]; then
            sed -i -r -e "s|=INFO, stdout|=$LOG_LEVEL, stdout|g" $KAFKA_HOME/config/log4j.properties
            sed -i -r -e "s|^(log4j.appender.stdout.threshold)=.*|\1=${LOG_LEVEL}|g" $KAFKA_HOME/config/log4j.properties
        fi
        export KAFKA_LOG4J_OPTS="-Dlog4j.configuration=file:$KAFKA_HOME/config/log4j.properties"

        #
        # Process all environment variables that start with 'CONNECT_'
        #
        env | while read -r VAR;
        do
          env_var=`echo "$VAR" | sed -r "s/([^=]*)=.*/\1/g"`
          if [[ $env_var =~ ^CONNECT_ ]]; then
            prop_name=`echo "$VAR" | sed -r "s/^CONNECT_([^=]*)=.*/\1/g" | tr '[:upper:]' '[:lower:]' | tr _ .`
            prop_value=`echo "$VAR" | sed -r "s/^CONNECT_[^=]*=(.*)/\1/g"`
						if egrep -q "(^|^#)$prop_name=" /opt/connect-distributed.properties; then

                #note that no config names or values may contain an '@' char
								sed -r -i "s@(^|^#)($prop_name)=(.*)@\2=${prop_value}@g" /opt/connect-distributed.properties

            else
                # echo "Adding property $prop_name=${prop_value}"
                # echo "$prop_name=${prop_value}" >> $KAFKA_HOME/config/connect-distributed.properties
								echo "$prop_name=${prop_value}" >> /opt/connect-distributed.properties
            fi

            if [[ "$SENSITIVE_PROPERTIES" = *"$env_var"* ]]; then
                echo "--- Setting property from $env_var: $prop_name=[hidden]"
            else
                echo "--- Setting property from $env_var: $prop_name=${prop_value}"
            fi
          fi
        done
        ;;
esac

# TBD aws secretsmanager get-secret-value --secret-id AmazonMSK_mary
# echo "After secret manager call"

cd /opt/kafka_2.12-2.7.0/bin
./connect-distributed.sh /opt/connect-distributed.properties
