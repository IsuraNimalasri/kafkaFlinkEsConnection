# kafkaFlinkEsConnection

https://github.com/IsuraNimalasri/KafkaFlinkEs-Pipeline/blob/master/README.md
Kafka produce -> kafka broker -> kafkaConsumer -> Flink-Source -> Flink-Transformation -> FlinkDataSink with ES -> Kibana  




 Kafka and Zookeper 

1. Start Zookeper
```
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```
2. Start Kafka Server
```
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

3. create Topic
```
.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic ncs-test1
```
4. flink start 
```
flink-1.1.4-bin-hadoop27-scala_2.11\flink-1.1.4\bin\start-local.bat
```

5. elasticsearch configuration 
 5.1) change yml file : 
```
 cluster.name: "es_ncs"
```
6. create event-data index
```

curl -XPUT 'http://localhost:9200/event-data/' -d '```
{
      "settings" : {
        "index" : {
            "number_of_shards" : 1, 
            "number_of_replicas" : 0
        }
    }
}'
```
7. put mapping for ftt doctype
```
curl -XPUT 'localhost:9200/event-data/_mapping/ftt' -d '
{ "properties": {
            "factory_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "line_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "style": {
                "type": "string",
                "index": "not_analyzed"
            },
            "datetime": {
                "type":   "date"
          		
            },
            "value": {
                "type":"integer"
          		
            }
      }
}'
```

8. put mapping for defects doctype
```
curl -XPUT 'localhost:9200/event-data/_mapping/defects' -d '{
      "properties": {
            "factory_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "line_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "style": {
                "type": "string",
                "index": "not_analyzed"
            },
            "datetime": {
                "type":   "date"
          		
            },
            "value": {
                "type":"integer"
          		
            }
      }
}'
```
9. put mapping for reject doctype
```
curl -XPUT 'localhost:9200/event-data/_mapping/reject' -d '{
      "properties": {
            "factory_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "line_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "style": {
                "type": "string",
                "index": "not_analyzed"
            },
            "datetime": {
                "type":   "date"
          		
            },
            "value": {
                "type":"integer"
          		
            }
      }
}'
```
10. put mapping for rwtgood doctype
```
curl -XPUT 'localhost:9200/event-data/_mapping/rwtgood' -d '{
      "properties": {
            "factory_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "line_id": {
              "type": "string",
              "index": "not_analyzed"
            },
            "style": {
                "type": "string",
                "index": "not_analyzed"
            },
            "datetime": {
                "type":   "date"
          		
            },
            "value": {
                "type":"integer"
          		
            }
      }
}'
```
