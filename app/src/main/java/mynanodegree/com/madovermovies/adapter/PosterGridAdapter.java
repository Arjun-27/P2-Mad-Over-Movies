package mynanodegree.com.madovermovies.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import mynanodegree.com.madovermovies.AppConstants;
import mynanodegree.com.madovermovies.R;
import mynanodegree.com.madovermovies.data.MovieContract;
import mynanodegree.com.madovermovies.data.MovieData;

/**
 * Created ba Arjun Chouhan on 29-02-2016.
 */

public class PosterGridAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<MovieData> posterData;
    private boolean isOfflineOrFav;

    public PosterGridAdapter(Activity activity, ArrayList<MovieData> posterData, boolean isOfflineOrFav) {

        this.activity = activity;
        this.posterData = posterData;
        this.isOfflineOrFav = isOfflineOrFav;
    }

    @Override
    public int getCount() {
        return posterData != null ? posterData.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return posterData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.grid_poster, parent, false);
        }

        ImageView poster = (ImageView) convertView;
        if(isOfflineOrFav) {
            Cursor c = activity.getContentResolver().query(MovieContract.Favourites.CONTENT_URI, null, MovieContract.Favourites.MOVIE_ID + "=" + posterData.get(position).getId(), null, null);
            if(c != null && c.moveToFirst()) {
                poster.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(c.getBlob(1))));
                c.close();
            }
        } else {
            Picasso.with(activity)
                    .load(AppConstants.BASE_IMAGE_PATH + posterData.get(position).getPoster_path())
                    .placeholder(R.drawable.placeholder)
                    .error(R.mipmap.ic_launcher)
                    .into(poster);
        }
        return poster;
    }
}
