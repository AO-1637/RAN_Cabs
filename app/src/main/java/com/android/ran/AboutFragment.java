package com.android.ran;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.Button;

public class AboutFragment extends Fragment {

    private Activity parentActivity;
    private Button tcView, ppView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentActivity = getActivity();
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity.setTitle("About");
        setHasOptionsMenu(true);
        initVariables();

        tcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(parentActivity)
                        .setTitle("Terms & Conditions")
                        .setMessage(R.string.content_about)
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
            }
        });

        ppView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(parentActivity)
                        .setTitle("Privacy Policy")
                        .setMessage(R.string.content_about)
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_about, menu);
        if (menu != null) {
            menu.removeItem(R.id.action_notifications);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_app: {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "The Journey you'll remember.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initVariables() {
        tcView = parentActivity.findViewById(R.id.button_terms_and_conditions);
        ppView = parentActivity.findViewById(R.id.button_privacy_policy);
    }
}