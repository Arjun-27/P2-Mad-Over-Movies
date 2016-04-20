package mynanodegree.com.madovermovies.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mynanodegree.com.madovermovies.AppConstants;
import mynanodegree.com.madovermovies.R;

/**
 * Created by Arjun on 4/19/16 for MadOverMovies.
 */
public class TrailerRecyclerAdapter extends RecyclerView.Adapter<TrailerViewHolder>{
    private Activity activity;
    private ArrayList<String> keys;

    public TrailerRecyclerAdapter(Activity activity, ArrayList<String> keys) {
        this.activity = activity;
        this.keys = keys;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrailerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder holder, int position) {
        Picasso.with(activity)
                .load(AppConstants.BASE_THUMB_PATH.replace("**", keys.get(holder.getAdapterPosition()).split("\\|")[0]))
                .noPlaceholder()
                .error(R.mipmap.ic_launcher)
                .into(holder.trailerThumb);

        holder.trailerName.setText(keys.get(holder.getAdapterPosition()).split("\\|")[1]);

        holder.trailerThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchTrailer(AppConstants.BASE_YOUTUBE_PATH + keys.get(holder.getAdapterPosition()).split("\\|")[0]);
            }
        });

        holder.shareTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTrailer(AppConstants.BASE_YOUTUBE_PATH + keys.get(holder.getAdapterPosition()).split("\\|")[0]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys == null ? 0 : keys.size();
    }

    private void watchTrailer(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri i_uri = Uri.parse(uri);
        intent.setData(i_uri);
        activity.startActivity(intent);
    }

    private void shareTrailer(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url + "\n\n --MadOverMovies :D");
        intent.setType("text/plain");
        activity.startActivity(Intent.createChooser(intent, "Share trailer..."));
    }
}
