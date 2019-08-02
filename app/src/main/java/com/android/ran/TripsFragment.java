package com.android.ran;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.ran.adapter.TripsAdapter;
import com.android.ran.model.Cab;
import com.android.ran.model.User;
import com.android.ran.network.APIUtils;
import com.android.ran.network.EndPointInterface;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class TripsFragment extends android.support.v4.app.Fragment {

    private static final String TAG = TripsFragment.class.getSimpleName();
    private Activity parentActivity;
    private View rootView;
    private SharedPreferences pref;
    private User user;
    private TripsAdapter tripsAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageView emptyView;
    private RelativeLayout rootLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentActivity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_trips, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity.setTitle("My Trips");
        initViews();

        pref = parentActivity.getSharedPreferences("AppPref", MODE_PRIVATE);
        String token = pref.getString("token", "");
        String json = pref.getString("user", "");
        user = new Gson().fromJson(json, User.class);

        generateArrayData(user.getCabsBooked());
        if (user.getCabsBooked().length == 0)
            progressBar.setVisibility(View.VISIBLE);

        EndPointInterface service = APIUtils.getAPIService();
        service.userDetail(user.get_id(), user.getEmail(), token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    if (user.getCabsBooked().length != response.body().getCabsBooked().length) {
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("user", new Gson().toJson(response.body()));
                        edit.apply();
                        tripsAdapter.refreshData(response.body().getCabsBooked());
                    } else if (response.body().getCabsBooked().length == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else if (response.body().getCabsBooked().length > 0) {
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG + "On Failure", t.getMessage());
                progressBar.setVisibility(View.GONE);
                if (tripsAdapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    Snackbar.make(rootLayout, "Something went wrong. Please try again later!", Snackbar.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Couldn't refresh trips", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateArrayData(Cab[] cabsBooked) {
        tripsAdapter = new TripsAdapter(cabsBooked);
        recyclerView.setAdapter(tripsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
    }

    private void initViews() {
        rootLayout = rootView.findViewById(R.id.fragment_trips_layout);
        recyclerView = rootView.findViewById(R.id.trips_recycler_view);
        emptyView = rootView.findViewById(R.id.trips_empty_view);
        progressBar = rootView.findViewById(R.id.trips_progress_bar);
    }
}