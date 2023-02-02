package common.messages;

public class RandomStockCheck {
    private String s;

    protected RandomStockCheck(){}
    public RandomStockCheck(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
