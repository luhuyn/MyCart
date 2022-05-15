package hanu.a2_1901040098;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import hanu.a2_1901040098.Adapters.CartAdapter;
import hanu.a2_1901040098.Database.ProductManager;
import hanu.a2_1901040098.Models.Product;

public class CartActivity extends AppCompatActivity {

    RecyclerView rvCart;
    public TextView txtTotal;
    List<Product> products;
    CartAdapter cartAdapter;
    ProductManager productManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart = findViewById(R.id.rvCart);
        rvCart.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        productManager =new ProductManager(CartActivity.this);
        products = productManager.allProduct();
        cartAdapter = new CartAdapter(products, CartActivity.this);
        rvCart.setAdapter(cartAdapter);
    }
    @Override
    protected void onResume(){
        super.onResume();
        int totalPrice = 0;
        TextView txtPrice = findViewById(R.id.txtPrice);
        for(int i = 0;i <products.size();i++){
            totalPrice = totalPrice + products.get(i).getUnitPrice()*products.get(i).getQuantity();
        }
        txtPrice.setText(totalPrice + " VND");
    }
}