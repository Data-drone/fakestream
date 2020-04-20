#################################
####### Build stream count job
#################################

FROM maven:3.6-jdk-8-slim AS builder

# Get Clickcount app and compile it
# we have ./kafka_apps because we run docker compose from project level
COPY ./kafka_apps/sensor_stream/sensor_count /opt/sensor-stream
WORKDIR /opt/sensor-stream
RUN mvn clean package

#################################
####### Build Maven 
#################################

FROM wurstmeister/kafka:2.12-2.4.1
WORKDIR /opt/kafka-app
COPY --from=builder /opt/sensor-stream/target/brian-*.jar /opt//kafka-app/brian.jar