package br.com.laurowag;

import br.com.laurowagnitz.model.Cliente;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Startup
@Singleton
public class Notificador {
    
    private KafkaConsumer<String, byte[]> consumer;
    private Thread thread;
    
    public Notificador() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.2.136.226:9092");
        props.put("group.id","laurowag");
        props.put("enable.auto.commit","true");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("max.partition.fetch.bytes","2097152");
        /*
        props.put("sasl.jaas.config","org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-password\";");
        props.put("security.protocol","SASL_PLAINTEXT");
        props.put("sasl.mechanism","PLAIN");
        */
        
        System.out.println("****** VAI CONECTAR ******");
        consumer = new KafkaConsumer<String, byte[]>(props);
        System.out.println("****** CONECTOU ******");
        
        Schema s = ReflectData.AllowNull.get().getSchema(Cliente.class);
        ReflectDatumReader<Object> reader = new ReflectDatumReader<Object>(s);

        ManagedThreadFactory threadFactory;
        try {
            threadFactory = InitialContext.doLookup("java:comp/DefaultManagedThreadFactory");
            
            thread = threadFactory.newThread(new Runnable() {
                @Override
                public void run() {
                    consumer.subscribe(Arrays.asList("laurowag"));
                    int timeouts = 0;

                    while (true) {
                        ConsumerRecords<String, byte[]> records = consumer.poll(200);
                        if (records.count() == 0) {
                            timeouts++;
                        } else {
                            System.out.printf("Got %d records after %d timeouts\n", records.count(), timeouts);
                        }

                        for (ConsumerRecord<String, byte[]> record: records) {
                            try {
                                Cliente cliente = (Cliente) reader.read(null,
                                                    DecoderFactory.get().binaryDecoder(record.value(), null));
                                System.out.println("BAIXOU "+cliente.getNome());
                            } catch (Exception erro) {
                                erro.printStackTrace();                    
                            }
                        }
                    }
                }
            });
            thread.start();
        } catch (NamingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    
    @PreDestroy
    private void preDestroy() {
        thread.interrupt();
        consumer.close();
    }

}
