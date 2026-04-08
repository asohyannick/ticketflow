package com.boaz.ticket_service.config.kafka;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConfig {

		@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
		private String bootstrapServers;
		
		@Bean
		public ConsumerFactory<String, Object> consumerFactory() {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, "ticket-service-group");
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					JsonDeserializer.class);
			props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
			props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
					"java.util.HashMap");
			return new DefaultKafkaConsumerFactory<>(props);
		}
		
		@Bean
		public ConcurrentKafkaListenerContainerFactory<String, Object>
		kafkaListenerContainerFactory() {
			ConcurrentKafkaListenerContainerFactory<String, Object> factory =
					new ConcurrentKafkaListenerContainerFactory<>();
			factory.setConsumerFactory(consumerFactory());
			return factory;
		}
}