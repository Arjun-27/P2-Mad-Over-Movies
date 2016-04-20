package mynanodegree.com.madovermovies.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mynanodegree.com.madovermovies.R;
import mynanodegree.com.madovermovies.adapter.TrailerRecyclerAdapter;

/**
 * Created by Arjun on 4/19/16 for MadOverMovies.
 */
public class TrailersFragment extends Fragment {
    private RecyclerView trailerList;
    private ArrayList<String> keys;

    public TrailersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keys = getArguments().getStringArrayList("keys");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_trailers, container, false);

        trailerList = (RecyclerView) rootView.findViewById(R.id.listTrailers);
        trailerList.setHasFixedSize(true);
        trailerList.setLayoutManager(new LinearLayoutManager(getActivity()));

        trailerList.setAdapter(new TrailerRecyclerAdapter(getActivity(), keys));

        return rootView;
    }


}
