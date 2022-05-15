package hanu.a2_1901040098.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import hanu.a2_1901040098.CartActivity;
import hanu.a2_1901040098.Database.DBHelper;
import hanu.a2_1901040098.Models.Constants;
import hanu.a2_1901040098.Models.Product;
import hanu.a2_1901040098.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    private List<Product> products;
    private Context context;
    public CartAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }
    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cart_product, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder cartHolder, @SuppressLint("RecyclerView") int position){
        cartHolder.txtProductprice.setText(Integer.toString(products.get(position).getUnitPrice()) + " VND");
        cartHolder.txtProductname.setText(products.get(position).getName());
        DBHelper dbHelper = new DBHelper(this.context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String query = "DELETE FROM Cart WHERE id = "+products.get(position).getId()+ ";";
        cartHolder.txtQuantity.setText(Integer.toString(products.get(position).getQuantity()));
        cartHolder.txtSum.setText(Integer.toString(products.get(position).getUnitPrice() * products.get(position).getQuantity()));

        cartHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                products.get(position).setQuantity(products.get(position).getQuantity() + 1);
                String increase = "UPDATE Cart SET quantity = quantity + 1 WHERE id = "+ products.get(position).getId();
                sqLiteDatabase.execSQL(increase);
                sqLiteDatabase.close();
                TextView txtPrice = ((CartActivity)context).findViewById(R.id.txtPrice);
                int totalPrice = 0;
                for(int i = 0;i < products.size();i++){
                    totalPrice = totalPrice + products.get(i).getUnitPrice() * products.get(i).getQuantity();
                }
                txtPrice.setText(totalPrice + "VND");
                notifyItemChanged(position);
            }
        });
        cartHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(products.get(position).getQuantity() == 1){
                    Toast.makeText(context,"Successfully deleted product form cart", Toast.LENGTH_SHORT).show();
                    sqLiteDatabase.execSQL(query);
                    products.remove(products.get(position));

                }
                else {
                    products.get(position).setQuantity(products.get(position).getQuantity() - 1);
                    String isUpdated = "UPDATE Cart SET quantity = quantity - 1 WHERE id = " + products.get(position).getId();
                    sqLiteDatabase.execSQL(isUpdated);
                }
                sqLiteDatabase.close();
                TextView txtPrice = ((CartActivity)context).findViewById(R.id.txtPrice);
                int totalPrice = 0;
                for (int i = 0;i<products.size();i++){
                    totalPrice= Math.abs(totalPrice - products.get(i).getUnitPrice()*products.get(i).getQuantity());
                }
                txtPrice.setText(totalPrice + "VND");
                notifyItemChanged(position);
            }
        });
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(products.get(position).getThumbnail());
                if (bitmap != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cartHolder.imgProduct.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return products.size();
    }

    public class CartHolder extends RecyclerView.ViewHolder {
        private TextView txtProductname, txtProductprice, txtQuantity, txtSum;
        private ImageButton btnMinus, btnPlus;
        private ImageView imgProduct;
        private Button btnCheckout;

        public CartHolder(@NonNull View view) {
            super(view);
            txtProductname = itemView.findViewById(R.id.txtProductname);
            txtProductprice = itemView.findViewById(R.id.txtProductprice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtSum = itemView.findViewById(R.id.txtSum);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnCheckout = itemView.findViewById(R.id.btnCheckout);
        }
    }
    private Bitmap downloadImage(String link){
        try{
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

