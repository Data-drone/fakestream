package processors;

import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.KeyValue;

import org.apache.kafka.streams.kstream.Printed;

import com.google.gson.Gson;

import java.time.Duration;
import java.util.Properties;

import serde.SensorValJsonSerde;
import model.SensorVal;


public class SensorStreamCount 
{
    static final String inputTopic = "sensors-raw";
    static final String outputTopic = "sensors-data-count";

    static Gson gson = new Gson();

    public static void main( String[] args )
    {
        final String bootstrapServers = args.length > 0 ? args[0] : "kafka:9092";

        // Configure our application
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        final StreamsBuilder builder = new StreamsBuilder();
        createSensorCountStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        streams.cleanUp();

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    /**
     * Configure the Streams Application
     * settings for 
     * @param bootstrapServers
     * @return
     */

    static Properties getStreamsConfiguration(final String bootstrapServers) 
    {
        final Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "sensor-readings-count");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());


        return streamsConfiguration;
    }

    static void createSensorCountStream(final StreamsBuilder builder) {

        final KStream<String, SensorVal> valueLines = builder
                    .stream(inputTopic, Consumed.with(Serdes.String(), new SensorValJsonSerde()));

        // Printed sysout = Printed.<String, SensorVal>toSysOut(); 

        // valueLines.print(sysout);
        // need to see if we can do something with this?
        //final KStream<String, Map> sensorValues = valueLines.mapValues( (v -> gson.fromJson(v, Map.class)));

        // need function to parse the string
        // format is json
        // this doesn't seem to be doing what I want?
        // do we need to have a serdes in the groupby?
        // we need to change the table def?
        final KTable<Windowed<String>, Long> valueCounts = valueLines
                    .groupByKey()
                    .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
                    .count();

        // Printed sysout2 = Printed.<String, Long>toSysOut();

        //valueCounts.toStream()
        //            .map((key, value) -> new KeyValue<>(key.key() + "@" + key.window().start() + "->" + key.window().end(), value))
        //            .print(sysout2);
                    
            
        valueCounts.toStream()
                    .map((key, value) -> new KeyValue<>(key.key() + "@" + key.window().start() + "->" + key.window().end(), value))
                    .to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));


    }
}


