package shop.chaekmate.search.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Declarables bindings() {
        List<Declarable> declarables = new ArrayList<>();

        String[][] queueConfigs = {
                {"cm-book-1", "cm-book-key-1"},
                {"cm-book-2", "cm-book-key-2"},
                {"cm-book-3", "cm-book-key-3"}
        };

        for (String[] cfg : queueConfigs) {
            Queue queue = new Queue(cfg[0], true);
            declarables.add(queue);
            declarables.add(BindingBuilder.bind(queue).to(exchange()).with(cfg[1]));
        }

        return new Declarables(declarables);
    }
}
