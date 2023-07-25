#!/bin/bash

# CONSUMING MESSAGES
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic

# CONSUMING FROM BEGINNING
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning

# ADDITIONAL INFO
kafka-console-consumer --bootstrap-server localhost:9092 \
--topic first_topic \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.timestamp=true \
--property print.offset=true \
--property print.headers=true \
--property print.partition=true \
--property print.key=true \
--property print.value=true \
--from-beginning
