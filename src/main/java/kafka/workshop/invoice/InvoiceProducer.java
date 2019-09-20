// InvoiceProducer.java
package kafka.workshop.invoice;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import kafka.workshop.models.Invoice;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
//

// kafka-topics --zookeeper k5.nodesense.ai:2181 --create --topic invoices-gopal --replication-factor 1 --partitions 3

public class InvoiceProducer {
    public static String BOOTSTRAP_SERVERS = "k5.nodesense.ai:9092";
    // FIXME: Always check
    public static String SCHEMA_REGISTRY = "http://k5.nodesense.ai:8081"; //default
    public static String TOPIC = "invoices-gopal";

    static Random random = new Random();
    static int[] categories = {1, 2, 3, 4};
    static int[] customerIds = {1000, 2000, 3000, 4000, 5000, 6000};
    static String[] customerNames = {"Krish", "Gayathri", "Nila", "Venkat", "Hari", "Ravi"};

    static String[] stateIds = {"KA", "TN", "KL", "MH", "DL", "AP"};


    public static Invoice getNextRandomInvoice() {

        String categoryId = "" + customerIds[random.nextInt(customerIds.length)];
        String stateId = "" + stateIds[random.nextInt(stateIds.length)];

        String customerId = UUID.randomUUID().toString();


        String id = UUID.randomUUID().toString();


        Invoice invoice = new Invoice();

        invoice.setId(id);
        invoice.setCustomerId(customerId);

        invoice.setQty(random.nextInt(5) + 1);
        invoice.setAmount(random.nextInt(5000) + 100);
        invoice.setCountry("IN");
        invoice.setInvoiceDate(System.currentTimeMillis());
        invoice.setState(stateId);

        return invoice;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long events = 10000;


        Properties props = new Properties();
        // hardcoding the Kafka server URI for this example
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", SCHEMA_REGISTRY);

        Producer<String, Invoice> producer = new KafkaProducer<String, Invoice>(props);

        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            Invoice invoice = getNextRandomInvoice();

            // Invoice ID as key
            ProducerRecord<String, Invoice> record = new ProducerRecord<String, Invoice>(TOPIC,
                    invoice.getState().toString(),
                    invoice);
            producer.send(record).get(); // get() sync wait
            System.out.println("Sent Invoice" + invoice);
            Thread.sleep(5000);
        }

    }
}