package shop.chaekmate.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String esHost;
    @Value("${spring.elasticsearch.username}")
    private String username;
    @Value("${spring.elasticsearch.password}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.deactivateDefaultTyping();

        RestClientBuilder builder = RestClient.builder(HttpHost.create(esHost))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(5_000)
                        .setSocketTimeout(60_000)
                )
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setConnectionTimeToLive(30, TimeUnit.SECONDS)
                        .setKeepAliveStrategy((response, context) -> 30_000)
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(50)
                )
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization",
                                "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                });

        RestClient restClient = builder.build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper(mapper)
        );

        return new ElasticsearchClient(transport);
    }
}
