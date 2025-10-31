package shop.chaekmate.search.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.queues.queue-names}")
    private List<String> queueNames;

    @Value("${rabbitmq.queues.routing-keys}")
    private List<String> routingKeys;
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Declarables bindings() {
        List<Declarable> declarables = new ArrayList<>();
        for (int i = 0; i < queueNames.size(); i++) {
            String q = queueNames.get(i);
            String r = routingKeys.get(i);

            Queue queue = QueueBuilder.durable(q).build();
            Binding binding = BindingBuilder.bind(queue)
                    .to(new TopicExchange(exchangeName))
                    .with(r);

            declarables.add(queue);
            declarables.add(binding);
        }

        return new Declarables(declarables);
    }
}
