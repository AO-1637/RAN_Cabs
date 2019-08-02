package com.android.ran;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ran.model.Cab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConfirmRideFragment extends BottomSheetDialogFragment {

    private Activity parentActivity;
    private View rootView;
    private TextView pickupView, dropView, startTimeView, carNameView, seatsView, fareView, taxesView, totalCostView;
    private LinearLayout confirmRideView;
    private Cab selectedCab;
    private String carName, pickup, drop, startTime, seats, fare;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentActivity = getActivity();

        assert getArguments() != null;
        selectedCab = (Cab) getArguments().getSerializable("selected_cab");
        pickup = getArguments().getString("pickup");
        drop = getArguments().getString("drop");
        carName = selectedCab.getCarName();
        seats = selectedCab.getSeats();
        fare = selectedCab.getFare();

        rootView = inflater.inflate(R.layout.fragment_confirm_ride, container, false);
        return rootView;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();

        try {
            Calendar calendar = Calendar.getInstance();
            if (selectedCab.getStartTime() != null) {
                startTime = selectedCab.getStartTime();
                Date displayTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(startTime);
                calendar.setTime(displayTime);
            } else {
                assert getArguments() != null;
                startTime = getArguments().getString("startTime");
                Date displayTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(startTime);
                calendar.setTime(displayTime);
            }
            calendar.add(Calendar.HOUR, 5);
            calendar.add(Calendar.MINUTE, 30);
            startTimeView.setText(new SimpleDateFormat("EEE, MMM d, hh:mm a").format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (selectedCab.getCollegeName().equals(getResources().getStringArray(R.array.colleges)[0])) {
            String[] kgpList = getResources().getStringArray(R.array.array_KGP_options_A);
            setLocations(kgpList);
        } else {
            String[] vitList = getResources().getStringArray(R.array.array_VIT_options_A);
            setLocations(vitList);
        }

        carNameView.setText(carName);
        seatsView.setText(seats);

        float tripFare = (float) Integer.parseInt(fare);
        String displayFare = "₹ " + Float.toString(tripFare);
        fareView.setText(displayFare);

        float tax = (float) 0.12 * Integer.parseInt(fare); // to be calculated later according to Indian Government
        String displayTaxes = "₹ " + Float.toString(tax);
        taxesView.setText(displayTaxes);

        float totalCost = Integer.parseInt(fare) + tax;
        String displayTotalCost = "₹ " + Float.toString(totalCost);
        totalCostView.setText(displayTotalCost);

        confirmRideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parentActivity, PaymentActivity.class);
                intent.putExtra("confirmed_ride_details", selectedCab);
                intent.putExtra("pickup", pickupView.getText().toString());
                intent.putExtra("drop", dropView.getText().toString());
                intent.putExtra("startTime", startTime);
                parentActivity.finish();
                parentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setLocations(String[] list) {
        for (String aList : list) {
            if (pickup.equals(aList)) {
                String displayPickup = pickup + ", " + selectedCab.getCollegeName();
                pickupView.setText(displayPickup);
                dropView.setText(drop);
            } else if (drop.equals(aList)) {
                String displayDrop = drop + ", " + selectedCab.getCollegeName();
                pickupView.setText(pickup);
                dropView.setText(displayDrop);
            }
        }
    }

    private void initViews() {
        pickupView = rootView.findViewById(R.id.confirm_ride_pickup);
        dropView = rootView.findViewById(R.id.confirm_ride_drop);
        startTimeView = rootView.findViewById(R.id.confirm_ride_start_time);
        carNameView = rootView.findViewById(R.id.confirm_ride_car_name);
        seatsView = rootView.findViewById(R.id.confirm_ride_seats);
        fareView = rootView.findViewById(R.id.confirm_ride_fare);
        taxesView = rootView.findViewById(R.id.confirm_ride_taxes);
        totalCostView = rootView.findViewById(R.id.confirm_ride_total_cost);
        confirmRideView = rootView.findViewById(R.id.button_confirm_ride);
    }
}