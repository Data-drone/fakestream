# Quick Cluster Setups

Quick setups to build ad-hoc clusters

## Scripts

base_nifi_cluster - from apache nifi repo - builds nifi cluster on zookeeper

baes_nifi_kafka - builds a fakestream nifi instance with processors ready to go to generate fake data with one kafka node for writing data to and from.


## start commands

```Bash

# nifi kafka with kafka apps
# run from project root
# build and compile code first
docker-compose -f docker_compose/dc_base_nifi_kafka.yml build kafka-app

docker-compose -f docker_compose/dc_base_nifi_kafka.yml up


```