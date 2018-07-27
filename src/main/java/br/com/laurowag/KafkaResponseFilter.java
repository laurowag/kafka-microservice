package br.com.laurowag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.avro.Schema;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Provider
public class KafkaResponseFilter implements ContainerResponseFilter {
	
	public KafkaResponseFilter() {
		System.out.println("**** CRIOU FILTER ****");
	}

	@Override
	public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
		if (arg1.getEntity() != null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", "localhost:9092");
			props.put("group.id","test");
			props.put("enable.auto.commit","true");
			props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
			props.put("max.partition.fetch.bytes","2097152");
			
			ReflectData reflectData = ReflectData.AllowNull.get();
		    Schema schema = reflectData.getSchema(arg1.getEntity().getClass());
	
		    ReflectDatumWriter<Object> writer = new ReflectDatumWriter<Object>(schema);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			KafkaProducer<String, Object> producer = new KafkaProducer<>(props);
			
			try {
				try {
					writer.write(arg1.getEntity(), EncoderFactory.get().directBinaryEncoder(out, null));
					producer.send(new ProducerRecord<String, Object>("test", "cliente", out.toByteArray()));
				} catch (IOException e) {
					e.printStackTrace();
				}				
			} finally {
				producer.close();
			}
		}
	}

}
