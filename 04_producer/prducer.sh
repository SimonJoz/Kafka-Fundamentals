#!/bin/bash

# Producing to a non existing topic
kafka-console-producer --bootstrap-server localhost:9092 --topic new_topic
# > Message 1
# > Message 2
# > Message 3

# Producing to a existing topic
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic
# > Hello World
# > Kafka is awesome
# > Java rocks!
# > Xebia - Where Experts Grow

# Producing with configs
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --producer-property acks=all

# Producing messages with keys
kafka-console-producer --bootstrap-server localhost:9092 \
--topic first_topic \
--property parse.key=true \
--property key.separator=:

# > Key_1:Value1
# > Key_2:Value2
# > Key_3:Value3
