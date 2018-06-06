package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SportAdapter sportAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.f_search_recyclerview);

        sportAdapter = new SportAdapter(getContext());
        recyclerView.setAdapter(sportAdapter);

        GridLayoutManager gl = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gl);

        return v;
    }

}
