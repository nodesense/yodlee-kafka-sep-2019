// InvoiceStream.java
package kafka.workshop.invoice;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import kafka.workshop.models.Invoice;

import java.util.Collections;
import java.util.Map;


import java.util.Properties;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class InvoiceStream {

    static  String bootstrapServers = "k5.nodesense.ai:9092";
    //FIXME: chance schema url
    static String schemaUrl = "http://k5.nodesense.ai:8081";

    public static Properties getConfiguration() {


        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-invoice-stream-gopal");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "product-invoice-stream-client-gopal");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);


        props.put("schema.registry.url", schemaUrl);
        return props;
    }


    public static void main(final String[] args) throws Exception {
        System.out.println("Running Invoice Stream");

        Properties props = getConfiguration();

        //Serdes ==> SerializationDeserialization

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();
        final Serde<Double> doubleSerde = Serdes.Double();


        final Serde<Invoice> InvoiceAvroSerde = new SpecificAvroSerde<>();

        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                schemaUrl);
        //
        InvoiceAvroSerde.configure(serdeConfig, true); // `true` for record keys


        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        // a Stream is a consumer
        // invoices-gopal is a topic
        // stream is consuming data from invoices-gopal
        final KStream<String, Invoice> invoiceStream = builder.stream("invoices-gopal");

        invoiceStream.foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice invoice) {
                System.out.println("Invoice Key " + key + "  value id  " + invoice.getId() + ":" + invoice.getAmount() );
                System.out.println("received invoice " + invoice);
            }
        });

        // Aggregation, pre-requisties for the aggregation
        KGroupedStream<String, Invoice> stateGroupStream = invoiceStream.groupBy(
                (key, invoice) -> invoice.getState() // return a key (state)
        );

        // KEY, VALUE, table used for aggregation
        KTable<String, Long> stateGroupCount = stateGroupStream
                .count();

        /// filter
        KStream<String, Invoice> invoiceQtyGt3Stream = invoiceStream
                .filter((key, invoice) ->  invoice.getQty() > 3);

        invoiceQtyGt3Stream.foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice invoice) {
                System.out.println("Invoice Key " + key + "  value id  " + invoice.getId() + ":" + invoice.getAmount() );
                System.out.println("received invoice " + invoice);
            }
        });


        // Write the output to a kafka topic
        KStream<String, Long> outputStream = stateGroupCount.toStream();

        // Sink to kafka topic/output, is a topic streams-state-invoices-gopal-count
        outputStream.to("streams-state-invoices-gopal-count",
                                Produced.with(stringSerde, longSerde));


        outputStream.foreach(new ForeachAction<String, Long>() {
            @Override
            public void apply(String stateName, Long count) {
                System.out.println("State Name  " + stateName + "  Sales count  " +  count );
            }
        });


        // collection of streams put together
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);


        try {
            // clean the local state if the steram is stateful
          //  streams.cleanUp();
        }catch(Exception e) {
        }

        streams.start();

        System.out.println("Stream started");

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}