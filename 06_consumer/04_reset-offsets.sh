#!/bin/bash

# RESET OFFSET OF SPECIFIC GROUP
kafka-consumer-groups --bootstrap-server localhost:9092 \
--group my-first-application --reset-offsets \
--to-earliest --execute --topic first_topic

# DESCRIBE GROUP
kafka-consumer-groups --bootstrap-server localhost:9092 \
--describe --group my-first-application

# CONSUME FROM WHERE THE OFFSET HAVE BEEN RESET
kafka-console-consumer --bootstrap-server localhost:9092 \
--topic first_topic --group my-first-application

# DESCRIBE GROUP AGAIN
kafka-consumer-groups --bootstrap-server localhost:9092 \
--describe --group my-first-application

# SHIFT OFFSET BY 5 - BACKWARDS
kafka-consumer-groups --bootstrap-server localhost:9092 \
--group my-first-application --reset-offsets --shift-by -5 --execute --topic first_topic

# SHIFT OFFSET BY 2 - FORWARD
kafka-consumer-groups --bootstrap-server localhost:9092 \
--group my-first-application \
--reset-offsets --shift-by 2 --execute --topic first_topic

# CONSUME AGAIN
kafka-console-consumer --bootstrap-server localhost:9092 \
--topic first_topic --group my-first-application
