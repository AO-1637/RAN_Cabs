package com.android.ran.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ran.R;
import com.android.ran.model.Cab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelectCabAdapter extends RecyclerView.Adapter<SelectCabAdapter.SelectCabHolder> {

    private Context context;
    private List<Cab> cabList;
    private ListItemClickListener mOnClickListener;
    private Cab travelDetails;

    public SelectCabAdapter(Context context, List<Cab> cabList, Cab travelDetails, ListItemClickListener listener) {
        this.context = context;
        this.cabList = cabList;
        this.travelDetails = travelDetails;
        mOnClickListener = listener;
    }

    public void refreshData(List<Cab> dataSet) {
        cabList.clear();
        cabList.addAll(dataSet);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectCabHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_select_cab, parent, false);
        return new SelectCabHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull SelectCabHolder holder, int position) {
        try {
            Calendar calendar = Calendar.getInstance();
            if (cabList.get(position).getStartTime() != null) {
                Date startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(cabList.get(position).getStartTime());
                calendar.setTime(startTime);
            } else {
                Date startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(travelDetails.getStartTime());
                calendar.setTime(startTime);
            }
            calendar.add(Calendar.HOUR, 5);
            calendar.add(Calendar.MINUTE, 30);
            holder.dateView.setText(new SimpleDateFormat("d MMM").format(calendar.getTime()));
            holder.timeView.setText(new SimpleDateFormat("hh:mm a").format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.carNameView.setText(cabList.get(position).getCarName());
        holder.seatsView.setText(cabList.get(position).getSeats());

        String displayFare = "₹ " + cabList.get(position).getFare();
        holder.fareView.setText(displayFare);

        int seats = Integer.parseInt(cabList.get(position).getSeats());
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.seatsView.getBackground();
        int magnitudeColor = getMagnitudeColor(seats);
        magnitudeCircle.setColor(magnitudeColor);
    }

    @Override
    public int getItemCount() {
        return cabList.size();
    }

    private int getMagnitudeColor(int magnitude) {
        int magnitudeColorResourceId;
        switch (magnitude) {
            case 1:
                magnitudeColorResourceId = R.color.seats1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.seats2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.seats3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.seats4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.seats5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.seats6;
                break;
            default:
                magnitudeColorResourceId = R.color.seats6plus;
                break;
        }
        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }


    public interface ListItemClickListener {
        void onListItemClick(Cab selectedCab, String pickup, String drop, String startTime);
    }

    class SelectCabHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView dateView, timeView, carNameView, seatsView, fareView;

        SelectCabHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.select_cab_date);
            timeView = itemView.findViewById(R.id.select_cab_time);
            carNameView = itemView.findViewById(R.id.select_cab_car_name);
            seatsView = itemView.findViewById(R.id.select_cab_seats);
            fareView = itemView.findViewById(R.id.select_cab_fare);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(cabList.get(clickedPosition), travelDetails.getPickup(),
                    travelDetails.getDrop(), travelDetails.getStartTime());
        }
    }
}