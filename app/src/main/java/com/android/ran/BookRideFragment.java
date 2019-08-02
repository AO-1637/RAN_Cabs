package com.android.ran;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.ran.model.Cab;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class BookRideFragment extends android.support.v4.app.Fragment {

    private Activity parentActivity;
    private Calendar DateCalendar, TimeCalendar, now;
    private InputMethodManager imm;
    private AutoCompleteTextView collegeNameView, pickupView, dropView;
    private EditText dateView, timeView, seatsView;
    private ImageButton swapLocationView;
    private Button increaseSeatView, decreaseSeatView, selectCabView;
    private String selectedCollege, isoDate, isoTime;
    private boolean route = true;
    private long thirtyDays = 2592000000L;
    private int selectedDate;
    private int selectedSeats = 4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentActivity = getActivity();
        return inflater.inflate(R.layout.fragment_book_ride, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity.setTitle("RAN");
        initVariables();

        setupCollegeSpinner();
        collegeNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pickupView.setText("");
                dropView.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                selectedCollege = collegeNameView.getText().toString();
                selectLocation(selectedCollege, route);
            }
        });

        pickupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                imm.hideSoftInputFromWindow(pickupView.getWindowToken(), 0);
                if (TextUtils.isEmpty(selectedCollege)) {
                    collegeNameView.requestFocus();
                    collegeNameView.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                }
                return true;
            }
        });

        dropView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                imm.hideSoftInputFromWindow(dropView.getWindowToken(), 0);
                if (TextUtils.isEmpty(selectedCollege)) {
                    collegeNameView.requestFocus();
                    collegeNameView.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                }
                return true;
            }
        });

        swapLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(selectedCollege)) {
                    collegeNameView.requestFocus();
                    collegeNameView.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    imm.hideSoftInputFromWindow(collegeNameView.getWindowToken(), 0);
                } else if ((TextUtils.isEmpty(pickupView.getText().toString())) && (TextUtils.isEmpty(dropView.getText().toString()))) {
                    pickupView.requestFocus();
                    pickupView.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    imm.hideSoftInputFromWindow(pickupView.getWindowToken(), 0);
                } else {
                    route = !route;
                    Editable location = pickupView.getText();
                    pickupView.setText(dropView.getText());
                    dropView.setText(location);
                    selectLocation(selectedCollege, route);
                }
            }
        });

        setupDateTimePicker();
        String numberAsString = "" + selectedSeats;
        seatsView.setText(numberAsString);

        increaseSeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSeats != 14) {
                    selectedSeats++;
                    seatsView.setText(String.valueOf(selectedSeats));
                }
            }
        });

        decreaseSeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSeats != 1) {
                    selectedSeats--;
                    seatsView.setText(String.valueOf(selectedSeats));
                }
            }
        });

        selectCabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCab();
            }
        });
    }

    private void selectLocation(String collegeSelected, boolean routeSelected) {
        boolean isCollegePresent = false;
        String availableColleges[] = getResources().getStringArray(R.array.colleges);
        for (String college : availableColleges) {
            if (collegeSelected.equals(college)) {
                isCollegePresent = true;
            }
        }
        if (isCollegePresent) {
            setupPickupLocationSpinner(collegeSelected, routeSelected);
            setupDropLocationSpinner(collegeSelected, routeSelected);
        } else {
            pickupView.setKeyListener(null);
            dropView.setKeyListener(null);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupCollegeSpinner() {
        ArrayAdapter collegeSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                R.array.colleges, R.layout.support_simple_spinner_dropdown_item);
        collegeNameView.setAdapter(collegeSpinnerAdapter);
        collegeNameView.setKeyListener(null);
        collegeNameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((AutoCompleteTextView) view).showDropDown();
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPickupLocationSpinner(String collegeSelected, boolean routeSelected) {
        ArrayAdapter pickupSpinnerAdapter;
        if (routeSelected) {
            if (collegeSelected.equals(getResources().getStringArray(R.array.colleges)[0])) {
                pickupSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_KGP_options_A, R.layout.support_simple_spinner_dropdown_item);
            } else {
                pickupSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_VIT_options_A, R.layout.support_simple_spinner_dropdown_item);
            }
        } else {
            if (collegeSelected.equals(getResources().getStringArray(R.array.colleges)[0])) {
                pickupSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_KGP_options_B, R.layout.support_simple_spinner_dropdown_item);
            } else {
                pickupSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_VIT_options_B, R.layout.support_simple_spinner_dropdown_item);
            }
        }
        pickupView.setAdapter(pickupSpinnerAdapter);
        pickupView.setKeyListener(null);
        pickupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDropLocationSpinner(String collegeSelected, boolean routeSelected) {
        ArrayAdapter dropSpinnerAdapter;
        if (routeSelected) {
            if (collegeSelected.equals(getResources().getStringArray(R.array.colleges)[0])) {
                dropSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_KGP_options_B, R.layout.support_simple_spinner_dropdown_item);
            } else {
                dropSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_VIT_options_B, R.layout.support_simple_spinner_dropdown_item);
            }
        } else {
            if (collegeSelected.equals(getResources().getStringArray(R.array.colleges)[0])) {
                dropSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_KGP_options_A, R.layout.support_simple_spinner_dropdown_item);
            } else {
                dropSpinnerAdapter = ArrayAdapter.createFromResource(parentActivity,
                        R.array.array_VIT_options_A, R.layout.support_simple_spinner_dropdown_item);
            }
        }
        dropView.setAdapter(dropSpinnerAdapter);
        dropView.setKeyListener(null);
        dropView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((AutoCompleteTextView) view).showDropDown();
                return false;
            }
        });
    }

    private void setupDateTimePicker() {
        now = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                DateCalendar.set(Calendar.YEAR, year);
                DateCalendar.set(Calendar.MONTH, monthOfYear);
                DateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (now.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                    if (TextUtils.isEmpty(timeView.getText().toString())) {
                        selectedDate = DateCalendar.get(Calendar.DAY_OF_MONTH);
                        formatDate();
                    } else {
                        if (TimeCalendar.getTimeInMillis() >= System.currentTimeMillis() - 60000) {
                            selectedDate = DateCalendar.get(Calendar.DAY_OF_MONTH);
                            formatDate();
                        } else
                            Toast.makeText(getContext(), "Don't look back you're not going that way!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedDate = DateCalendar.get(Calendar.DAY_OF_MONTH);
                    formatDate();
                }
            }
        };

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(dateView.getWindowToken(), 0);
                DateCalendar = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(parentActivity, date, DateCalendar
                        .get(Calendar.YEAR), DateCalendar.get(Calendar.MONTH), DateCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis() + thirtyDays);
                mDatePicker.show();
            }
        });

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                TimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                TimeCalendar.set(Calendar.MINUTE, minute);
                if (TimeCalendar.get(Calendar.DAY_OF_MONTH) == selectedDate) {
                    if (TimeCalendar.getTimeInMillis() >= System.currentTimeMillis() - 60000)
                        formatTime();
                    else
                        Toast.makeText(getContext(), "Don't look back you're not going that way!", Toast.LENGTH_LONG).show();
                } else {
                    if (TextUtils.isEmpty(dateView.getText().toString()))
                        Toast.makeText(getContext(), "Hey! You missed selecting the date.", Toast.LENGTH_LONG).show();
                    else
                        formatTime();
                }
            }
        };

        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(timeView.getWindowToken(), 0);
                TimeCalendar = Calendar.getInstance();
                TimePickerDialog mTimePicker = new TimePickerDialog(parentActivity, time, TimeCalendar
                        .get(Calendar.HOUR_OF_DAY), TimeCalendar.get(Calendar.MINUTE), false);
                mTimePicker.show();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void formatDate() {
        String displayFormat = new SimpleDateFormat("EEE, MMM d", Locale.US).format(DateCalendar.getTime());
        dateView.setText(displayFormat);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        isoDate = df.format(DateCalendar.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    private void formatTime() {
        String displayFormat = new SimpleDateFormat("hh:mm a", Locale.US).format(TimeCalendar.getTime());
        timeView.setText(displayFormat);

        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        isoTime = df.format(TimeCalendar.getTime());
    }

    private void selectCab() {
        String collegeName = collegeNameView.getText().toString();
        String pickup = pickupView.getText().toString();
        String drop = dropView.getText().toString();
        String date = dateView.getText().toString();
        String time = timeView.getText().toString();
        String startTime = isoDate + 'T' + isoTime + 'Z';
        String seats = seatsView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(collegeName)) {
            focusView = collegeNameView;
            cancel = true;
        } else if (TextUtils.isEmpty(pickup)) {
            focusView = pickupView;
            cancel = true;
        } else if (TextUtils.isEmpty(drop)) {
            focusView = dropView;
            cancel = true;
        } else if (TextUtils.isEmpty(date)) {
            dateView.setFocusableInTouchMode(true);
            focusView = dateView;
            cancel = true;
        } else if (TextUtils.isEmpty(time)) {
            timeView.setFocusableInTouchMode(true);
            focusView = timeView;
            cancel = true;
        } else if (TextUtils.isEmpty(seats)) {
            focusView = seatsView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            focusView.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        } else {
            Cab travelDetails = new Cab(collegeName, pickup, drop, startTime, "", seats,
                    "", "", "", "");
            Intent intent = new Intent(parentActivity, SelectCabActivity.class);
            intent.putExtra("travel_details", travelDetails);
            startActivity(intent);
        }
    }

    private void initVariables() {
        imm = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        collegeNameView = parentActivity.findViewById(R.id.book_ride_college);
        pickupView = parentActivity.findViewById(R.id.book_ride_pickup);
        dropView = parentActivity.findViewById(R.id.book_ride_drop);
        swapLocationView = parentActivity.findViewById(R.id.book_ride_swap_location);
        dateView = parentActivity.findViewById(R.id.book_ride_date);
        timeView = parentActivity.findViewById(R.id.book_ride_time);
        seatsView = parentActivity.findViewById(R.id.book_ride_seats);
        increaseSeatView = parentActivity.findViewById(R.id.book_ride_increase_seat);
        decreaseSeatView = parentActivity.findViewById(R.id.book_ride_decrease_seat);
        selectCabView = parentActivity.findViewById(R.id.button_select_cab);
    }
}