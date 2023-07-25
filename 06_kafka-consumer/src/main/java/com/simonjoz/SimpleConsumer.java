package com.simonjoz;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

public class SimpleConsumer {

    private static final String GROUP_ID = "group_1";
    private static final String TOPIC = "first_topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singleton(TOPIC));

        while (true) {
            kafkaConsumer.poll(Duration.of(1000, ChronoUnit.MILLIS))
                    .forEach(rec -> System.out.printf("Topic: %s  Partition: %s  Offset: %s   Key: %s  Value: %s\n",
                            rec.topic(),rec.partition(),rec.offset(),rec.key(),rec.value())
                    );

        }
    }
}
