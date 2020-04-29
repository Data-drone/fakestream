#################################
####### Build stream count job
#################################

FROM maven:3.6-jdk-8-slim AS builder

# Get Clickcount app and compile it
# we have ./kafka_apps because we run docker compose from project level
COPY ./kafka_apps/sensor_stream /opt/sensor_stream
WORKDIR /opt/sensor_stream
RUN mvn clean package assembly:single

#################################
####### Build Maven 
#################################

FROM openjdk:8-jre
WORKDIR /home
COPY --from=builder /opt/sensor_stream/target/sensor_stream*.jar /home/