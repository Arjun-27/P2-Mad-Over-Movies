package mynanodegree.com.madovermovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mynanodegree.com.madovermovies.fragments.DetailsMainFragment;
import mynanodegree.com.madovermovies.R;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if(savedInstanceState == null) {
            DetailsMainFragment fragDetails = new DetailsMainFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable("MovieData", getIntent().getParcelableExtra("MovieData"));
            arguments.putByteArray("poster", getIntent().getByteArrayExtra("poster"));
            arguments.putBoolean("offlineStatus", getIntent().getBooleanExtra("offlineStatus", false));
            fragDetails.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragFrame, fragDetails)
                    .commit();
        }
    }
}