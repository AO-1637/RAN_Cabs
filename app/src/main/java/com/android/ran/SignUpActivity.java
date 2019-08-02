package com.android.ran;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.ran.model.User;
import com.android.ran.network.APIUtils;
import com.android.ran.network.EndPointInterface;
import com.android.ran.network.SignUpResponse;
import com.android.ran.network.SignUpUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private LinearLayout rootLayout;
    private TextInputLayout emailLayout, passwordLayout, confirmPasswordLayout;
    private EditText usernameView, emailView, contactView, passwordView, confirmPasswordView;
    private CheckBox termsOfUseView;
    private Button signUpView;
    private InputMethodManager imm;
    private TextWatcher textWatcher;
    private String countryCode = "+91 ";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_sign_up);
        initViews();

        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        contactView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contactView.setFocusableInTouchMode(true);
                contactView.requestFocus();
                imm.showSoftInput(contactView, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
        });

        contactView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (contactView.getText().toString().length() == 0) {
                        contactView.setText(countryCode);
                        Selection.setSelection(contactView.getText(), contactView.getText().length());
                    }
                    imm.showSoftInput(contactView, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (contactView.getText().toString().equals(countryCode)) {
                        contactView.removeTextChangedListener(textWatcher);
                        contactView.getText().clear();
                        contactView.addTextChangedListener(textWatcher);
                    }
                }
            }
        });

        contactView.addTextChangedListener(textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91 ")) {
                    contactView.setText(countryCode);
                    Selection.setSelection(contactView.getText(), contactView.getText().length());
                }
            }
        });

        passwordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (passwordView.getText().toString().length() == 0) {
                        passwordLayout.setErrorTextAppearance(R.style.NoteDisplay);
                        passwordLayout.setError("Password should contain at least one number, " +
                                "one uppercase letter and one special character.");
                    }
                } else {
                    passwordLayout.setErrorEnabled(false);
                }
            }
        });

        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setErrorEnabled(false);
                confirmPasswordLayout.setErrorEnabled(false);
                confirmPasswordView.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    passwordLayout.setErrorTextAppearance(R.style.NoteDisplay);
                    passwordLayout.setError("Password should contain at least one number, " +
                            "one uppercase letter and one special character.");
                }
            }
        });

        confirmPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        String checkboxText = "By creating this account, you agree to our Terms & Conditions and Privacy Policy.";
        final SpannableStringBuilder ssBuilder = new SpannableStringBuilder(checkboxText);

        ClickableSpan tcClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                new AlertDialog.Builder(SignUpActivity.this)
                        .setTitle("Terms & Conditions")
                        .setMessage(R.string.content_about)
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
            }
        };

        ClickableSpan ppClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                new AlertDialog.Builder(SignUpActivity.this)
                        .setTitle("Privacy Policy")
                        .setMessage(R.string.content_about)
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
            }
        };

        ssBuilder.setSpan(
                tcClickableSpan,
                checkboxText.indexOf("Terms & Conditions"),
                checkboxText.indexOf("Terms & Conditions") + String.valueOf("Terms & Conditions").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                ppClickableSpan,
                checkboxText.indexOf("Privacy Policy"),
                checkboxText.indexOf("Privacy Policy") + String.valueOf("Privacy Policy").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        termsOfUseView.setText(ssBuilder);
        termsOfUseView.setMovementMethod(LinkMovementMethod.getInstance());

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        String username = usernameView.getText().toString();
        String email = emailView.getText().toString();
        String contact = contactView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        passwordLayout.setErrorEnabled(false);
        confirmPasswordLayout.setErrorEnabled(false);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            focusView = usernameView;
            cancel = true;
        } else if (!username.matches(".*[a-zA-Z]+.*")) {
            focusView = usernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            focusView = emailView;
            cancel = true;
        } else if (TextUtils.isEmpty(contact)) {
            focusView = contactView;
            cancel = true;
        } else if (contact.length() > 4 && contact.length() < 14) {
            focusView = contactView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            focusView = passwordView;
            cancel = true;
        } else if (password.length() > 0 && password.length() < 6) {
            focusView = passwordView;
            cancel = true;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            focusView = confirmPasswordView;
            cancel = true;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError("Passwords do not match.");
            focusView = confirmPasswordView;
            cancel = true;
        } else if (!termsOfUseView.isChecked()) {
            Toast.makeText(SignUpActivity.this, "Please agree to our Terms & Conditions and Privacy Policy!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cancel) {
            focusView.requestFocus();
            focusView.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        } else {
            if (isConnectedToInternet()) {
                EndPointInterface service = APIUtils.getAPIService();
                service.createUser(username, email, contact, password).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        } else {
                            SignUpResponse error = SignUpUtils.parseError(response);
                            displayResponse(error.response());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.e(TAG + "On Failure", t.getMessage());
                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
                    }
                });
            } else
                Snackbar.make(rootLayout, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }
    }

    private void displayResponse(String message) {
        switch (message) {
            case "Email not valid":
                emailLayout.setError("Email address not valid.");
                emailView.requestFocus();
                break;
            case "Email already registered":
                emailLayout.setError("Email address already registered.");
                emailView.requestFocus();
                break;
            case "Password weak":
                passwordLayout.setErrorTextAppearance(R.style.ErrorDisplay);
                passwordLayout.setError("Password should contain at least one number, " +
                        "one uppercase letter and one special character.");
                passwordView.requestFocus();
                break;
            default:
                Log.w(TAG + "On Response", message);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initViews() {
        rootLayout = findViewById(R.id.activity_sign_up_layout);
        usernameView = findViewById(R.id.sign_up_name);
        emailLayout = findViewById(R.id.sign_up_email_layout);
        emailView = findViewById(R.id.sign_up_email);
        contactView = findViewById(R.id.sign_up_mobile_number);
        passwordLayout = findViewById(R.id.sign_up_password_layout);
        passwordView = findViewById(R.id.sign_up_password);
        confirmPasswordLayout = findViewById(R.id.sign_up_confirm_password_layout);
        confirmPasswordView = findViewById(R.id.sign_up_confirm_password);
        termsOfUseView = findViewById(R.id.sign_up_terms_of_use);
        signUpView = findViewById(R.id.button_create_account);
    }
}