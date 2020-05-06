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
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;

import org.apache.flink.util.Collector;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Properties;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;  
import java.text.DateFormat;

public class StreamingJob {

	private static String KAFKA_TOPIC_INPUT = "sensors-raw";
	private static String KAFKA_TOPIC_OUTPUT = "sensors-data-count";

	public static void main(String[] args) throws Exception {

		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// try using the processing time
		//env.setStreamTimeCharacteristic(TimeCharacteristic.EventTimeWindows);

		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "kafka:9092");
		properties.setProperty("group.id", "test");

		//FlinkKafkaConsumer<String> sensorConsumer = new FlinkKafkaConsumer<>("sensors-raw", 
		//		new SimpleStringSchema(), properties);
		FlinkKafkaConsumer<ObjectNode> sensorConsumer = new FlinkKafkaConsumer(KAFKA_TOPIC_INPUT, 
				new JSONKeyValueDeserializationSchema(false), properties);
		
		DataStream<Tuple2<String, Integer>> stream = env
			.addSource(sensorConsumer)
			.flatMap(new SelectKeyAndFlatMap())
			// assign timestamp
			.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple3<String, Timestamp, Double>>() {

				@Override
				public long extractAscendingTimestamp(Tuple3<String, Timestamp, Double> entry) {
					long timestamp = entry.f1.getTime();
					return timestamp;
				}
			})
			.flatMap( new FlatMapFunction<Tuple3<String, Timestamp, Double>, Tuple2<String, Integer>>() {

				@Override
				public void flatMap(Tuple3<String, Timestamp, Double> in, Collector<Tuple2<String, Integer>> out) throws Exception {
					out.collect(new Tuple2<>(in.f0, 1));
				}
			})
			.keyBy(0)
			.window(TumblingEventTimeWindows.of(Time.seconds(5)))
			//.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
			.sum(1);

		FlinkKafkaProducer<String> sensorProducer = new FlinkKafkaProducer<String>("kafka:9092", KAFKA_TOPIC_OUTPUT, new SimpleStringSchema());

		//DataStream<String> stream_out = 
		stream
			.map(new MapFunction<Tuple2<String, Integer>, String>() {
				@Override
				public String map(Tuple2<String, Integer> tuple) {
					return tuple.toString();
				}
			})
			.addSink(sensorProducer);

		// execute program
		env.execute("Sensor Count Stream");
	}

	
	// Map the ObjectNode to a Tuple3
	public static class SelectKeyAndFlatMap implements FlatMapFunction<ObjectNode, Tuple3<String, Timestamp, Double>> {

		final String formatDate = "yyyy-MM-dd HH:mm:ss.SSSSSS";

		@Override
		public void flatMap(ObjectNode kafMsg, Collector<Tuple3<String, Timestamp, Double>> out) throws Exception {

			//DateFormat formatter = new SimpleDateFormat(formatDate);

			String key = kafMsg.get("key").get("sensor").asText();
			String timestamp = kafMsg.get("value").get("timestamp").asText();
			// convert to java timestamp?
			Timestamp ts = Timestamp.valueOf(timestamp);
			Double value = kafMsg.get("value").get("value").asDouble();

			out.collect(new Tuple3<>(key, ts, value));
			
		}

	}
	
}
