/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package processors;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;

import org.apache.flink.util.Collector;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Properties;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;  
import java.text.DateFormat;

//import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;



/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

	private static String KAFKA_TOPIC_INPUT = "sensors-raw";

	public static void main(String[] args) throws Exception {

		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// try using the processing time
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "kafka:9092");
		properties.setProperty("group.id", "test");

		//FlinkKafkaConsumer<String> sensorConsumer = new FlinkKafkaConsumer<>("sensors-raw", 
		//		new SimpleStringSchema(), properties);
		FlinkKafkaConsumer<ObjectNode> sensorConsumer = new FlinkKafkaConsumer(KAFKA_TOPIC_INPUT, 
				new JSONKeyValueDeserializationSchema(false), properties);
		
		DataStream<Tuple3<String, String, Double>> stream = env
			.addSource(sensorConsumer)
			.flatMap(new SelectKeyAndFlatMap())
			// assign timestamp
			.keyBy(0)
			.window(TumblingEventTimeWindows.of(Time.seconds(5)))
			//.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
			.sum(2);

		stream.print();
		/*
		 * Here, you can start creating your execution plan for Flink.
		 *
		 * Start with getting some data from the environment, like
		 * 	env.readTextFile(textPath);
		 *
		 * then, transform the resulting DataStream<String> using operations
		 * like
		 * 	.filter()
		 * 	.flatMap()
		 * 	.join()
		 * 	.coGroup()
		 *
		 * and many more.
		 * Have a look at the programming guide for the Java API:
		 *
		 * http://flink.apache.org/docs/latest/apis/streaming/index.html
		 *
		 */

		// execute program
		env.execute("Sensor Stream Java App- 1");
	}

	
	// Map the ObjectNode to a Tuple3
	public static class SelectKeyAndFlatMap implements FlatMapFunction<ObjectNode, Tuple3<String, String, Double>> {

		final String formatDate = "yyyy-MM-dd HH:mm:ss.SSSSSS";

		@Override
		public void flatMap(ObjectNode kafMsg, Collector<Tuple3<String, String, Double>> out) throws Exception {

			//DateFormat formatter = new SimpleDateFormat(formatDate);

			String key = kafMsg.get("key").get("sensor").asText();
			String timestamp = kafMsg.get("value").get("timestamp").asText();
			// convert to java timestamp?
			//Timestamp ts = Timestamp.valueOf(formatter.format(timestamp));
			Double value = kafMsg.get("value").get("value").asDouble();

			out.collect(new Tuple3<>(key, timestamp, value));
			
		}

	}
	
}
