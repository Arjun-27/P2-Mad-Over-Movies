package mynanodegree.com.madovermovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mynanodegree.com.madovermovies.R;

/**
 * Created by Arjun on 4/19/16 for MadOverMovies.
 */
public class ReviewViewHolder extends RecyclerView.ViewHolder {

    TextView tvAuthor, tvReview;

    public ReviewViewHolder(View convertView) {
        super(convertView);
        tvAuthor = (TextView) convertView.findViewById(R.id.textAuthor);
        tvReview = (TextView) convertView.findViewById(R.id.textReview);
    }
}
