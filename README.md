# Fakestream Data Generation

For experimenting with different data processing and storage technologies, fake data is a must.

Ideally fake data should have some resemblance to real data. This repo leverages NiFi and python libraries to generate realistic looking data

## Notes

Start the container with:

```
docker run --name fakestream \
    -p 8080:8080 \
    -d \
    fakestream:latest
```

## Examples

docker-compose script sets up four nodes with:
1x nifi
1x nifi-registry
1x zookeeper
1x kafka

## ToDos

 - Utilise NiPy API to load templates
 - Utilise NiPy API to connect registry