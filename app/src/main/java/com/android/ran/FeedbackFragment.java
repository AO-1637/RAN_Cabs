package com.android.ran;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

public class FeedbackFragment extends Fragment {

    private View rootView;
    private Activity parentActivity;
    /*private User sharedData = User.getInstance();*/
    private EditText feedbackText;
    private Spinner feedbackSpinner;
    private String feedbackSpinnerStr;
    private TextView submitAnotherFeedbackButton;
    private RelativeLayout submitFeedback;
    private LinearLayout submitAnotherFeedback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentActivity = getActivity();
        rootView = inflater.inflate(com.android.ran.R.layout.fragment_feedback, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity.setTitle("Feedback");
        setHasOptionsMenu(true);
        initViews();

        feedbackText.clearFocus();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parentActivity,
                com.android.ran.R.array.feedback_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackSpinner.setAdapter(adapter);
        feedbackSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                feedbackSpinnerStr = adapterView.getAdapter().getItem(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitAnotherFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFeedback.setVisibility(View.VISIBLE);
                submitAnotherFeedback.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_feedback, menu);
        if (menu != null) {
            menu.removeItem(R.id.main_notifications);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feedback_send:
                submitFeedback();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitFeedback() {
        try {
            sendFeedback();
        } catch (NetworkErrorException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Network error, please try again later", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Format error, please try again later", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendFeedback() throws Exception {
        String feedbackStr = feedbackText.getText().toString();
        if (feedbackStr.isEmpty()) {
            throw new Exception("No feedback was written");
        } else {
            sendFeedbackAsEmail(feedbackStr);
        }
    }

    /*
    private void trySendToServer(String feedback) throws JSONException {
        //String path = UserContract.serverIP + "feedback/" + feedbackSpinnerStr + "/" + "id/" + sharedData.getUserName();
        JSONObject reqJson = new JSONObject();
        try {
            reqJson.put("feedback", feedback);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new JSONException("Format json error");
        }

        JSONObject res = null;
        try {
            //res = new PutExecutor().execute(path, reqJson.toString()).get();

            if (res == null) {
                throw new NetworkErrorException("no response from server");
            } else {
                if (res.getInt("code") == 200 && res.getString("status").equals("success")) // logged in successfully
                {
                    res.getString("message");
                    Toast.makeText(getContext(), "Feedback was sent, Thank you", Toast.LENGTH_LONG).show();
                } else {
                    String message = res.getString("message");
                    System.out.println("Failed to send following feedback to server " + feedback +
                            "" + "received following message: " + message);
                    throw new NetworkErrorException("received following message: " + message);
                }
            }
        } catch (NetworkErrorException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Network error, please try again later", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    private void sendFeedbackAsEmail(String message) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String username = "";
        if (user != null)
            username = user.getDisplayName();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, feedbackSpinnerStr + " Report By Mr." + username);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"help@ran.in"});
        if (intent.resolveActivity(parentActivity.getPackageManager()) != null) {
            startActivity(intent);
            feedbackText.setText("");
            submitFeedback.setVisibility(View.GONE);
            submitAnotherFeedback.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        feedbackSpinner = rootView.findViewById(R.id.feedback_spinner);
        feedbackText = rootView.findViewById(R.id.feedback_text);
        submitFeedback = rootView.findViewById(R.id.submit_feedback);
        submitAnotherFeedback = rootView.findViewById(R.id.submit_another_feedback);
        submitAnotherFeedbackButton = rootView.findViewById(R.id.button_submit_another_feedback);
    }
}