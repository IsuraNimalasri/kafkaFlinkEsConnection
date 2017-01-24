package test;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch2.RequestIndexer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by Isura on 1/22/2017.
 */
public class EventProcesserClinet {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> stream = readFromKafka(env);
        stream.print();
        writeToElasticOpsIndex(stream);
//        writeToElasticAnalyticsIndex(stream);
        // execute program
        env.execute("ncs_Factory_simulation!");
    }

//    Read from kafka
public static DataStream<String> readFromKafka(StreamExecutionEnvironment env) {
    env.enableCheckpointing(5000);
    // set up the execution environment
    Properties properties = new Properties();
    properties.setProperty("bootstrap.servers", "localhost:9092");
    properties.setProperty("group.id", "ncs-test1");

    DataStream<String> stream = env.addSource(
            new FlinkKafkaConsumer09<>("ncs-test1", new SimpleStringSchema(), properties));
    return stream;
}
//    write to ops index
public static void writeToElasticOpsIndex(DataStream<String> input) {

    Map<String, String> config = new HashMap<>();

    // This instructs the sink to emit after every element, otherwise they would be buffered
    config.put("bulk.flush.max.actions", "1");
    config.put("cluster.name", "es_ncs");

    try {
        // Add elasticsearch hosts on startup
        List<InetSocketAddress> transports = new ArrayList<>();
        transports.add(new InetSocketAddress("127.0.0.1", 9300)); // port is 9300 not 9200 for ES TransportClient

        ElasticsearchSinkFunction<String> indexLog = new ElasticsearchSinkFunction<String>() {
////      Message Body Format ==========>   factory_id TAB line_id TAB event_name TAB style
            public IndexRequest createIndexRequest(String element) {
                String[] logContent = element.trim().split("\t");
                Map<String, String> esJson = new HashMap<>();
                esJson.put("factory_id", logContent[0]);
                esJson.put("line_id", logContent[1]);
                esJson.put("style",logContent[3]);
                long epoch = System.currentTimeMillis();
                String s = Objects.toString(epoch, null);
                esJson.put("datetime",s);
                esJson.put("value",logContent[4]);
//                Create Index pattern
//                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
//                Date date = new Date();
//                String dt = dateFormat.format(date);

                return Requests
                        .indexRequest()
                        .index("event-data")
                        .type(logContent[2])
                        .source(esJson);
            }

            @Override
            public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
                indexer.add(createIndexRequest(element));
            }
        };

        ElasticsearchSink esSink = new ElasticsearchSink(config, transports, indexLog);
        input.addSink(esSink);
    } catch (Exception e) {
        System.out.println(e);
    }
}
//    write to Analytics Index
//public static void writeToElasticAnalyticsIndex(DataStream<String> input){
//    Map<String, String> config2 = new HashMap<>();
//
//    // This instructs the sink to emit after every element, otherwise they would be buffered
//    config2.put("bulk.flush.max.actions", "1");
//    config2.put("cluster.name", "es_ncs");
//    try {
//        // Add elasticsearch hosts on startup
//        List<InetSocketAddress> transports = new ArrayList<>();
//        transports.add(new InetSocketAddress("127.0.0.1", 9300)); // port is 9300 not 9200 for ES TransportClient
//
//        ElasticsearchSinkFunction<String> indexLog2 = new ElasticsearchSinkFunction<String>() {
//            ////      Message Body Format ==========>   factory_id TAB line_id TAB event_name TAB style
//            public IndexRequest createIndexRequest(String element) {
//                String[] logContent = element.trim().split("\t");
//                Map<String, String> esJson = new HashMap<>();
//                esJson.put("factory_id", logContent[0]);
//                esJson.put("line_id", logContent[1]);
//                esJson.put("style",logContent[3]);
//                long epoch = System.currentTimeMillis();
//                String s = Objects.toString(epoch, null);
//                esJson.put("datetime",s);
//                esJson.put("count",logContent[4]);
////                Create Index pattern
//                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
//                Date date = new Date();
//                String dt = dateFormat.format(date);
//
//                return Requests
//                        .indexRequest()
//                        .index("ncs-mas-ops-"+dt)
//                        .type(logContent[2])
//                        .source(esJson);
//            }
//
//            @Override
//            public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
//                indexer.add(createIndexRequest(element));
//            }
//        };
//
//        ElasticsearchSink esSink = new ElasticsearchSink(config2, transports, indexLog);
//        input.addSink(esSink);
//    } catch (Exception e) {
//        System.out.println(e);
//    }
//}

}
