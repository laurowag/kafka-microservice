package br.com.laurowag.rest;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.errors.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Extension of KafkaAvroSerializer that supports reflection-based serialization of values when
 * objects are wrapped in a ReflectContainer.
 */
public class KafkaAvroReflectSerializer extends KafkaAvroSerializer {
    public static class ReflectContainer {
        private final Object object;

        public ReflectContainer(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

    private static final EncoderFactory ENCODER_FACTORY = EncoderFactory.get();
    private static final ReflectData REFLECT_DATA = ReflectData.AllowNull.get();

    @Override
    protected byte[] serializeImpl(String subject, Object object) throws SerializationException {
        if (object instanceof ReflectContainer) {
            Object value = ((ReflectContainer) object).getObject();
            if (value == null) {
                return null;
            }
            Schema schema = REFLECT_DATA.getSchema(value.getClass());
            try {
                int registeredSchemaId = this.schemaRegistry.register(subject, schema);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(0);
                out.write(ByteBuffer.allocate(4).putInt(registeredSchemaId).array());

                DatumWriter<Object> dw = new ReflectDatumWriter<>(schema);
                Encoder encoder = ENCODER_FACTORY.directBinaryEncoder(out, null);
                dw.write(value, encoder);
                encoder.flush();
                return out.toByteArray();
            } catch (RuntimeException | IOException e) {
                e.printStackTrace();
                throw new SerializationException("Error serializing Avro message", e);
            } catch (RestClientException e) {
                e.printStackTrace();
                throw new SerializationException("Error registering Avro schema: " + schema, e);
            }
        }
        return super.serializeImpl(subject, object);
    }
}