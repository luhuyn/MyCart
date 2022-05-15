package hanu.a2_1901040098.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
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
import java.util.ArrayList;
import java.util.List;

import hanu.a2_1901040098.Database.DBHelper;
import hanu.a2_1901040098.Models.Constants;
import hanu.a2_1901040098.Models.Product;
import hanu.a2_1901040098.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {
    private List<Product> products;
    private Context context;
    List<Product> productsSearched;

    public ListAdapter(List<Product> products,Context context){
        this.products = products;
        this.context = context;
        this.productsSearched = products;
    }


    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_product, parent, false);
        return new ListAdapter.ListHolder(view, context);
    }

    @Override
    public  void onBindViewHolder(@NonNull ListHolder listHolder, @SuppressLint("RecyclerView") int position){
        listHolder.productPrice.setText(Integer.toString(products.get(position).getUnitPrice()) +" VND");
        listHolder.productName.setText(products.get(position).getName());

        DBHelper dbHelper = new DBHelper(this.context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO Cart(id, name, thumbnail, price, quantity) VALUES(?, ?, ?, ?, ?)";
        String query = "Select * from Cart where id = " + products.get(position).getId();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        String isUpdated = "UPDATE Cart\n" + "SET quantity= quanity + 1" + " WHERE id =" +products.get(position).getId()+";" ;
        SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sql);

        listHolder.btnAddtocart.setOnClickListener(new View.OnClickListener() {
            boolean isAdded = false;
            @Override
            public void onClick(View view) {
                if (cursor.getCount() <= 0){
                    cursor.close();
                }else {
                    isAdded = true;
                    cursor.close();
                }
                if(!isAdded){
                    Toast.makeText(context,"Successfully added this product to cart", Toast.LENGTH_SHORT).show();
                    sqLiteStatement.bindLong(1, products.get(position).getId());
                    sqLiteStatement.bindString(2, products.get(position).getName());
                    sqLiteStatement.bindString(3, products.get(position).getThumbnail());
                    sqLiteStatement.bindLong(4, products.get(position).getUnitPrice());
                    sqLiteStatement.bindLong(5, 1);
                    sqLiteStatement.executeInsert();
                    sqLiteDatabase.close();
                }
                else {
                    Toast.makeText(context,"Successfully added this product to cart", Toast.LENGTH_SHORT).show();
                    sqLiteDatabase.execSQL(isUpdated);
                }
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
                            listHolder.productPhoto.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
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

    @Override
    public int getItemCount(){
        return products.size();
    }
    public class ListHolder extends RecyclerView.ViewHolder{
        private TextView productName, productPrice;
        private ImageButton btnAddtocart;
        private ImageView productPhoto;
        private Context context;

        public ListHolder(@NonNull View view, Context context){
            super(view);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productPhoto = itemView.findViewById(R.id.productPhoto);
            btnAddtocart = itemView.findViewById(R.id.btnAddtocart);
            this.context = context;
        }

    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String keyword = charSequence.toString();
                if (keyword.isEmpty()){
                    productsSearched = products;
                }
                else {
                    List<Product> productResults = new ArrayList<>();
                    for(Product product: products){
                        if (product.getName().toLowerCase().contains(keyword.toLowerCase())){
                            productResults.add(product);
                        }
                    }
                    productsSearched = productResults;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = productsSearched;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productsSearched = (List<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}


