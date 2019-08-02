package com.android.ran;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.ran.adapter.NotificationsCardViewHolder;
import com.android.ran.model.Cab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotificationsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseRecyclerAdapter adapter;
    private ImageView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_notifications);
        initViews();

        GridLayoutManager manager = new GridLayoutManager(NotificationsActivity.this, 1);
        RecyclerView notificationRecyclerView = findViewById(R.id.notifications_recycler_view);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(userId).child("Car Booked");

        if (query == null) {
            emptyView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        FirebaseRecyclerOptions<Cab> options =
                new FirebaseRecyclerOptions.Builder<Cab>()
                        .setQuery(query, Cab.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Cab, NotificationsCardViewHolder>(options) {

            @NonNull
            @Override
            public NotificationsCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notifications, parent, false);
                return new NotificationsCardViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NotificationsCardViewHolder holder, int position, @NonNull Cab model) {
                holder.bindCarBooked(model);
                progressBar.setVisibility(View.GONE);
            }
        };

        notificationRecyclerView.setAdapter(adapter);
        notificationRecyclerView.setLayoutManager(manager);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        progressBar = findViewById(R.id.notifications_progress_bar);
        emptyView = findViewById(R.id.notifications_empty_view);
    }
}