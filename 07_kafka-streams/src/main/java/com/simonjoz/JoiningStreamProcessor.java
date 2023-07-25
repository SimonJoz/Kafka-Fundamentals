package com.simonjoz;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class JoiningStreamProcessor {

    public static void main(String[] args) { // Set up the configuration.

        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "inventory-data");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        // Since the input topic uses Strings for both key and value, set the default Serdes to String.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Get the source stream.
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> leftStream = builder.stream("left-join-input-topic");
        final KStream<String, String> rightStream = builder.stream("right-join-input-topic");
        KTable<String, String> table = builder.table("right-join-input-topic");
        KTable<String, String> table2 = builder.table("right-join-input-topic");


        KStream<String, String> innerJoinStream = leftStream.join(
                rightStream,
                (leftValue, rightValue) -> {
                    System.out.println("Left value: " + leftValue + " Right value: " + rightValue);
                    return "Left value: " + leftValue + " Right value: " + rightValue;
                },
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5)));


        innerJoinStream.to("inner-join-output-topic");


        KStream<String, String> leftJoinStream = leftStream.leftJoin(
                rightStream,
                (leftValue, rightValue) -> "Left value: " + leftValue + "Right value: " + rightValue,
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5)));


        leftJoinStream.to("left-join-output-topic");

        KStream<String, String> outerJoinStream = leftStream.outerJoin(
                rightStream,
                (leftValue, rightValue) -> "Left value: " + leftValue + "Right value: " + rightValue,
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5)));

        outerJoinStream.to("outer-join-output-topic");


        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props); // Print the topology to the console. System.out.println(topology.describe());
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach a shutdown handler to catch control-c and terminate the application gracefully.
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {

            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
