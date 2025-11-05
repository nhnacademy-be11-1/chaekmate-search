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

    @Bean
    public PromptTemplate groupPromptTemplate() {
        return new PromptTemplate("""
        너는 사용자의 검색 결과를 분석하여, 의미적으로 일관되고 검색어 추천에 적합한 "그룹 이름"을 생성하는 엔진이다.

        입력:
        - 검색어(keyword): {keyword}
        - 검색어 임베딩 벡터(keywordVector): {keywordVector}
        - 검색 결과 목록(results): {results}

        목표:
        - 사용자가 다시 검색창에 입력하고 싶을 만큼 자연스럽고 직관적인 추천 검색어를 1개 생성하라.
        - 생성된 그룹명은 keyword와 0.8 이상의 코사인 유사도를 가지며,
          results의 공통 주제나 분위기를 압축적으로 표현해야 한다.

        작성 규칙:
        1. keyword를 그대로 반복하지 말고, 의미를 확장하거나 함축한 형태로 변형하라.
        2. "추천", "모음", "작", "리스트" 같은 상투적 단어는 사용하지 말라.
        3. 3~6 단어 이내의 자연스러운 명사구 또는 검색어 형태로 작성하라.
        4. 출력은 반드시 JSON 객체 하나만으로 제한하라.
        5. "출력은 반드시 JSON만 포함해야 하며, ```json, ``` 등의 코드블록을 포함하지 말라."
        6. json키는 groupName으로 한다
        keyword: {keyword}
        keywordVector: {keywordVector}
        results: {results}
        """);
    }

}
