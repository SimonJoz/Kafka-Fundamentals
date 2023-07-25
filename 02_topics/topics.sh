#!/bin/bash

# LIST
kafka-topics --bootstrap-server localhost:9092 --list

# CREATE
kafka-topics --bootstrap-server localhost:9092 --topic first_topic --create
kafka-topics --bootstrap-server localhost:9092 --topic second_topic --create --partitions 3 --replication-factor 3

# CREATE FAIL
kafka-topics --bootstrap-server localhost:9092 --topic third_topic --create --partitions 3 --replication-factor 4

# DESCRIBE
kafka-topics --bootstrap-server localhost:9092 --describe
kafka-topics --bootstrap-server localhost:9092 --topic first_topic --describe
kafka-topics --bootstrap-server localhost:9092 --topic second_topic --describe

# DELETE
kafka-topics --bootstrap-server localhost:9092 --topic second_topic --delete


# DESCRIBE DYNAMIC CONFIGS
kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name first_topic --describe

# ADD DYNAMIC CONFIG
kafka-configs --bootstrap-server localhost:9092 --entity-type topics \
--entity-name first_topic --add-config min.insync.replicas=2 --alter

# DELETE CONFIG
kafka-configs --bootstrap-server localhost:9092 --entity-type topics \
--entity-name first_topic --delete-config min.insync.replicas --alter
