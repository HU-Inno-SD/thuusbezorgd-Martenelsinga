package stock.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stock.data.IngredientRepository;

import javax.transaction.Transactional;

@Transactional
@Service
public class StockService {
    private final IngredientRepository repo;
    public StockService(IngredientRepository ingredientRepository){
        this.repo = ingredientRepository;
    }

//    protected StockService() {
//
//    }

    public int checkStock(){
        return 0;
    }
}
