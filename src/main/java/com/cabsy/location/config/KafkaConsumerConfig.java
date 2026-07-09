package com.cabsy.location.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.cabsy.location.event.DriverLocationUpdatedEvent;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
    private String trustedPackages;

    @Bean
    public ConsumerFactory<String, DriverLocationUpdatedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JacksonJsonDeserializer<DriverLocationUpdatedEvent> valueDeserializer =
                new JacksonJsonDeserializer<>(DriverLocationUpdatedEvent.class);
        valueDeserializer.addTrustedPackages(trustedPackages);
        valueDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(valueDeserializer));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DriverLocationUpdatedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, DriverLocationUpdatedEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, DriverLocationUpdatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    /**
     * Malformed events fail at deserialization time - skip them with a warning instead of
     * retrying, so one bad payload can't block the partition or crash the consumer.
     */
    private DefaultErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(
                (record, exception) -> log.warn(
                        "Skipping unprocessable driver-location-updated record at offset {}: {}",
                        record.offset(), exception.getMessage()),
                new FixedBackOff(1000L, 3L));
        handler.addNotRetryableExceptions(DeserializationException.class);
        return handler;
    }
}
