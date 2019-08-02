package com.android.ran;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ran.adapter.SelectCabAdapter;
import com.android.ran.model.Cab;
import com.android.ran.model.User;
import com.android.ran.network.APIUtils;
import com.android.ran.network.EndPointInterface;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCabActivity extends AppCompatActivity implements SelectCabAdapter.ListItemClickListener {

    private static final String TAG = SelectCabActivity.class.getSimpleName();
    private View actionBarView;
    private String token;
    private User user;
    private Cab travelDetails;
    private SelectCabAdapter selectCabAdapter;
    private ImageButton closeView;
    private TextView pickupView, dropView;
    private boolean refreshActivated = false;
    private ShimmerFrameLayout shimmerView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_select_cab);
        initViews();

        SharedPreferences pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        token = pref.getString("token", "");
        String json = pref.getString("user", "");
        user = new Gson().fromJson(json, User.class);

        Intent intent = getIntent();
        travelDetails = (Cab) intent.getSerializableExtra("travel_details");

        if (isConnectedToInternet())
            getAvailableCabList();
        else
            setContentView(R.layout.layout_internet_connectivity);

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (travelDetails.getCollegeName().equals(getResources().getStringArray(R.array.colleges)[0])) {
            String[] kgpList = getResources().getStringArray(R.array.array_KGP_options_A);
            setLocations(kgpList);
        } else {
            String[] vitList = getResources().getStringArray(R.array.array_VIT_options_A);
            setLocations(vitList);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullAndRefresh();
            }
        });
    }

    @Override
    public void onResume() {
        shimmerView.startShimmerAnimation();
        super.onResume();
    }

    @Override
    protected void onPause() {
        shimmerView.stopShimmerAnimation();
        super.onPause();
    }

    private void getAvailableCabList() {
        EndPointInterface service = APIUtils.getAPIService();
        Call<List<Cab>> call = service.availableCabList(
                user.getEmail(), token, travelDetails.getCollegeName(), travelDetails.getPickup(),
                travelDetails.getDrop(), travelDetails.getSeats(), travelDetails.getStartTime());

        call.enqueue(new Callback<List<Cab>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cab>> call, @NonNull Response<List<Cab>> response) {
                if (!refreshActivated) {
                    shimmerView.stopShimmerAnimation();
                    shimmerView.setVisibility(View.GONE);
                    if (response.body() != null)
                        generateDataList(response.body(), travelDetails);
                    refreshActivated = true;
                } else {
                    if (response.body() != null) {
                        selectCabAdapter.refreshData(response.body());
                        if (selectCabAdapter.getItemCount() == 0)
                            emptyView.setVisibility(View.VISIBLE);
                        else
                            emptyView.setVisibility(View.INVISIBLE);
                        refreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Cab>> call, @NonNull Throwable t) {
                Log.e(TAG + "On Failure", t.getMessage());
                if (!refreshActivated) {
                    shimmerView.stopShimmerAnimation();
                    shimmerView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Couldn't refresh list", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void generateDataList(List<Cab> cabList, Cab travelDetails) {
        selectCabAdapter = new SelectCabAdapter(this, cabList, travelDetails, this);
        recyclerView.setAdapter(selectCabAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SelectCabActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        if (layoutManager.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onListItemClick(Cab selectedCab, String pickup, String drop, String startTime) {
        BottomSheetDialogFragment confirmRide = new ConfirmRideFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_cab", selectedCab);
        bundle.putString("pickup", pickup);
        bundle.putString("drop", drop);
        bundle.putString("startTime", startTime);
        confirmRide.setArguments(bundle);
        confirmRide.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
    }

    private void setLocations(String[] list) {
        for (String aList : list) {
            if (travelDetails.getPickup().equals(aList)) {
                pickupView.setText(travelDetails.getCollegeName());
                dropView.setText(travelDetails.getDrop());
            } else if (travelDetails.getDrop().equals(aList)) {
                pickupView.setText(travelDetails.getPickup());
                dropView.setText(travelDetails.getCollegeName());
            }
        }
    }

    private void pullAndRefresh() {
        refreshLayout.setRefreshing(true);
        if (isConnectedToInternet())
            getAvailableCabList();
        else
            setContentView(R.layout.layout_internet_connectivity);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @SuppressLint("InflateParams")
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = inflater != null ? inflater.inflate(R.layout.layout_select_cab_action_bar, null) : null;
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(actionBarView);
        }
    }

    private void initViews() {
        closeView = actionBarView.findViewById(R.id.select_cab_close);
        pickupView = actionBarView.findViewById(R.id.select_cab_pickup);
        dropView = actionBarView.findViewById(R.id.select_cab_drop);
        shimmerView = findViewById(R.id.select_cab_shimmer_layout);
        refreshLayout = findViewById(R.id.select_cab_refresh_layout);
        recyclerView = findViewById(R.id.select_cab_recycler_view);
        emptyView = findViewById(R.id.select_cab_empty_layout);
    }
}