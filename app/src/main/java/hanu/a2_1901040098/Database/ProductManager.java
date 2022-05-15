package hanu.a2_1901040098.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

import hanu.a2_1901040098.Models.Product;

public class ProductManager {
    private SQLiteDatabase sqLiteDatabase;

   public ProductManager(Context context){
       DBHelper dbHelper = new DBHelper(context);
       sqLiteDatabase = dbHelper.getWritableDatabase();
   }
   public List<Product> allProduct(){
       List<Product> products = new ArrayList<>();
       Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Cart WHERE quantity > 0", null);

       int indexId = cursor.getColumnIndex("id");
       int indexName = cursor.getColumnIndex("name");
       int indexThumbnail = cursor.getColumnIndex("thumbnail");
       int indexUnitPrice = cursor.getColumnIndex("price");
       int indexQuantity = cursor.getColumnIndex("quantity");

       while (cursor.moveToNext()){
           int id = cursor.getInt(indexId);
           String name = cursor.getString(indexName);
           String thumbnail = cursor.getString(indexThumbnail);
           int price = cursor.getInt(indexUnitPrice);
           int quantity = cursor.getInt(indexQuantity);

           Product product = new Product(id, name, thumbnail, price, quantity);
           products.add(product);
       }
       cursor.close();
       return products;
   }
}

