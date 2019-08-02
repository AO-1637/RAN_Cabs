package com.android.ran;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ran.model.User;
import com.android.ran.network.APIUtils;
import com.android.ran.network.EndPointInterface;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private Activity parentActivity;
    private View rootView;
    private Menu mOptionsMenu;
    private ViewGroup container;
    private InputMethodManager imm;
    private SharedPreferences pref;
    private String token;
    private User user;
    private TextView usernameView, emailView, contactView, alternateContactView, passwordView;
    private TextView profileEmailView, profileContactView, profileAlternateContactView, profilePasswordView;
    private ConnectivityManager connMgr;
    private LinearLayout rootLayout;
    private AlertDialog dialog;
    private EditText editUsernameView, editEmailView, editContactView, editAlternateContactView,
            oldPasswordView, newPasswordView, confirmNewPasswordView;
    private String countryCode = "+91 ";
    private TextInputLayout newPasswordLayout, confirmNewPasswordLayout;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentActivity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity.setTitle("My Profile");
        setHasOptionsMenu(true);
        initViews();

        container = (ViewGroup) rootView.getParent();
        connMgr = (ConnectivityManager) parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        imm = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        pref = this.parentActivity.getSharedPreferences("AppPref", MODE_PRIVATE);
        String json = pref.getString("user", "");
        token = pref.getString("token", "");
        user = new Gson().fromJson(json, User.class);

        usernameView.setText(user.getUsername());
        emailView.setText(user.getEmail());
        contactView.setText(user.getContact());
        alternateContactView.setText(user.getAlternateContact());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mOptionsMenu = menu;
        inflater.inflate(R.menu.menu_profile, mOptionsMenu);
        mOptionsMenu.findItem(R.id.profile_save).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_edit:
                activateEditProfile();
                return true;
            case R.id.profile_save:
                saveEditedProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void activateEditProfile() {
        if (isConnectedToInternet()) {
            usernameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_profile_black, 0);
            emailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_profile_black, 0);
            contactView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_profile_black, 0);
            alternateContactView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_profile_black, 0);
            passwordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_profile_black, 0);
            profileEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profileContactView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profileAlternateContactView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profilePasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mOptionsMenu.findItem(R.id.profile_edit).setVisible(false);
            mOptionsMenu.findItem(R.id.profile_save).setVisible(true);
            usernameView.setEnabled(true);
            emailView.setEnabled(true);
            contactView.setEnabled(true);
            alternateContactView.setEnabled(true);
            passwordView.setEnabled(true);

            usernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View customView = getLayoutInflater().inflate(R.layout.layout_edit_profile_username, container, false);
                    editUsernameView = customView.findViewById(R.id.edit_profile_name);
                    editUsernameView.setText(usernameView.getText());

                    dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Edit Name")
                            .setView(customView)
                            .setPositiveButton("Submit", null)
                            .setNegativeButton("Cancel", null)
                            .create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface di) {
                            dialog.getButton(di.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String newName = editUsernameView.getText().toString();
                                    usernameView.setText(newName);
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    dialog.show();
                    positiveButton(false);

                    editUsernameView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (TextUtils.isEmpty(s.toString()) || s.toString().equals(user.getUsername())
                                    || !s.toString().matches(".*[a-zA-Z]+.*")) {
                                positiveButton(false);
                            } else {
                                positiveButton(true);
                            }
                        }
                    });
                    setFocus(editUsernameView);
                }
            });

            emailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View customView = getLayoutInflater().inflate(R.layout.layout_edit_profile_email, container, false);
                    editEmailView = customView.findViewById(R.id.edit_profile_email);
                    editEmailView.setText(emailView.getText());

                    dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Edit Email")
                            .setView(customView)
                            .setPositiveButton("Submit", null)
                            .setNegativeButton("Cancel", null)
                            .create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface di) {
                            dialog.getButton(di.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String newEmail = editEmailView.getText().toString();
                                    emailView.setText(newEmail);
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    dialog.show();
                    positiveButton(false);

                    editEmailView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (TextUtils.isEmpty(s.toString()) || s.toString().equals(user.getEmail())
                                    || !isEmailValid(s.toString()))
                                positiveButton(false);
                            else
                                positiveButton(true);

                        }
                    });
                    setFocus(editEmailView);
                }
            });

            contactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View customView = getLayoutInflater().inflate(R.layout.layout_edit_profile_contact, container, false);
                    editContactView = customView.findViewById(R.id.edit_profile_mobile_number);
                    editContactView.setText(contactView.getText());

                    dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Edit Mobile Number")
                            .setView(customView)
                            .setPositiveButton("Submit", null)
                            .setNegativeButton("Cancel", null)
                            .create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface di) {
                            dialog.getButton(di.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String newContact = editContactView.getText().toString();
                                    contactView.setText(newContact);
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    dialog.show();
                    positiveButton(false);

                    editContactView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!s.toString().startsWith("+91 ")) {
                                editContactView.setText(countryCode);
                                Selection.setSelection(editContactView.getText(), editContactView.getText().length());
                            } else if (s.toString().equals(user.getContact())
                                    || s.toString().equals(alternateContactView.getText().toString())
                                    || s.toString().length() > 4 && s.toString().length() < 14) {
                                positiveButton(false);
                            } else {
                                positiveButton(true);
                            }
                        }
                    });
                    setFocus(editContactView);
                }
            });

            alternateContactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View customView = getLayoutInflater().inflate(R.layout.layout_edit_profile_alternate_contact, container, false);
                    editAlternateContactView = customView.findViewById(R.id.edit_profile_alternate_number);
                    editAlternateContactView.setText(alternateContactView.getText());

                    dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Edit Alternate Number")
                            .setView(customView)
                            .setPositiveButton("Submit", null)
                            .setNegativeButton("Cancel", null)
                            .create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface di) {
                            dialog.getButton(di.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String newAlternateContact = editAlternateContactView.getText().toString();
                                    alternateContactView.setText(newAlternateContact);
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    dialog.show();
                    positiveButton(false);

                    editAlternateContactView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!s.toString().startsWith("+91 ")) {
                                editAlternateContactView.setText(countryCode);
                                Selection.setSelection(editAlternateContactView.getText(), editAlternateContactView.getText().length());
                            } else if (s.toString().equals(user.getAlternateContact())
                                    || s.toString().equals(contactView.getText().toString())
                                    || s.toString().length() > 4 && s.toString().length() < 14) {
                                positiveButton(false);
                            } else {
                                positiveButton(true);
                            }
                        }
                    });
                    setFocus(editAlternateContactView);
                }
            });

            passwordView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View customView = getLayoutInflater().inflate(R.layout.layout_edit_profile_password, container, false);
                    oldPasswordView = customView.findViewById(R.id.edit_profile_old_password);
                    newPasswordView = customView.findViewById(R.id.edit_profile_new_password);
                    confirmNewPasswordView = customView.findViewById(R.id.edit_profile_confirm_new_password);
                    newPasswordLayout = customView.findViewById(R.id.edit_profile_new_password_layout);
                    confirmNewPasswordLayout = customView.findViewById(R.id.edit_profile_confirm_new_password_layout);

                    dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Change Password")
                            .setView(customView)
                            .setPositiveButton("Submit", null)
                            .setNegativeButton("Cancel", null)
                            .create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface di) {
                            dialog.getButton(di.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String newPassword = newPasswordView.getText().toString();
                                    String confirmNewPassword = confirmNewPasswordView.getText().toString();
                                    if (!confirmNewPassword.equals(newPassword)) {
                                        confirmNewPasswordLayout.setError("Passwords do not match.");
                                        confirmNewPasswordView.requestFocus();
                                    } else {
                                        passwordView.setText(newPassword);
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                    positiveButton(false);

                    oldPasswordView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.toString().length() >= 6 && newPasswordView.getText().toString().length() >= 6
                                    && confirmNewPasswordView.getText().toString().length() >= 6) {
                                positiveButton(true);
                            } else {
                                positiveButton(false);
                            }
                        }
                    });

                    newPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                if (newPasswordView.getText().toString().length() == 0) {
                                    newPasswordLayout.setErrorTextAppearance(R.style.NoteDisplay);
                                    newPasswordLayout.setError("Password should contain at least one number, " +
                                            "one uppercase letter and one special character.");
                                }
                            } else {
                                newPasswordLayout.setErrorEnabled(false);
                            }
                        }
                    });

                    newPasswordView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            newPasswordLayout.setErrorEnabled(false);
                            confirmNewPasswordLayout.setErrorEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.toString().length() == 0) {
                                newPasswordLayout.setErrorTextAppearance(R.style.NoteDisplay);
                                newPasswordLayout.setError("Password should contain at least one number, " +
                                        "one uppercase letter and one special character.");
                            } else if (s.toString().length() >= 6 && oldPasswordView.getText().toString().length() >= 6
                                    && confirmNewPasswordView.getText().toString().length() >= 6) {
                                positiveButton(true);
                            } else {
                                positiveButton(false);
                            }
                        }
                    });

                    confirmNewPasswordView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            confirmNewPasswordLayout.setErrorEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.toString().length() >= 6 && oldPasswordView.getText().toString().length() >= 6
                                    && newPasswordView.getText().toString().length() >= 6) {
                                positiveButton(true);
                            } else {
                                positiveButton(false);
                            }
                        }
                    });
                    setFocus(oldPasswordView);
                }
            });
        } else {
            Snackbar.make(rootLayout, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }
    }

    private void saveEditedProfile() {
        if ((user.getUsername() != usernameView.getText()) || (user.getEmail() != emailView.getText()) ||
                (user.getContact() != contactView.getText()) || ((alternateContactView.getText().toString().length() > 0)
                && user.getAlternateContact() == null) || (user.getAlternateContact() != null &&
                user.getAlternateContact() != alternateContactView.getText()))
            promptPassword();

        usernameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        emailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        contactView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        alternateContactView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        passwordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        profileEmailView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0);
        profileContactView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);
        profileAlternateContactView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);
        profilePasswordView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0);
        mOptionsMenu.findItem(R.id.profile_save).setVisible(false);
        mOptionsMenu.findItem(R.id.profile_edit).setVisible(true);
        usernameView.setEnabled(false);
        emailView.setEnabled(false);
        contactView.setEnabled(false);
        alternateContactView.setEnabled(false);
        passwordView.setEnabled(false);
    }

    private void promptPassword() {
        View customView = getLayoutInflater().inflate(R.layout.layout_edit_profile_prompt_password, container, false);
        EditText promptPasswordView = customView.findViewById(R.id.edit_profile_prompt_password);

        dialog = new AlertDialog.Builder(getContext())
                .setTitle("Enter Password")
                .setView(customView)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface di) {
                dialog.getButton(di.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        saveProfile();
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Saving...");
                        progressDialog.show();
                    }
                });
                dialog.getButton(di.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.main_content_frame, new ProfileFragment());
                        ft.commit();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
        positiveButton(false);

        promptPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 6) {
                    positiveButton(false);
                } else {
                    positiveButton(true);
                }
            }
        });
        setFocus(promptPasswordView);
    }

    private void saveProfile() {
        EndPointInterface service = APIUtils.getAPIService();
        service.userUpdate(user.get_id(), user.getEmail(), token,
                usernameView.getText().toString(),
                emailView.getText().toString(),
                contactView.getText().toString(),
                alternateContactView.getText().toString()).enqueue(new Callback<User>() {

            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.body() != null) {
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("user", new Gson().toJson(response.body()));
                    edit.apply();
                    progressDialog.cancel();
                    Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_content_frame, new ProfileFragment());
                ft.commit();
                progressDialog.cancel();
                Log.e(TAG + "On Failure", t.getMessage());
                Snackbar.make(rootLayout, "Something went wrong. Please try again later!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void positiveButton(boolean enable) {
        if (enable)
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        else
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    private void setFocus(final EditText et) {
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                et.post(new Runnable() {
                    @Override
                    public void run() {
                        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        et.requestFocus();
    }

    private boolean isConnectedToInternet() {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void initViews() {
        rootLayout = rootView.findViewById(R.id.fragment_profile_layout);
        usernameView = rootView.findViewById(R.id.profile_name);
        profileEmailView = rootView.findViewById(R.id.profile_email_tv);
        emailView = rootView.findViewById(R.id.profile_email);
        profileContactView = rootView.findViewById(R.id.profile_mobile_number_tv);
        contactView = rootView.findViewById(R.id.profile_mobile_number);
        profileAlternateContactView = rootView.findViewById(R.id.profile_alternate_number_tv);
        alternateContactView = rootView.findViewById(R.id.profile_alternate_number);
        profilePasswordView = rootView.findViewById(R.id.profile_password_tv);
        passwordView = rootView.findViewById(R.id.profile_password);
    }
}