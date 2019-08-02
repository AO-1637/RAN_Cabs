package com.android.ran.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.ran.R;
import com.android.ran.model.Cab;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView timeView, contentView;

    public NotificationsCardViewHolder(View itemView) {
        super(itemView);

        timeView = itemView.findViewById(R.id.notifications_time);
        contentView = itemView.findViewById(R.id.notifications_content);

        Context mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    public void bindCarBooked(Cab cabBooked) {
        timeView.setText(cabBooked.getStartTime());

        String notificationContent;
        String userName = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            userName = user.getDisplayName();
        }
        assert userName != null;
        String name[] = userName.split(" ");

        notificationContent = name[0] + ", we have received your payment. Your ride details will be available in the 'My Trips' section.";
        contentView.setText(notificationContent);
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Cab> cabBookedList = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Car Booked");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cabBookedList.add(snapshot.getValue(Cab.class));
                }
                int itemPosition = getLayoutPosition();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
