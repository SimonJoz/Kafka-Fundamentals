#!/bin/bash

# LIST CONSUMER GROUPS
kafka-consumer-groups --bootstrap-server localhost:9092 --list

# DESCRIBE SPECIFIC GROUP
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group group_1

# DESCRIBE ANOTHER GROUP
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group group_2

# PRODUCER
kafka-console-producer --bootstrap-server localhost:9092 \
--topic first_topic \
--property parse.key=true \
--property key.separator=:

# > Key_1:Value1
# > Key_2:Value2
# > Key_3:Value3

# START CONSUMER
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group group_1

# DESCRIBE GROUP AGAIN
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group group_1
