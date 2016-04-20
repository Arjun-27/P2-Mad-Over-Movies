package mynanodegree.com.madovermovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Arjun on 4/20/16 for MadOverMovies.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "mynanodegree.com.madovermovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITES = "favourites";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";

    public static final class Favourites implements BaseColumns {
        public static final Uri CONTENT_URI = MovieContract.BASE_CONTENT_URI.buildUpon().appendPath("favourites").build();

        public static final String BACKDROP = "backdrop";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/favourites";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/favourites";
        public static final String MOVIE_ID = "id";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String OVERVIEW = "overview";
        public static final String POPULARITY = "popularity";
        public static final String POSTER = "poster";
        public static final String RELEASE_DATE = "release_date";
        public static final String TABLE_NAME = "favourites";
        public static final String TITLE = "title";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String VOTE_COUNT = "vote_count";

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Reviews implements BaseColumns {
        public static final Uri CONTENT_URI = MovieContract.BASE_CONTENT_URI.buildUpon().appendPath("reviews").build();

        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/reviews";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/reviews";
        public static final String ID_REVIEWS = "id_reviews";
        public static final String MOVIE_ID = "id";
        public static final String TABLE_NAME = "reviews";

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Trailers implements BaseColumns {
        public static final Uri CONTENT_URI = MovieContract.BASE_CONTENT_URI.buildUpon().appendPath("trailers").build();

        public static final String MOVIE_ID = "id";
        public static final String NAME = "name";
        public static final String TABLE_NAME = "trailers";
        public static final String VKEY = "vkey";

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
