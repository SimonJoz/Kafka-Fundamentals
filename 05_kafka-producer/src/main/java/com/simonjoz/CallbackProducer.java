package com.simonjoz;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Properties;
import java.util.UUID;

public class CallbackProducer {

    private static final String TOPIC = "uuid-topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(properties);

        final int len = 5;
        System.out.print("\n    TOPIC         TIMESTAMP        PARTITION     KEY                    VALUE                           OFFSET\n");
        System.out.println("======================================================================================================================");

        for (int i = 0; i <= len; i++) {

            ProducerRecord<Integer, String> record = new ProducerRecord<>(TOPIC, i, UUID.randomUUID().toString());

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println(exception.getMessage());
                } else {
                    System.out.printf(" %s      %s          %s          %s       %s            %s\n",
                            metadata.topic(),
                            LocalTime.ofInstant(Instant.ofEpochMilli(metadata.timestamp()), ZoneId.systemDefault()),
                            metadata.partition(),
                            record.key(),
                            record.value(),
                            metadata.offset());
                }
            });
        }
        producer.close();
        System.out.println("======================================================================================================================");
    }
}
