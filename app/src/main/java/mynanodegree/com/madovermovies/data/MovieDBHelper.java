package mynanodegree.com.madovermovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arjun on 4/20/16 for MadOverMovies.
 */
public class MovieDBHelper extends SQLiteOpenHelper{
    public MovieDBHelper(Context context) {
        super(context, "movies.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE favourites (_id INTEGER PRIMARY KEY AUTOINCREMENT,poster  BLOB,overview  TEXT,release_date  TEXT,id  TEXT,original_title  TEXT,title  TEXT,backdrop  BLOB,popularity  TEXT,vote_count  TEXT,vote_average  TEXT,UNIQUE (id) ON CONFLICT REPLACE)");
        db.execSQL("CREATE TABLE trailers (_id INTEGER PRIMARY KEY AUTOINCREMENT,name  TEXT,vkey  TEXT,id  TEXT,UNIQUE (vkey) ON CONFLICT REPLACE)");
        db.execSQL("CREATE TABLE reviews (_id INTEGER PRIMARY KEY AUTOINCREMENT,id_reviews  TEXT,author  TEXT ,content  TEXT,id  TEXT,UNIQUE (id_reviews) ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favourites");
        db.execSQL("DROP TABLE IF EXISTS trailers");
        db.execSQL("DROP TABLE IF EXISTS reviews");
    }
}
