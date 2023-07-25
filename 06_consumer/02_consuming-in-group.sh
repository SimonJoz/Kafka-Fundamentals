#!/bin/bash

# GROUP_1
kafka-console-consumer --bootstrap-server localhost:9092 \
--topic first_topic \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.offset=true \
--property print.partition=true \
--property print.key=true \
--property print.value=true \
--group group_1

# GROUP_2
kafka-console-consumer --bootstrap-server localhost:9092 \
--topic first_topic \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.offset=true \
--property print.partition=true \
--property print.key=true \
--property print.value=true \
--group group_2

# PRODUCER
kafka-console-producer --bootstrap-server localhost:9092 \
--topic first_topic \
--property parse.key=true \
--property key.separator=:

# > Key_1:Value1
# > Key_2:Value2
# > Key_3:Value3
