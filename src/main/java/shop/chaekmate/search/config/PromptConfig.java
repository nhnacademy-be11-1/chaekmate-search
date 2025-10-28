package shop.chaekmate.search.config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromptConfig {
    @Bean
    public PromptTemplate searchPromptTemplate() {
        PromptTemplate template = new PromptTemplate("""
                너는 검색 결과 리랭킹 엔진이다.
                사용자의 질의: "{input}"
                
                아래에는 두 종류의 검색 결과가 주어진다.
                1) 키워드 기반 검색 결과 (keywordSearch)
                2) 벡터 기반 검색 결과 (vectorSearch)
                
                두 결과를 relevance가 높은 순서대로 통합 정렬하라.
                출력은 JSON 배열로만 반환한다.
                각 항목은 id 필드만 포함한다.
                
                keywordSearch:
                {keywordSearch}
                
                vectorSearch:
                {vectorSearch}
                """);
        return template;
    }
}
