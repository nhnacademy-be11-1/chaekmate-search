package shop.chaekmate.search.task.worker.setting;

public class BookEmbeddingSetting implements BookSetting{

    @Override
    public String name() {
        return "BookEmbeddingThread-";
    }

    @Override
    public int threadSize() {
        return 10;
    }
}
