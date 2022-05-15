package hanu.a2_1901040098.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyCart";
    private static final int DB_VERSION = 1443;

    public DBHelper(@Nullable Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("CREATE TABLE Cart(" + "id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                                "thumbnail TEXT NOT NULL," +
                                "name TEXT NOT NULL," +
                                "quantity INT," +
                                "price INT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE Cart");

        onCreate(sqLiteDatabase);
    }
}
