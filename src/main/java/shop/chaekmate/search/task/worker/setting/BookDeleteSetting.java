package shop.chaekmate.search.task.worker.setting;

import lombok.Getter;

@Getter
public class BookDeleteSetting implements BookSetting{
    @Override
    public String name() {
        return "BookDeleteThread-";
    }

    @Override
    public int threadSize() {
        return 1;
    }
}
