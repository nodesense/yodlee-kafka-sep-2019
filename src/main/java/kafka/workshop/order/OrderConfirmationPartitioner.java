//OrderConfirmationPartitioner.java
package kafka.workshop.order;

import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

// import io.confluent.common.utils.Utils;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;


public class OrderConfirmationPartitioner implements Partitioner {
    private Random random;

    public OrderConfirmationPartitioner() {
        System.out.println("OrderConfirmationPartitioner created");
        random = new Random();
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // config properties
    }

    // return a partition number (if 4 max partition, returns 0, 1, 2, 3
    @Override
    public int partition(String topic,
                         Object key,  // key object as ref
                         byte[] keyBytes, // key in serialized bytes
                         Object value,  // OrderConfirmation object
                         byte[] valueBytes, // serialized json bytes
                         Cluster cluster) {

        int partition = 0;

        System.out.println("Partition Thread ID " + Thread.currentThread().getId());


        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);

        int numPartitions = partitions.size();

        System.out.println("Total partitions " + partitions.size() + " for topic");


        if (numPartitions <= 1) {
            return 0;
        }

        String country = (String) key;

        if (country.equals(("IN"))) {
            partition = 0;
        } else if (country.equals(("USA"))) {
            partition =  1;
        } else {
            partition = 2;
        }

        // value
        OrderConfirmation order = (OrderConfirmation) value;



        // OR

        // Find the id of current user based on the username
//        //Integer userId = userService.findUserId(userName);
//        Integer userId = random.nextInt(5);
//        // If the userId not found, default partition is 0
//        if (userId != null) {
//            partition = userId;
//        }


        // Other option, use murmur2 algorithm
        // or use hash key
        // -1 does ensure that 0 is not taken
        //  partition = Math.abs(Utils.murmur2(country.getBytes()) % (numPartitions - 1)) + 1;

        // Use custom models like Sensor key, product id etc for partition



        System.out.println(" For key " + key + " Part " + partition);
        return partition;
    }

    @Override
    public void close() {

    }

}