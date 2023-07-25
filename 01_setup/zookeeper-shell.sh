#!/bin/bash

zookeeper-shell localhost:2181 get /controller | grep "brokerid"

# START INTERACTIVE SHELL
zookeeper-shell localhost:2181

# AVAILABLE BROKER IDS
ls /brokers/ids

# BROKER 1 DETAILS
get /brokers/ids/1

# BROKER 2 DETAILS
get /brokers/ids/2

# LIST ALL BROKERS TOPICS
ls /brokers/topics

# CONTROLLER BROKER
get /controller

# REMOVE CONTROLLER BROKER
delete /controller


