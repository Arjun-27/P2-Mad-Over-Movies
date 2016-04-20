package mynanodegree.com.madovermovies.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mynanodegree.com.madovermovies.R;
import mynanodegree.com.madovermovies.data.MovieData;

public class MovieDetailFragment extends Fragment {
    private TextView releaseDate, synopsis, rating;
    private MovieData movieData;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            movieData = getArguments().getParcelable("MovieData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_details, container, false);

        if (movieData != null) {
            releaseDate = (TextView) rootView.findViewById(R.id.tvDate);
            rating = (TextView) rootView.findViewById(R.id.tvRate);
            synopsis = (TextView) rootView.findViewById(R.id.textSynopsis);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans.ttf");
            releaseDate.setTypeface(font);
            rating.setTypeface(font);
            synopsis.setTypeface(font);

            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            String strDate = "";
            try {
                Date date = originalFormat.parse(movieData.getRelease_date());
                strDate = targetFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            releaseDate.setText(releaseDate.getText().toString() + "\n\t" + strDate);
            rating.setText(rating.getText().toString() + "\n\t" + movieData.getVote_average());
            synopsis.setText(movieData.getOverview());
        }

        return rootView;
    }
}