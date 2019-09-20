Kafka Training


http://packages.confluent.io/archive/5.1/confluent-5.1.2-2.11.zip

c:\confluent-5.1.2
       \bin
       \share
       \etc       
       
Set environment path

Add a path, user/system account,
  
Env name: KAFKA_HOME
Value:  c:\confluent-5.1.2

and add below to PATH
		
		c:\confluent-5.1.2\bin\windows
        


Windows Zookeeper

open a command prompt

zookeeper-server-start.bat %KAFKA_HOME%\etc\kafka\zookeeper.properties

MAC/Linux  => zookeeper-server-start $KAFKA_HOME/etc/kafka/zookeeper.properties

open a second prompt for windows

kafka-server-start.bat %KAFKA_HOME%\etc\kafka\server.properties

for mac/linux : kafka-server-start $KAFKA_HOME/etc/kafka/server.properties


CREATE A TOPIC

open third command prompt

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

kafka-topics --list --zookeeper localhost:2181


produce a message in topic test

kafka-console-producer --broker-list localhost:9092 --topic test


Open fourth command prompt for consumer

Subscribe for topic test

This will susbcribe messages from now onwards

kafka-console-consumer --bootstrap-server localhost:9092 --topic test


kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning



kafka-console-consumer --bootstrap-server localhost:9092 --topic test --partition 0 --from-beginning


kafka-console-consumer --bootstrap-server localhost:9092 --topic test --partition 0 --offset 3

=====

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic greetings

kafka-console-producer --broker-list localhost:9092 --topic greetings


kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --partition 0 --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --partition 1 --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --partition 2 --from-beginning


kafka-console-producer --broker-list localhost:9092 --topic greetings --property "parse.key=true" --property "key.separator=:"







kafka-topics --describe --zookeeper localhost:2181  --topic greetings


-- already running
kafka-server-start.bat %KAFKA_HOME%\etc\kafka\server.properties

new command prompt
kafka-server-start.bat %KAFKA_HOME%\etc\kafka\server-1.properties

new command prompt

kafka-server-start.bat %KAFKA_HOME%\etc\kafka\server-2.properties


new command prompt

zookeeper-shell localhost:2181


======
  
  
kafka-topics --create --zookeeper localhost:2181 --replication-factor 3 --partitions 3 --topic logs

kafka-topics --describe --zookeeper localhost:2181  --topic logs

kafka-console-producer --broker-list localhost:9092 --topic logs


<!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.1.1</version>
</dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka_2.12</artifactId>
    <version>2.1.1</version>
</dependency>

    
<!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>2.1.1</version>
</dependency>


 kafka-topics --zookeeper localhost:2181 --delete --topic greetings
 
 kafka-server-start config/server.properties \
   --override delete.topic.enable=true \
   --override broker.id=100 \
   --override log.dirs=/tmp/kafka-logs-100 \
   --override port=9192
   
   
   ./bin/kafka-topics.sh --alter --zookeeper localhost:2181 --topic my-topic --partitions 3

bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic test 
--partitions 1 --replication-factor 1 --config retention.ms=172800000

$ bin/kafka-topics.sh --zookeeper zk.yoursite.com --alter --topic as-access --config retention.ms=86400000

kafka-consumer-groups  --bootstrap-server localhost:9092 --describe --group greetings-consumer-group

kafka-consumer-groups --bootstrap-server host.server.com:9093 --describe --command-config client.properties



--

{"version":1,
  "partitions":[
     {"topic":"signals","partition":0,"replicas":[0,1,2]},
     {"topic":"signals","partition":1,"replicas":[0,1,2]},
     {"topic":"signals","partition":2,"replicas":[0,1,2]}
]}

$ kafka-reassign-partitions --zookeeper localhost:2181 --reassignment-json-file increase-replication-factor.json --execute

 $ kafka-topics --zookeeper localhost:2181 --topic signals --describe
