package mynanodegree.com.madovermovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import mynanodegree.com.madovermovies.R;

/**
 * Created by Arjun on 4/19/16 for MadOverMovies.
 */
public class TrailerViewHolder extends RecyclerView.ViewHolder {

    ImageView trailerThumb;
    TextView trailerName;
    ImageButton shareTrailer;

    public TrailerViewHolder(View convertView) {
        super(convertView);

        trailerThumb = (ImageView) convertView.findViewById(R.id.imageThumb);
        trailerName = (TextView) convertView.findViewById(R.id.trailerName);
        shareTrailer = (ImageButton) convertView.findViewById(R.id.buttonShare);
    }
}
