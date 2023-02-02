package common;

public class StockObject {
    private Long dishId;
    private int nrInStock;

    protected StockObject(){}
    public StockObject(Long dishId, int nrInStock) {
        this.dishId = dishId;
        this.nrInStock = nrInStock;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public int getNrInStock() {
        return nrInStock;
    }

    public void setNrInStock(int nrInStock) {
        this.nrInStock = nrInStock;
    }

    public void takeOne(){
        this.nrInStock -= 1;
    }
}
