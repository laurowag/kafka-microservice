package br.com.laurowag.rest;

import br.com.laurowag.rest.KafkaAvroReflectSerializer.ReflectContainer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class KafkaResponseFilter implements ContainerResponseFilter {

    private KafkaProducer<String, Object> producer;

    public KafkaResponseFilter() {
        System.out.println("**** CRIOU FILTER ****");

        Properties props = new Properties();
        props.put("bootstrap.servers", "10.2.167.21:9092");
        props.put("group.id","test");
        props.put("enable.auto.commit","true");
        props.put("value.converter.avro.compatibility.level", "NONE");
        props.put("value.converter.avro.compatibility.level", "NONE");
        //props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //props.put("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("max.partition.fetch.bytes","2097152");

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                        io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                        br.com.laurowag.rest.KafkaAvroReflectSerializer.class);

        props.put("schema.registry.url", "http://10.2.141.98:8081");
        /*
		props.put("sasl.jaas.config","org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-password\";");
		props.put("security.protocol","SASL_PLAINTEXT");
		props.put("sasl.mechanism","PLAIN");
         */
        System.out.println("****** VAI CONECTAR ******");
        producer = new KafkaProducer<>(props);
        System.out.println("****** CONECTOU ******");
    }

    @PreDestroy
    public void preDestroy() {
        producer.close();		
        System.out.println("****** FECHOU******");
    }

    @Override
    public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
        if (arg1.getEntity() != null) {
            ManagedThreadFactory threadFactory;
            try {
                threadFactory = InitialContext.doLookup("java:comp/DefaultManagedThreadFactory");

                Thread thread = threadFactory.newThread(new Runnable() {
                    @Override
                    public void run() {
                        producer.send(new ProducerRecord<String, Object>("laurowag", "cliente", new br.com.laurowag.rest.KafkaAvroReflectSerializer.ReflectContainer(arg1.getEntity())));
                        System.out.println("enviou");
                    }
                });
                thread.start();
            } catch (NamingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }			
        }
    }

}
