// SimpleConsumer.java

package kafka.workshop;



import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class SimpleConsumer {
    public static String BOOTSTRAP_SERVERS = "k5.nodesense.ai:9092";
    public static String TOPIC = "gopal-db-products";

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // props.put(GROUP_ID_CONFIG, "greetings-consumer-group-gopal-file"); // offset, etc, TODO

        // -- true, automatically commit the offset, automatically
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");

        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // <Key as string, Value as string>
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // subscribe from one or more topics
        consumer.subscribe(Collections.singletonList(TOPIC));

        System.out.println("Consumer starting..");

        while (true) {
            // Consumer poll for the data with wait time
            // poll for msgs for 1 second, any messges within second, group together
            // if no msg, exit in 1 second, records length is 0
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            if (records.count() == 0)
                continue; // wait for more msg


            List<PartitionInfo> partitions = consumer.partitionsFor(TOPIC);
            for (PartitionInfo partitionInfo: partitions) {
                System.out.println("Partition " + partitionInfo);
            }


            // Iterate record and print the record
            for (ConsumerRecord<String, String> record: records) {
                System.out.printf("partition=%d, offset=%d\n", record.partition(),
                        record.offset());

                // start the processing record - 5 seconds
                // threads.....

                // end the processing the record

                System.out.printf("key=%s,value=%s\n", record.key(), record.value());
            }

           // consumer.commitSync(); // manual commit, set the offset

            Thread.sleep(2000);
        }

    }

}