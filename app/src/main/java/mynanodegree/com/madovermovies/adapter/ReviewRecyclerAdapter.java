package mynanodegree.com.madovermovies.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mynanodegree.com.madovermovies.R;

/**
 * Created by Arjun on 4/19/16 for MadOverMovies.
 */
public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private Activity activity;
    private ArrayList<String> reviewList;

    public ReviewRecyclerAdapter(Activity activity, ArrayList<String> reviewList) {
        this.activity = activity;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String rdata[] = reviewList.get(position).split("\\|");

        holder.tvAuthor.setText(rdata[1]);
        holder.tvReview.setText(rdata[2]);
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }
}
