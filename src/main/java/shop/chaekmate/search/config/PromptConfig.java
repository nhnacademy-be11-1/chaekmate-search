package shop.chaekmate.search.config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromptConfig {
    @Bean
    public PromptTemplate searchPromptTemplate() {
        return new PromptTemplate("""
                너는 검색 결과 리랭킹 엔진이다.
                사용자의 질의: "{input}"
                
                아래에는 두 종류의 검색 결과가 주어진다.
                1) 키워드 기반 검색 결과 (keywordSearch)
                2) 벡터 기반 검색 결과 (vectorSearch)
                
                주어진 두 검색 결과(keywordSearch, vectorSearch)를 참고하되,
                사용자의 질의와 가장 관련성이 높은 결과를 선택하고 정렬한다.
                
                출력 규칙:
                - 숫자만 담긴 배열로 반환한다. 배열만 반환한다.
                - 배열 이외의 어떤 문자(설명, 코드블록, 주석 등)도 출력하지 않는다.
                
                keywordSearch:
                {keywordSearch}
                
                vectorSearch:
                {vectorSearch}
                """);
    }
}
