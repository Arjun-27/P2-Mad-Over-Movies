package mynanodegree.com.madovermovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mynanodegree.com.madovermovies.R;
import mynanodegree.com.madovermovies.adapter.ReviewRecyclerAdapter;

/**
 * Created by Arjun on 4/19/16 for MadOverMovies.
 */
public class ReviewsFragment extends Fragment {
    private RecyclerView reviewList;
    private ArrayList<String> reviews;

    public ReviewsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviews = getArguments().getStringArrayList("reviews");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_reviews, container, false);

        reviewList = (RecyclerView) rootView.findViewById(R.id.reviewsList);
        reviewList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        reviewList.setLayoutManager(manager);

        reviewList.setAdapter(new ReviewRecyclerAdapter(getActivity(), reviews));
        return rootView;
    }


}
