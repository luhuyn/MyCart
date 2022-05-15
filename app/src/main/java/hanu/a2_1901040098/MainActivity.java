package hanu.a2_1901040098;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hanu.a2_1901040098.Adapters.ListAdapter;
import hanu.a2_1901040098.Models.Constants;
import hanu.a2_1901040098.Models.Product;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnSearch;
    private RecyclerView rvProductsList;
    RecyclerView.LayoutManager layoutManager;
    private static List<Product> products = new ArrayList<>();
    private EditText searchBar;
    CharSequence key="";
    private ListAdapter listAdapter = new ListAdapter(products, this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem){
        if (menuItem.getItemId() == R.id.btnCart){
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);
        rvProductsList = findViewById(R.id.rvProductsList);
        btnSearch = findViewById(R.id.btnSearch);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int begin, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int begin, int before, int count) {
                listAdapter.getFilter().filter(charSequence);
                key = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                String API_URL = "https://mpr-cart-api.herokuapp.com/products";
                String json = loadJSON(API_URL);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (json == null) {

                            Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("name");
                                    String thumbnail = jsonObject.getString("thumbnail");
                                    int unitPrice = jsonObject.getInt("unitPrice");
                                    products.add(new Product(id, name, thumbnail, unitPrice));
                                }
                                rvProductsList = findViewById(R.id.rvProductsList);
                                layoutManager = new GridLayoutManager(MainActivity.this, 2);
                                rvProductsList.setLayoutManager(layoutManager);
                                rvProductsList.setAdapter(listAdapter);
                                rvProductsList.setHasFixedSize(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }}
                });
            }
        });
    }

    public String loadJSON(String link) {
        URL url;
        HttpURLConnection urlConnection;
        try {
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuilder result = new StringBuilder();
            String line;
            while(sc.hasNextLine()) {
                line = sc.nextLine();
                result.append(line);
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Bitmap downloadImage(String link){
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

