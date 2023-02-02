package common.messages;

import common.StockObject;

import java.util.List;

public class RandomStockCheckReply {
    private List<StockObject> stock;

    protected RandomStockCheckReply(){}
    public RandomStockCheckReply(List<StockObject> stock) {
        this.stock = stock;
    }

    public List<StockObject> getStock() {
        return stock;
    }

    public void setStock(List<StockObject> stock) {
        this.stock = stock;
    }
}
