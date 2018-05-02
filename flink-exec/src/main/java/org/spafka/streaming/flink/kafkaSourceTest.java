package org.spafka.streaming.flink;


import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

@Slf4j
public class kafkaSourceTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableSysoutLogging();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        env.enableCheckpointing(5000);// create a checkpoint every 5 seconds
        //   env.getConfig().setGlobalJobParameters(parameterTool); // make parameters available in the web interface
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        System.setProperty("java.security.auth.login.config", "/Users/spafka/Desktop/flink_in_action/src/main/resources/kafka_client_jaas.conf");

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("client.id", "flinkKafkaSource");
        props.put("group.id", "flink");
        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        // security
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");


        FlinkKafkaConsumer010<String> kafkaConsumer010 = new FlinkKafkaConsumer010<>(
                "testGo",
                new SimpleStringSchema(), props

        );

        kafkaConsumer010.setStartFromEarliest();
        DataStream<String> input = env.addSource(kafkaConsumer010);


        input.map(new MapFunction<String, Object>() {
            @Override
            public Object map(String s) {
                log.info("message={}", s);
                return null;
            }
        });

        env.execute();


    }
}