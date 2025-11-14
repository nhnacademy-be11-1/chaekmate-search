package shop.chaekmate.search.task.worker.setting;

public class BookSaveSetting implements BookSetting{

    @Override
    public String name() {
        return "BookSaveThread-";
    }

    @Override
    public int threadSize() {
        return 2;
    }
}
