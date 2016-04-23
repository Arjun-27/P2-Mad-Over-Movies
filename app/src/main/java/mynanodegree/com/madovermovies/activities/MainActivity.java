package mynanodegree.com.madovermovies.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import mynanodegree.com.madovermovies.AppConstants;
import mynanodegree.com.madovermovies.CheckNetworkConnection;
import mynanodegree.com.madovermovies.fragments.DetailsMainFragment;
import mynanodegree.com.madovermovies.JSONParser;
import mynanodegree.com.madovermovies.data.MovieData;
import mynanodegree.com.madovermovies.adapter.PosterGridAdapter;
import mynanodegree.com.madovermovies.R;
import mynanodegree.com.madovermovies.data.MovieContract;

public class MainActivity extends AppCompatActivity {

    private GridView moviePosters;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<MovieData> data;
    private TextView emptyView;
    private int page = 2, pos;
    private boolean isLoading = false, stateRestored = false, isTwoPane = false, isOfflineOrFav = false;
    private String sortCriteria = "popularity.desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        moviePosters = (GridView) findViewById(R.id.gridPosters);
        data = new ArrayList<>();

        emptyView = (TextView)findViewById(android.R.id.empty);
        moviePosters.setEmptyView(emptyView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            isTwoPane = true;
        }
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);

        if(savedInstanceState != null) {
            data = savedInstanceState.getParcelableArrayList("movieList");
            pos = savedInstanceState.getInt("posToScroll");
            sortCriteria = savedInstanceState.getString("sortBy");
            page = savedInstanceState.getInt("numPages");

            if(sortCriteria.equals("--"))
                moviePosters.setAdapter(new PosterGridAdapter(MainActivity.this, data, true));
            else
                moviePosters.setAdapter(new PosterGridAdapter(MainActivity.this, data, false));

            moviePosters.smoothScrollToPosition(pos);
            stateRestored = true;
            moviePosters.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    pos = view.getLastVisiblePosition();
                    if(view.getFirstVisiblePosition() < 4)
                        refreshLayout.setEnabled(true);
                    else
                        refreshLayout.setEnabled(false);
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && !isLoading && view.getAdapter().getCount() != 0) {
                        // Till end of this view has been scrolled
                        if (CheckNetworkConnection.isNetworkAvailable(MainActivity.this) && !sortCriteria.equals("--")) {
                            new DiscoverMovies().execute(AppConstants.BASE_PATH_DISCOVER + "&sort_by=" + sortCriteria + "&page=" + page++);
                        }
                    }
                }
            });
        } else {
            if (CheckNetworkConnection.isNetworkAvailable(this) && !sortCriteria.equals("--")) {
                isOfflineOrFav = false;
                new DiscoverMovies().execute(AppConstants.BASE_PATH_DISCOVER + "&sort_by=" + sortCriteria);
            } else {
                isOfflineOrFav = true;
                emptyView.setText(R.string.str_no_internet);
            }
        }

        refreshLayout.setNestedScrollingEnabled(true);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!sortCriteria.equals("--")) {
                    if(CheckNetworkConnection.isNetworkAvailable(MainActivity.this)) {
                        emptyView.setText(R.string.str_refreshing);
                        isOfflineOrFav = false;
                        data.clear();
                        if(moviePosters.getAdapter() != null)
                            ((BaseAdapter) moviePosters.getAdapter()).notifyDataSetChanged();
                        new DiscoverMovies().execute(AppConstants.BASE_PATH_DISCOVER + "&sort_by=" + sortCriteria + "&page=" + 1);
                    } else {
                        isOfflineOrFav = true;
                        emptyView.setText(R.string.str_no_internet);
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isLoading)
                            new Handler().postDelayed(this, 900);
                        else
                            refreshLayout.setRefreshing(false);
                    }
                }, 900);
            }
        });

        moviePosters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                    byte[] byteArr = baos.toByteArray();

                    if (!isTwoPane)
                        startActivity(new Intent(MainActivity.this, MovieDetailsActivity.class).putExtra("MovieData", data.get(position)).putExtra("poster", byteArr).putExtra("offlineStatus", isOfflineOrFav));
                    else {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("MovieData", data.get(position));
                        arguments.putByteArray("poster", byteArr);
                        arguments.putBoolean("offlineStatus", isOfflineOrFav);
                        DetailsMainFragment fragment = new DetailsMainFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class DiscoverMovies extends AsyncTask<String, Void, ArrayList<MovieData>> {
        @Override
        protected ArrayList<MovieData> doInBackground(String... params) {
            try {
                isLoading = true;

                JSONObject object = new JSONObject(JSONParser.fetchJSON(params[0]));
                JSONArray array = object.getJSONArray("results");
                ArrayList<MovieData> list = new ArrayList<>();

                for(int i = 0; i < array.length(); i++) {
                    MovieData movieData = new MovieData();
                    JSONObject data = (JSONObject) array.get(i);

                    movieData.setPoster_path(data.getString("poster_path"));
                    movieData.setAdult(data.getString("adult"));
                    movieData.setOverview(data.getString("overview"));
                    movieData.setRelease_date(data.getString("release_date"));
                    movieData.setGenre_ids(data.getString("genre_ids"));
                    movieData.setId(data.getString("id"));
                    movieData.setOriginal_title(data.getString("original_title"));
                    movieData.setOriginal_language(data.getString("original_language"));
                    movieData.setTitle(data.getString("title"));
                    movieData.setBackdrop_path(data.getString("backdrop_path"));
                    movieData.setPopularity(data.getString("popularity"));
                    movieData.setVote_count(data.getString("vote_count"));
                    movieData.setVideo(data.getString("video"));
                    movieData.setVote_average(data.getString("vote_average"));

                    list.add(movieData);
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(ArrayList<MovieData> result) {
            if(moviePosters.getAdapter() == null) {
                data = result;
                isOfflineOrFav = false;
                moviePosters.setAdapter(new PosterGridAdapter(MainActivity.this, data, false));
            } else {
                data.addAll(result);
                ((BaseAdapter) moviePosters.getAdapter()).notifyDataSetChanged();
            }
            isLoading = false;
            deleteCache(MainActivity.this);

            if(!stateRestored) {
                moviePosters.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        pos = view.getLastVisiblePosition();
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        boolean isSwipeEnabled = false;
                        if (firstVisibleItem + visibleItemCount >= totalItemCount && !isLoading && view.getAdapter().getCount() != 0) {
                            // Till end of this view has been scrolled
                            if (CheckNetworkConnection.isNetworkAvailable(MainActivity.this) && !sortCriteria.equals("--")) {
                                isOfflineOrFav = false;
                                new DiscoverMovies().execute(AppConstants.BASE_PATH_DISCOVER + "&sort_by=" + sortCriteria + "&page=" + page++);
                            } else if(sortCriteria.equals("--")) {
                                isOfflineOrFav = true;
                            } else {
                                Snackbar.make(moviePosters, R.string.str_cant_load_movies, Snackbar.LENGTH_LONG).show();
                            }
                        }
                        if(view.getChildCount() > 0 && view.getFirstVisiblePosition() == 0 && view.getChildAt(0).getTop() == 0)
                            isSwipeEnabled = true;
                        refreshLayout.setEnabled(isSwipeEnabled);
                    }
                });
            }
        }
    }

    public static void deleteCache(Context context) {
        Log.d("CACHE", "DELETING...");
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        try {
            Log.d("POS", "" + pos);
            savedInstanceState.putString("sortBy", sortCriteria);
            savedInstanceState.putInt("posToScroll", pos);
            savedInstanceState.putParcelableArrayList("movieList", data);
            savedInstanceState.putInt("numPages", page);

            super.onSaveInstanceState(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(data != null) {
            data.clear();
            page = 1;
            switch (menuItem.getItemId()) {
                case R.id.most_popular:
                    sortCriteria = "popularity.desc";
                    moviePosters.setAdapter(null);
                    if(CheckNetworkConnection.isNetworkAvailable(MainActivity.this))
                        new DiscoverMovies().execute(AppConstants.BASE_PATH_DISCOVER + "&sort_by=" + sortCriteria + "&page=" + page++);
                    else
                        emptyView.setText(R.string.str_no_internet);
                    break;

                case R.id.highest_rated:
                    sortCriteria = "vote_average.desc";
                    moviePosters.setAdapter(null);
                    if(CheckNetworkConnection.isNetworkAvailable(MainActivity.this))
                        new DiscoverMovies().execute(AppConstants.BASE_PATH_DISCOVER + "&sort_by=" + sortCriteria + "&page=" + page++);
                    else
                        emptyView.setText(R.string.str_no_internet);
                    break;

                case R.id.favourites:
                    sortCriteria = "--";
                    isOfflineOrFav = true;
                    Cursor c = getContentResolver().query(MovieContract.Favourites.CONTENT_URI, null, null, null, null);
                    if(c != null) {
                        while (c.moveToNext()) {
                            MovieData movieData = new MovieData();

                            movieData.setPoster_path("--");
                            movieData.setAdult("--");
                            movieData.setGenre_ids("--");
                            movieData.setOriginal_language("--");
                            movieData.setBackdrop_path("--");
                            movieData.setVideo("--");
                            movieData.setOverview(c.getString(2));
                            movieData.setRelease_date(c.getString(3));
                            movieData.setId(c.getString(4));
                            movieData.setOriginal_title(c.getString(5));
                            movieData.setTitle(c.getString(6));
                            movieData.setPopularity(c.getString(8));
                            movieData.setVote_count(c.getString(9));
                            movieData.setVote_average(c.getString(10));

                            data.add(movieData);
                        }
                        Log.d("COUNT", ""+data.size());
                        moviePosters.setAdapter(new PosterGridAdapter(MainActivity.this, data, true));
                        c.close();
                    }
                    break;
            }
            deleteCache(MainActivity.this);
            if(moviePosters.getAdapter() != null)
                ((BaseAdapter) moviePosters.getAdapter()).notifyDataSetChanged();

            return true;
        }
        return false;
    }
}