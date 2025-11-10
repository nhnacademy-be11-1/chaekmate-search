package shop.chaekmate.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String esHost;
    @Value("${spring.elasticsearch.username}")
    private String username;
    @Value("${spring.elasticsearch.password}")
    private String password;

    @Bean(destroyMethod = "close")
    public ElasticsearchClient elasticsearchClient() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.deactivateDefaultTyping();

        RestClientBuilder builder = RestClient.builder(HttpHost.create(esHost))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000)
                )
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                        .setKeepAliveStrategy((response, context) -> 60000)
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(50)
                )
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization",
                                "Basic " + Base64.getEncoder().encodeToString(
                                        (username + ":" + password).getBytes(StandardCharsets.UTF_8)
                                ))
                });

        RestClient restClient = builder.build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper(mapper)
        );

        return new ElasticsearchClient(transport);
    }
}
