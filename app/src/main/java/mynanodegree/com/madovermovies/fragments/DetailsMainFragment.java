package mynanodegree.com.madovermovies.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import mynanodegree.com.madovermovies.AppConstants;
import mynanodegree.com.madovermovies.CheckNetworkConnection;
import mynanodegree.com.madovermovies.JSONParser;
import mynanodegree.com.madovermovies.R;
import mynanodegree.com.madovermovies.data.MovieContract;
import mynanodegree.com.madovermovies.data.MovieData;

/**
 * Created by Arjun on 4/16/16 for MadOverMovies.
 */
public class DetailsMainFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    private FragmentTabHost fragTabHost;
    private ImageView imageDrop, imageScrim;
    private Toolbar toolbar;
    private FloatingActionButton markAsFav;
    private MovieData movieData;
    private ArrayList<String> keys, reviews;
    private boolean isOfflineOrFav;

    public DetailsMainFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieData = getArguments().getParcelable("MovieData");
        isOfflineOrFav = getArguments().getBoolean("offlineStatus");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_main, container, false);

        setHasOptionsMenu(true);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        imageDrop = (ImageView) rootView.findViewById(R.id.imageDrop);
        imageScrim = (ImageView) rootView.findViewById(R.id.imageScrim);
        markAsFav = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fragTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        markAsFav.setVisibility(View.INVISIBLE);

        toolbar.setTitle(movieData.getTitle());

        new TrailerFetch().execute(AppConstants.BASE_PATH_TRAILER.replace("**", movieData.getId()));
        new ReviewsFetch().execute(AppConstants.BASE_REVIEWS_PATH.replace("**", movieData.getId()));

        if(CheckNetworkConnection.isNetworkAvailable(getActivity())) {
            Picasso.with(getActivity()).load(AppConstants.BASE_IMAGE_PATH + movieData.getBackdrop_path()).placeholder(R.color.colorAccent).into(imageDrop);
        } else if(isOfflineOrFav) {
            Cursor cursor = getActivity().getContentResolver().query(MovieContract.Favourites.CONTENT_URI, null, MovieContract.Favourites.MOVIE_ID + "=" + movieData.getId(), null, null);
            if(cursor != null && cursor.moveToFirst()) {
                imageDrop.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(cursor.getBlob(7))));
                cursor.close();
            }
        } else {
            Snackbar.make(imageDrop, "Cannot load poster.. Check internet connection", Snackbar.LENGTH_LONG).show();
        }
        AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        final Bundle fragBundle = new Bundle();
        fragBundle.putParcelable("MovieData", movieData);

        fragTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        fragTabHost.addTab(fragTabHost.newTabSpec("FragSynopsis").setIndicator("Synopsis"), MovieDetailFragment.class, fragBundle);

        alterTabTextColors();

        markAsFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(markAsFav, "Already added to favourites..", Snackbar.LENGTH_SHORT).show();
                Cursor c = getActivity().getContentResolver().query(MovieContract.Favourites.CONTENT_URI, null, MovieContract.Favourites.MOVIE_ID + "=" + movieData.getId(), null, null);
                if(c != null && c.getCount() == 0) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        Bitmap bitmap = ((BitmapDrawable) imageDrop.getDrawable()).getBitmap();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] dropArr = baos.toByteArray();

                        ContentValues values = new ContentValues();
                        values.put(MovieContract.Favourites.POSTER, getArguments().getByteArray("poster"));
                        values.put(MovieContract.Favourites.OVERVIEW, movieData.getOverview());
                        values.put(MovieContract.Favourites.RELEASE_DATE, movieData.getRelease_date());
                        values.put(MovieContract.Favourites.MOVIE_ID, movieData.getId());
                        values.put(MovieContract.Favourites.ORIGINAL_TITLE, movieData.getOriginal_title());
                        values.put(MovieContract.Favourites.TITLE, movieData.getTitle());
                        values.put(MovieContract.Favourites.BACKDROP, dropArr);
                        values.put(MovieContract.Favourites.POPULARITY, movieData.getPopularity());
                        values.put(MovieContract.Favourites.VOTE_COUNT, movieData.getVote_count());
                        values.put(MovieContract.Favourites.VOTE_AVERAGE, movieData.getVote_average());

                        String keyAndName[] = keys.get(0).split("\\|");

                        ContentValues trailerVals = new ContentValues();
                        trailerVals.put(MovieContract.Trailers.NAME, keyAndName[1]);
                        trailerVals.put(MovieContract.Trailers.VKEY, keyAndName[0]);
                        trailerVals.put(MovieContract.Trailers.MOVIE_ID, movieData.getId());

                        ContentValues reviewVals = new ContentValues();

                        try {
                            ContentResolver resolver = getActivity().getContentResolver();
                            resolver.insert(MovieContract.Favourites.CONTENT_URI, values);
                            resolver.insert(MovieContract.Trailers.CONTENT_URI, trailerVals);
                            for (int i = 0; i < reviews.size(); i++) {
                                String arr[] = reviews.get(i).split("\\|");
                                reviewVals.put(MovieContract.Reviews.ID_REVIEWS, arr[0]);
                                reviewVals.put(MovieContract.Reviews.AUTHOR, arr[1]);
                                reviewVals.put(MovieContract.Reviews.CONTENT, arr[2]);
                                reviewVals.put(MovieContract.Reviews.MOVIE_ID, movieData.getId());
                                resolver.insert(MovieContract.Reviews.CONTENT_URI, reviewVals);
                            }
                            Snackbar.make(markAsFav, "Added to Favourites..", Snackbar.LENGTH_SHORT).show();
                        } catch (SQLException | UnsupportedOperationException e) {
                            Snackbar.make(markAsFav, "Failed to add to Favourites..", Snackbar.LENGTH_SHORT).show();
                        }

                        c.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        fragTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                alterTabTextColors();
            }
        });

        return rootView;
    }

    private void alterTabTextColors() {
        for(int i = 0; i < fragTabHost.getTabWidget().getChildCount(); i++) {
            if(!fragTabHost.getCurrentTabView().equals(fragTabHost.getTabWidget().getChildAt(i)))
                ((TextView)fragTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(Color.LTGRAY);
            else
                ((TextView)fragTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int toolBarHeight = toolbar.getMeasuredHeight();
        int appBarHeight = appBarLayout.getMeasuredHeight();
        Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ((float) appBarHeight - toolBarHeight)) * 255;
        imageScrim.getBackground().setAlpha(255 - Math.round(f));
    }

    private class TrailerFetch extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                keys = new ArrayList<>();
                if(!isOfflineOrFav) {
                    JSONArray arrayRes = new JSONObject(JSONParser.fetchJSON(params[0])).getJSONArray("results");

                    for (int i = 0; i < arrayRes.length(); i++)
                        keys.add(((JSONObject) arrayRes.get(i)).getString("key") + "|" + ((JSONObject) arrayRes.get(i)).getString("name"));
                } else {
                    Cursor cursor = getActivity().getContentResolver().query(MovieContract.Trailers.CONTENT_URI, null, MovieContract.Trailers.MOVIE_ID + "=" + movieData.getId(), null, null);
                    if(cursor != null) {
                        while (cursor.moveToNext()) {
                            keys.add(cursor.getString(2) + "|" + cursor.getString(1));
                        }
                        cursor.close();
                    }
                }
                return keys;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(final ArrayList<String> result) {
            keys = result;
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("keys", result);
            fragTabHost.addTab(fragTabHost.newTabSpec("FragTrailers").setIndicator("Trailers"), TrailersFragment.class, bundle);
            alterTabTextColors();
        }
    }

    private class ReviewsFetch extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                reviews = new ArrayList<>();
                if(!isOfflineOrFav) {
                    JSONArray arrayReviews = new JSONObject(JSONParser.fetchJSON(params[0])).getJSONArray("results");

                    for (int i = 0; i < arrayReviews.length(); i++) {
                        JSONObject arrObj = (JSONObject) arrayReviews.get(i);
                        reviews.add(arrObj.getString("id") + "|" + arrObj.getString("author") + "|" + arrObj.getString("content"));
                    }
                } else {
                    Cursor cursor = getActivity().getContentResolver().query(MovieContract.Reviews.CONTENT_URI, null, MovieContract.Reviews.MOVIE_ID + "=" + movieData.getId(), null, null);
                    if(cursor != null) {
                        Log.d("total", ""+cursor.getCount());
                        while (cursor.moveToNext()) {
                            reviews.add(cursor.getString(1) + "|" + cursor.getString(2) + "|" + cursor.getString(3));
                        }
                        cursor.close();
                    }
                }

                return reviews;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(ArrayList<String> result) {
            markAsFav.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("reviews", result);
            fragTabHost.addTab(fragTabHost.newTabSpec("FragReviews").setIndicator("Reviews"), ReviewsFragment.class, bundle);
            alterTabTextColors();
        }
    }
}
