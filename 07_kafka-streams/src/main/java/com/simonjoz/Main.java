package com.simonjoz;

import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) {

        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "inventory-data");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> source = builder.stream("inventory_purchases");

        source.mapValues((ValueMapper<String, Long>) Long::parseLong)
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .reduce(Long::sum, Materialized.with(Serdes.String(), Serdes.Long()))
                .toStream()
                .to("total_purchases", Produced.with(Serdes.String(), Serdes.Long()));


        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

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
