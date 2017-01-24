package test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Isura on 1/22/2017.
 */
public class FactorySim {

    public static void main(String[] args) {
//        Kafka configuration
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(config);

//        Fcatory Line Id
        String[] line_id = {"line1","line2","line3","line4","line5","line6","line7","line8","line9","line10"};
//        Factory names
        String[] factory_id = {"factoryA","factoryB","factoryC","factoryD","factoryA","factoryB","factoryC","factoryD","factoryA","factoryB","factoryC","factoryD"};
//        Factory Event
        String[] event_name= {"ftt","defects","reject","rwtgood","ftt","ftt","ftt","ftt","ftt","ftt"};
//        Style list
        String [] style = {"111111","222222","333333","4444444","555555","111111","222222","333333","4444444","555555"};

//      Message Body Format ==========>   factory_id TAB line_id TAB event_name TAB style
        int count = 0;
        while (true){
            Random randomGenerator = new Random();
            Integer f = randomGenerator.nextInt(10);
            Integer l = randomGenerator.nextInt(10);
            Integer e = randomGenerator.nextInt(10);
            Integer s = randomGenerator.nextInt(10);
            count ++;
            String message = factory_id[f]+"\t"+line_id[l]+"\t"+event_name[e]+"\t"+style[s]+"\t"+"1";
            producer.send(new ProducerRecord<String, String>("ncs-test1", "msg",message));
            try {
                Thread.sleep(100);
            } catch (InterruptedException er) {
                er.printStackTrace();
            }
            System.out.println(message);
        }


//        System.out.println(dateFormat.format(date));
    }
}
