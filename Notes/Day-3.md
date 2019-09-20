
Servers

    k5.nodesense.ai
    k6.nodesense.ai
    k7.nodesense.ai
    k8.nodesense.ai
    k9.nodesense.ai
    k10.nodesense.ai

username: root


in browser

Control Center

k5.nodesense.ai:9021
k6.nodesense.ai:9021
k7.nodesense.ai:9021
k8.nodesense.ai:9021
k9.nodesense.ai:9021
k10.nodesense.ai:9021



https://raw.githubusercontent.com/nodesense/kafka-workshop/master/system-setup.sh

kafka-topics --create --zookeeper k5.nodesense.ai:2181 --replication-factor 1 --partitions 3 --topic greetings-gopal

                            

kafka-console-consumer  --bootstrap-server k5.nodesense.ai:9092 --topic streams-state-invoices-gopal-count  --from-beginning --property print.key=true --property print.timestamp=true --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

--

ssh into the machine

 
ksql-datagen quickstart=users format=avro topic=gopal_users maxInterval=5000

ssh into the machine

ksql-datagen quickstart=pageviews format=avro topic=gopal_pageviews maxInterval=5000

3rd ssh/putty for streams hands-on

> ksql 


CREATE STREAM gopal_users_stream (userid varchar, regionid varchar, gender varchar) WITH
(kafka_topic='gopal_users', value_format='AVRO');


SHOW STREAMS;

DESCRIBE gopal_users_stream;

DESCRIBE EXTENDED gopal_users_stream;


NON_PERSISTED QUERIES [Means, the output is not stored in KAfka Brokers]

select userid, regionid, gender from gopal_users_stream;

select userid, regionid, gender from gopal_users_stream where gender='FEMALE';

select userid, regionid, gender from gopal_users_stream where gender='MALE';

PERSISTED QUERIES [the result is stored into Kafka broker as topics]


CREATE STREAM gopal_users_female AS
SELECT userid AS userid, regionid
FROM gopal_users_stream
where gender='FEMALE';


CREATE STREAM gopal_users_male AS
SELECT userid AS userid, regionid
FROM gopal_users_stream
where gender='MALE';


---

CREATE STREAM gopal_pageviews_stream (userid varchar, pageid varchar) WITH
(kafka_topic='gopal_pageviews', value_format='AVRO');

select * from gopal_pageviews_stream;



CREATE STREAM gopal_user_pageviews_enriched_stream AS
SELECT gopal_users_stream.userid AS userid, pageid, regionid, gender
FROM gopal_pageviews_stream
LEFT JOIN gopal_users_stream
WITHIN 1 HOURS
ON gopal_pageviews_stream.userid = gopal_users_stream.userid;



CREATE TABLE gopal_pageviews_region_table
WITH (VALUE_FORMAT='AVRO') AS
SELECT gender, regionid, COUNT() AS numusers
FROM gopal_user_pageviews_enriched_stream
WINDOW TUMBLING (size 60 second)
GROUP BY gender, regionid
HAVING COUNT() >= 1;



CREATE TABLE gopal_pageviews_region_gt_10_table AS
SELECT gender, regionid, numusers
FROM gopal_pageviews_region_table
where numusers > 10;

SHOW QUERIES;


EXPLAIN CSAS_GOPAL_USERS_FEMALE_0;
        TERMINATE <QUERY_ID>;


DROP STREAM gopal_users_male;
DROP TABLE gopal_pageviews_region_gt_10_table;


----------------

https://github.com/nodesense/kafka-workshop


Y@d!ee@678


confluent status connectors

mkdir gopal
cd gopal
touch input-file.txt

touch gopal-file-source.properties


nano gopal-file-source.properties

paste below content into the file


name=gopal-file-source
connector.class=FileStreamSource
tasks.max=1
file=/root/gopal/input-file.txt
topic=gopal-file-content


---
  Use Ctrl + O to write the file in nano editor
  Use Ctrl + X to exit from nano editor
  
  
confluent list connectors

confluent load gopal-file-source -d gopal-file-source.properties

confluent status connectors


confluent status gopal-file-source


on the same command prompt (ssh):

   echo "Gopal: Line 1" >> input-file.txt
   


on a second command prompt (ssh):

kafka-console-consumer --bootstrap-server localhost:9092 --topic gopal-file-content  --from-beginning



---
  
on your folder /root/gopal

  touch output-file.txt

  touch gopal-file-sink.properties
  
  nano gopal-file-sink.properties
  
  
  and paste below content
  
name=gopal-file-sink
connector.class=FileStreamSink
tasks.max=1
file=/root/gopal/output-file.txt
topics=gopal-file-content


use below command to load the file sink

confluent load gopal-file-sink -d gopal-file-sink.properties

confluent status connectors
confluent status gopal-file-sink

cat output-file.txt


============

# Connect with JDBC 

mysql -uroot

GRANT ALL ON *.* TO 'team'@'localhost' identified by 'team1234';

---------------------
  
mysql -uroot

CREATE DATABASE gopal;
USE gopal;

create table products (id int, name varchar(255), price int, create_ts timestamp DEFAULT CURRENT_TIMESTAMP , update_ts timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );


insert into products (id, name, price) values(1,'moto phone', 1000); 

select * from products;

update products set price=2200 where id=1; 

--
on second command prompt (ssh):

/root/gopal > 
  
  touch gopal-mysql-product-source.json

  nano gopal-mysql-product-source.json
  
  paste below
  
  {
  "name": "gopal-mysql-product-source",
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter.schema.registry.url": "http://localhost:8081",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url": "http://localhost:8081",
    "connection.url": "jdbc:mysql://localhost:3306/gopal?user=team&password=team1234",
    "_comment": "Which table(s) to include",
    "table.whitelist": "products",
    "mode": "timestamp",
     "timestamp.column.name": "update_ts",
    "validate.non.null": "false",
    "_comment": "The Kafka topic will be made up of this prefix, plus the table name  ",
    "topic.prefix": "gopal-db-"
  }
}




confluent load gopal-mysql-product-source -d gopal-mysql-product-source.json

confluent status gopal-mysql-product-source

confluent status connectors


insert into products (id, name, price) values(2,'google phone', 2000); 

insert into products (id, name, price) values(3,'nexus phone', 3000); 

update products set price=3333 where id=3; 




https://github.com/nodesense/mindtree-kafka


# Connect with JDBC 

mysql -uroot

GRANT ALL ON *.* TO 'team'@'localhost' identified by 'team1234';

---------------------
  
mysql -uroot

CREATE DATABASE gopal;
USE gopal;

create table products (id int, name varchar(255), price int, create_ts timestamp DEFAULT CURRENT_TIMESTAMP , update_ts timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );


insert into products (id, name, price) values(1,'moto phone', 1000); 

select * from products;

update products set price=2200 where id=1; 

--
on second command prompt (ssh):

/root/gopal > 
  
  touch gopal-mysql-product-source.json

  nano gopal-mysql-product-source.json
  
  paste below
  
  {
  "name": "gopal-mysql-product-source",
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter.schema.registry.url": "http://localhost:8081",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url": "http://localhost:8081",
    "connection.url": "jdbc:mysql://localhost:3306/gopal?user=team&password=team1234",
    "_comment": "Which table(s) to include",
    "table.whitelist": "products",
    "mode": "timestamp",
     "timestamp.column.name": "update_ts",
    "validate.non.null": "false",
    "_comment": "The Kafka topic will be made up of this prefix, plus the table name  ",
    "topic.prefix": "gopal-db-"
  }
}




confluent load gopal-mysql-product-source -d gopal-mysql-product-source.json

confluent status gopal-mysql-product-source

confluent status connectors


insert into products (id, name, price) values(2,'google phone', 2000); 

insert into products (id, name, price) values(3,'nexus phone', 3000); 

update products set price=3333 where id=3; 


MySQL SINK Example

touch gopal-mysql-product-sink.properties

nano gopal-mysql-product-sink.properties


name=gopal-mysql-product-sink
connector.class=io.confluent.connect.jdbc.JdbcSinkConnector
tasks.max=1
topics=products
connection.url=jdbc:mysql://localhost:3306/gopal?user=team&password=team1234
auto.create=true
key.converter=io.confluent.connect.avro.AvroConverter
key.converter.schema.registry.url=http://localhost:8081
value.converter=io.confluent.connect.avro.AvroConverter
value.converter.schema.registry.url=http://localhost:8081



confluent load gopal-mysql-product-sink -d gopal-mysql-product-sink.properties


Ensure no SECOND time enter key pressed after pasting this line

kafka-avro-console-producer --broker-list localhost:9092 --topic products --property value.schema='{"type":"record","name":"product","fields":[{"name":"id","type":"int"},{"name":"name", "type": "string"}, {"name":"price", "type": "int"}]}'


paste below line one after another without new line

{"id": 999, "name": "asus pro", "price": 100}
















