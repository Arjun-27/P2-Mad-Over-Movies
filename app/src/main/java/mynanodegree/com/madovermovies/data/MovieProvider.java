package mynanodegree.com.madovermovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Arjun on 4/20/16 for MadOverMovies.
 */
public class MovieProvider extends ContentProvider {
    static final int FAVOURITES = 500;
    static final int FAVOURITES_WITH_ID = 501;
    static final int REVIEWS = 300;
    static final int REVIEWS_WITH_ID = 301;
    static final int TRAILERS = 200;
    static final int TRAILERS_WITH_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static UriMatcher buildUriMatcher()
    {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("mynanodegree.com.madovermovies", "trailers", 200);
        uriMatcher.addURI("mynanodegree.com.madovermovies", "trailers/*", 201);
        uriMatcher.addURI("mynanodegree.com.madovermovies", "reviews", 300);
        uriMatcher.addURI("mynanodegree.com.madovermovies", "reviews/*", 301);
        uriMatcher.addURI("mynanodegree.com.madovermovies", "favourites", 500);
        uriMatcher.addURI("mynanodegree.com.madovermovies", "favourites/*", 501);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        this.mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case FAVOURITES:
                return this.mOpenHelper.getReadableDatabase().query("favourites", projection, selection, selectionArgs, null, null, sortOrder);
            case FAVOURITES_WITH_ID:
                return this.mOpenHelper.getReadableDatabase().query("favourites", projection, "id = ?", new String[] {String.valueOf(ContentUris.parseId(uri))}, null, null, sortOrder);
            case TRAILERS:
                return this.mOpenHelper.getReadableDatabase().query("trailers", projection, selection, selectionArgs, null, null, sortOrder);
            case TRAILERS_WITH_ID:
                return this.mOpenHelper.getReadableDatabase().query("trailers", projection, "id = ?", new String[] {String.valueOf(ContentUris.parseId(uri))}, null, null, sortOrder);
            case REVIEWS:
                return this.mOpenHelper.getReadableDatabase().query("reviews", projection, selection, selectionArgs, null, null, sortOrder);
            case REVIEWS_WITH_ID:
                return this.mOpenHelper.getReadableDatabase().query("reviews", projection, "id = ?", new String[] {String.valueOf(ContentUris.parseId(uri))}, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FAVOURITES:
                return "vnd.android.cursor.dir/work.technie.popularmovies/favourites";
            case FAVOURITES_WITH_ID:
                return "vnd.android.cursor.item/work.technie.popularmovies/favourites";
            case TRAILERS:
                return "vnd.android.cursor.dir/work.technie.popularmovies/trailers";
            case TRAILERS_WITH_ID:
                return "vnd.android.cursor.item/work.technie.popularmovies/trailers";
            case REVIEWS:
                return "vnd.android.cursor.dir/work.technie.popularmovies/reviews";
            case REVIEWS_WITH_ID:
                return "vnd.android.cursor.item/work.technie.popularmovies/reviews";
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        long l;
        switch (sUriMatcher.match(uri)) {
            case FAVOURITES:
                l = db.insert("favourites", null, values);
                if(l > 0L)
                    return MovieContract.Favourites.buildMoviesUri(l);
                else
                    throw new SQLException("Failed to insert row.. " + uri);
            case TRAILERS:
                l = db.insert("trailers", null, values);
                if(l > 0L)
                    return MovieContract.Trailers.buildMoviesUri(l);
                else
                    throw new SQLException("Failed to insert row.. " + uri);
            case REVIEWS:
                l = db.insert("reviews", null, values);
                if(l > 0L)
                    return MovieContract.Reviews.buildMoviesUri(l);
                else
                    throw new SQLException("Failed to insert row.. " + uri);
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
