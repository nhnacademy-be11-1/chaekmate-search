package shop.chaekmate.search.repository;

import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import shop.chaekmate.search.document.KeywordGroup;

public interface KeywordGroupRepository extends ElasticsearchRepository<KeywordGroup, UUID> {
}
