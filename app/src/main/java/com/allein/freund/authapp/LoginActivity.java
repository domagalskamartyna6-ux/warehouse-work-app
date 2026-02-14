package com.allein.freund.authapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.allein.freund.authapp.remote.APIUtils;
import com.allein.freund.authapp.remote.AuthService;
import com.allein.freund.authapp.remote.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    public static final String USER_COOKIE = "com.allein.freund.authapp.USER_COOKIE";
    private AuthService mAuthService;
    private String TAG = "LOGIN";
    private Button loginBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        mAuthService = APIUtils.getAuthService();
        loginBtn = (Button) findViewById(R.id.btn_submit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText emailInput = (EditText) findViewById(R.id.email_input);
                final EditText passwordInput = (EditText) findViewById(R.id.password_input);

                if (credentialsAreValid(emailInput, passwordInput)) {
                    String email = emailInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();
                    sendCredentials(email, password);
                    showLoginButton(false);
                }
            }
        });
    }

    private void sendCredentials(String email, String password) {
        mAuthService.sendCredentials(email, password)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body();
                            if ("success".equals(user.getStatus()) && user.getData() != null) {
                    
                                // Zapis ID i SKEY
                                getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                                        .edit()
                                        .putString("ID_EMPLOYEE", String.valueOf(user.getData().getIdEmployee()))
                                        .putString("SKEY", user.getData().getSkey())
                                        .apply();
                    
                                loginToast("Login complete.");
                                Log.i(TAG, "login complete.");
                    
                                // Przejście do MainActivity
                                passToMainActivity();
                    
                            } else {
                                loginToast("Credentials are invalid.");
                                Log.i(TAG, "credentials are invalid.");
                                showLoginButton(true);
                            }
                        } else {
                            loginToast("Login failed. Server error.");
                            Log.i(TAG, "login failed: " + response.message());
                            showLoginButton(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        loginToast("Login failed. Server is offline or broken.");
                        Log.e(TAG, "Unable to sent credentials to API. Server is offline or broken.");
                        showLoginButton(true);
                    }
                });
    }

    private void showLoginButton(Boolean flag) {
        if (flag) {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }
    }

    private String getUserCookie(List<String> cookies) {
        for (String cookie : cookies) {
            if (cookie.contains("user=")) {
                return cookie;
            }
        }
        return null;
    }

    private void passToMainActivity() {
        showLoginButton(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // żeby nie wracać do LoginActivity
    }

    private void loginToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    private Boolean credentialsAreValid(EditText emailInput, EditText passwordInput) {
        Boolean flag = true;
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Fill e-mail field.");
            flag = false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Wrong e-mail format.");
            flag = false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Fill password field.");
            flag = false;
        }
        return flag;
    }

}

