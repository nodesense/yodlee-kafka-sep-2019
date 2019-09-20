Producers - 100 msg / sec - festive
Producers - 20 msg / sec - normal days

topic: orders
    Paritions: 4 

Broker(s)

Consumers - Group 1
    SMS Sender - Consumer - 10 msg/sec - 1 instance 

    SMS Sender - Consumer - 10 msg/sec - 2nd instance
    ...
    ..
    SMS Sender - Consumer - 10 msg/sec - 10th instance
Consumers - Group 2
    Email Senders - Consumer - 1 instnace
    Email Senders - Consumer - 2 instnace

----
Partitions 
  P0 - [Msg0, ...]

Consumer Group -1 - CG1
    Consumer 1 - Allocate the partition to a consumer in a group
        Consumer 1 - P0
-----------

Partitions 
  P0 - [Msg0, ...]
  P1 - [M100, Mg1000...]

Consumer Group -1 - CG1
    Consumer 1 - Allocate the partition to a consumer in a group
        Consumer 1 - P0, P1 [Rebalancing]

--


Partitions 
  P0 - [Msg0, ...]
  P1 - [M100, Mg1000...]
  P2 - [.....]

Consumer Group -1 - CG1
    Consumer 1 - Allocate the partition to a consumer in a group
        Consumer 1 - P0, P1, P2 [Rebalancing]

-------------


----
Partitions 
  P0 - [Msg0, ...]

Consumer Group -1 - CG1
    Consumer 1 - Allocate the partition to a consumer in a group
        Consumer 1 - P0

    Consumer 2 - 

-----------

Partitions 
  P0 - [Msg0, ...]
  P1 - [M100, Mg1000...]

Consumer Group -1 - CG1
    Consumer 1 - Allocate the partition to a consumer in a group
        Consumer 1 - P0 [Rebalancing]
        Consumer 2 - P1  [Rebalancing]
        Consumer 3 - IDLE 
----


Partitions 

    P0 - [Msg0, Msg1, msg2 ....]
    P1 - [Msg100, Msg11,...]


topic: __consumer_offsets 
     CG1 - P0 [Last commit offset: 1]   
     CG2 - P0 [Last commit offset: null]   

Consumer Group - CG1 
    emails
        took msg0 from p0
        commit the offset [on p0, offset 0] to the broker 
        took msg1 from p0
        commit the offset [on p0, offset 1] to the broker 

        restart the consumer
        consumer read the offset from the broker [offset: 1]
        took msg2 from p0
        commit the offset [on p0, offset 2] to the broker 

Consumer Group - CG2
    sms

## Download Avro files from here

avro-tools-1.8.2.jar from here
http://central.maven.org/maven2/org/apache/avro/avro-tools/1.8.2/

download avro-1.8.2.jar  from here
http://central.maven.org/maven2/org/apache/avro/avro/1.8.2

Keep them in project rootfolder/lib

### Command

java -jar lib/avro-tools-1.8.2.jar compile schema src/main/resources/avro src/main/java



---
FOR SCHEMA Registry

save below files into bin/windows folder, ensure save as .bat file (NOT TEXT FILE)

https://raw.githubusercontent.com/renukaradhya/confluentplatform/master/bin/windows/schema-registry-run-class.bat

https://raw.githubusercontent.com/renukaradhya/confluentplatform/master/bin/windows/schema-registry-start.bat



--
edit the file etc\schema-registry\schema-registry.properties

change the listener port number from 8081 to 8091 instead then start the schema registry

-----
  
open command prompt
  
schema-registry-start.bat %KAFKA_HOME%\etc\schema-registry\schema-registry.properties


MAC/Linux: schema-registry-start $KAFKA_HOME/etc/schema-registry/schema-registry.properties


open the browser

http://localhost:8091/

get the registered schemas for key and value

http://localhost:8091/subjects


kafka-console-consumer --bootstrap-server localhost:9092 --topic invoices  --from-beginning --property print.key=true --property print.timestamp=true


----

kafka-console-consumer --bootstrap-server localhost:9092 --topic invoices  --from-beginning --property print.key=true --property print.timestamp=true


kafka-avro-console-consumer  --bootstrap-server localhost:9092 --topic invoices  --from-beginning --property print.key=true --property print.timestamp=true --property schema.registry.url="http://localhost:8091"


---

kafka-console-consumer --bootstrap-server localhost:9092 --topic invoices  --from-beginning --property print.key=true --property print.timestamp=true


kafka-avro-console-consumer  --bootstrap-server localhost:9092 --topic invoices  --from-beginning --property print.key=true --property print.timestamp=true --property schema.registry.url="http://localhost:8091"


 http://localhost:8091/subjects

http://localhost:8091/subjects/invoices-key/versions

http://localhost:8091/subjects/invoices-key/versions/1

http://localhost:8091/subjects/invoices-value/versions
http://localhost:8091/subjects/invoices-value/versions/1


http://localhost:8091/schemas/ids/1
http://localhost:8091/schemas/ids/2

------
