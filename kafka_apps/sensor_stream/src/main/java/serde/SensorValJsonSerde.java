package serde;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import model.SensorVal;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;

/*
* Json Serde for SensorVal
*/
public class SensorValJsonSerde extends Serdes.WrapperSerde<SensorVal> {
    
    public SensorValJsonSerde() {
        super(new Serializer<SensorVal>() {
            private Gson gson = new Gson();

            @Override
            public void configure(Map<String, ?> map, boolean b) {
            }

            @Override
            public byte[] serialize(String topic, SensorVal data) {
                return gson.toJson(data).getBytes(StandardCharsets.UTF_8);
            } 

            @Override
            public void close() {

            }
        },
        new Deserializer<SensorVal>() {
            private Gson gson = new Gson();

            @Override
            public void configure(Map<String, ?> configs, boolean isKey){

            }

            @Override
            public SensorVal deserialize(String topic, byte[] data) {
                return gson.fromJson(new String(data), SensorVal.class);
            }

            @Override
            public void close() {

            }
        });
    }
}