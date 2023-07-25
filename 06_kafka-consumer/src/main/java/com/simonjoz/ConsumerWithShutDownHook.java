package com.simonjoz;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

public class ConsumerWithShutDownHook {

    private static final String GROUP_ID = "group_1";
    private static final String TOPIC = "first_topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(TOPIC));

        Thread mainThread = Thread.currentThread();

        Runnable shutDownHandler = () -> {
            consumer.wakeup();

            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
        Runtime.getRuntime().addShutdownHook(new Thread(shutDownHandler));

        try {
            while (true) {

                Duration duration = Duration.of(100, ChronoUnit.MILLIS);
                consumer.poll(duration)
                        .forEach(rec -> System.out.printf("Topic: %s  Partition: %s  Offset: %s   Key: %s  Value: %s\n",
                                rec.topic(), rec.partition(), rec.offset(), rec.key(), rec.value())
                        );

                consumer.commitSync();
            }
        } catch (WakeupException e) {
            System.out.println("Wakeup exception.");
        } catch (Exception e) {
            System.err.println("Unexpected Exception");
        } finally {
            System.err.println("Graceful shutdown.");
            consumer.close();
        }
    }
}
