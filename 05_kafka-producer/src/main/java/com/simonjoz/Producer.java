package com.simonjoz;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("all")
public class Producer {

    private static final String TOPIC = "first_topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        long count = 0;
        while (true) {

            String key = count % 2 == 0 ? "1" : "2";
            String value = String.valueOf(count + 10);

            var record = new ProducerRecord<>(TOPIC, key, value);

            kafkaProducer.send(record);
            Thread.sleep(1000);
            count++;
            System.out.printf("Message sent. Key: %s Value: %s\n", key, value);
        }
    }
}
