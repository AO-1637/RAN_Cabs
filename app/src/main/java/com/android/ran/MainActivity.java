package com.android.ran;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ran.model.User;
import com.android.ran.util.MaterialDialog;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences pref;
    private DrawerLayout rootView;
    private Toolbar actionBar;
    private NavigationView navigationDrawerView;
    private String name, email;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(actionBar);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        String json = pref.getString("user", "");
        User user = new Gson().fromJson(json, User.class);

        if (user != null) {
            name = user.getUsername();
            email = user.getEmail();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, rootView, actionBar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        rootView.addDrawerListener(toggle);
        toggle.syncState();

        View navHeader = navigationDrawerView.getHeaderView(0);
        TextView nameView = navHeader.findViewById(R.id.navigation_drawer_name);
        TextView emailView = navHeader.findViewById(R.id.navigation_drawer_email);
        emailView.setText(email);
        nameView.setText(name);

        navigationDrawerView.setNavigationItemSelectedListener(this);
        navigationDrawerView.getMenu().getItem(0).setChecked(true);
        displaySelectedScreen(R.id.navigation_drawer_book_ride);
    }

    @Override
    public void onBackPressed() {
        BookRideFragment currentFragment = (BookRideFragment) getSupportFragmentManager().findFragmentByTag("Book Ride");
        if (rootView.isDrawerOpen(GravityCompat.START)) {
            rootView.closeDrawer(GravityCompat.START);
        } else if (currentFragment != null && currentFragment.isVisible()) {
            count = count + 1;
            if (count == 1)
                Toast.makeText(MainActivity.this, "Press again to close RAN", Toast.LENGTH_SHORT).show();
            else if (count == 2)
                finish();
        } else {
            navigationDrawerView.getMenu().getItem(0).setChecked(true);
            displaySelectedScreen(R.id.navigation_drawer_book_ride);
        }
    }

    private void displaySelectedScreen(int itemId) {
        switch (itemId) {
            case R.id.navigation_drawer_book_ride:
                count = 0;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_content_frame, new BookRideFragment(), "Book Ride");
                ft.commit();
                break;

            case R.id.navigation_drawer_trips:
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.main_content_frame, new TripsFragment(), "Trips");
                ft2.commit();
                break;

            case R.id.navigation_drawer_profile:
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.main_content_frame, new ProfileFragment(), "Profile");
                ft3.commit();
                break;

            case R.id.navigation_drawer_faqs:
                FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                ft4.replace(R.id.main_content_frame, new FaqsFragment(), "FAQs");
                ft4.commit();
                break;

            case R.id.navigation_drawer_logout:
                showLogoutDialog();
                break;

            case R.id.navigation_drawer_about:
                FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                ft5.replace(R.id.main_content_frame, new AboutFragment(), "About");
                ft5.commit();
                break;

            case R.id.navigation_drawer_feedback:
                FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
                ft6.replace(R.id.main_content_frame, new FeedbackFragment(), "Feedback");
                ft6.commit();
                break;
        }
        rootView.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void showLogoutDialog() {
        new MaterialDialog.Builder().init(MainActivity.this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pref.edit().clear().apply();
                                Intent i = new Intent(MainActivity.this, SignInActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.
                                        FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                        })
                .setNegativeButton("Cancel")
                .createMaterialDialog()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

   /* @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (!notificationVisited) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                ref = database.getReference().child("Users").child(userId).child("Car Booked");
            }

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    notificationsCount = (int) dataSnapshot.getChildrenCount();
                    MenuItem menuItem = menu.findItem(R.id.action_notifications);
                    menuItem.setIcon(NotificationsIconConverter.convertLayoutToImage(MainActivity.this, notificationsCount, R.drawable.ic_notifications));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_notifications:
                this.startActivity(new Intent(this, NotificationsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        actionBar = findViewById(R.id.main_toolbar);
        rootView = findViewById(R.id.activity_main_layout);
        navigationDrawerView = findViewById(R.id.main_navigation_drawer);
    }
}
