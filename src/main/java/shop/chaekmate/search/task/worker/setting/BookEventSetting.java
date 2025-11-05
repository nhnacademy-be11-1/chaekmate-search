package shop.chaekmate.search.task.worker.setting;

public class BookEventSetting implements BookSetting{
    @Override
    public String name() {
        return "BookEventThread-";
    }

    @Override
    public int threadSize() {
        return 2;
    }
}
