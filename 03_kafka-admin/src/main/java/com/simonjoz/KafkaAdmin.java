package com.simonjoz;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.TopicConfig;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaAdmin {


    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        try (Admin admin = Admin.create(props)) {

            final int partitions = 4;
            final short replicationFactor = 2;
            final String topicName = "admin-topic-" + UUID.randomUUID();
            final Set<String> topics = Collections.singleton(topicName);

            // CONSTRUCT NEW TOPIC WITH SOME SETTINGS
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            newTopic.configs(
                    Collections.singletonMap(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
            );

            // CREATE AND WAIT UNTIL TOPIC IS READY
            /*
                Call get() to block until the topic creation is complete or has failed
                if creation failed the ExecutionException wraps the underlying cause.
            */
            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));
            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();

            // LIST ALL TOPICS
            Set<String> topicsList = admin.listTopics().names().get();
            topicsList.forEach(System.out::println);


            // DESCRIBE CREATED TOPIC
            DescribeTopicsResult describeTopicsResult = admin.describeTopics(topics);
            Map<String, TopicDescription> stringTopicDescriptionMap = describeTopicsResult.allTopicNames().get();

            stringTopicDescriptionMap.forEach((name, desc) -> {
                        System.out.printf("\nTOPIC NAME: %s\n\n", name);

                        desc.partitions().forEach(partition -> {
                            System.out.printf("PARTITION: %s ", partition.partition());
                            System.out.println();
                            Node leader = partition.leader();
                            System.out.printf("  LEADER ID: %s HOST PORT: %s\n", leader.id(), leader.port());
                            partition.isr().forEach(isr -> {
                                        if (isr.id() != leader.id()) {
                                            System.out.printf("  REPLICA ID: %s HOST PORT: %s\n", isr.id(), isr.port());
                                        }
                                    }
                            );
                            System.out.println();
                        });
                        System.out.print("\n");
                    }
            );

            // CLEAN UP - DELETE CREATED TOPIC
            Map<String, KafkaFuture<Void>> deleteRequest = admin.deleteTopics(topics).topicNameValues();

            for (String key : deleteRequest.keySet()) {

                // block until future is completed or failed
                deleteRequest.get(key).get();

                System.out.printf("TOPIC '%s' HAS BEEN MARKED FOR DELETION\n", key);
            }
        }
    }
}
