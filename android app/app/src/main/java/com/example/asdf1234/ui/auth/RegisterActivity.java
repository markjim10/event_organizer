package com.example.asdf1234.ui.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText etEmail, etUsername, etMobile, etPassword, etConfirm;
    String email, username, mobile, password, confirm;
    UrlService urlService = new UrlService();
    String TAG = "register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_register);
    }

    public void register(View view) {

        progressBar = findViewById(R.id.pbarRegister);
        progressBar.setVisibility(View.VISIBLE);

        etEmail = findViewById(R.id.etRegEmail);
        etUsername = findViewById(R.id.etRegUsername);
        etMobile = findViewById(R.id.etRegMobileNum);
        etPassword = findViewById(R.id.etRegPass);
        etConfirm = findViewById(R.id.etRegConfirm);

        email = etEmail.getText().toString();
        username = etUsername.getText().toString();
        mobile = etMobile.getText().toString();
        password = etPassword.getText().toString();
        confirm = etConfirm.getText().toString();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty() || mobile.isEmpty()) {
            displayMessage("Fill in all of the fields");
            progressBar.setVisibility(View.GONE);
        } else if(email.length() < 4 || username.length() < 4 || mobile.length() < 4 || password.length() < 4 || confirm.length() < 4) {
            displayMessage("The minimum input must be at least 4 characters");
            progressBar.setVisibility(View.GONE);
        } else if(!email.trim().matches(emailPattern)) {
            displayMessage("Email must be in correct format");
            progressBar.setVisibility(View.GONE);
        } else if(!password.equals(confirm)) {
            displayMessage("Password does not match");
            progressBar.setVisibility(View.GONE);
        } else {
            submit();
        }
    }

    public void submit() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "accounts/register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("android_register", response);
                    progressBar.setVisibility(View.INVISIBLE);
                    if(response.equals("account already exists")) {
                        showAlert("Error", "Email or Username is already taken");
                    } else if(response.equals("sent a verification code")) {
                        showAlert("Registered", "A verification code has been sent to your email");
                        clearTexts();
                    } else {
                        Log.e(TAG, response);
                    }
                }, error -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e("android_register", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("username", username);
                params.put("mobile", mobile);
                params.put("password", password);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void clearTexts() {
        etConfirm.setText("");
        etEmail.setText("");
        etMobile.setText("");
        etUsername.setText("");
        etPassword.setText("");
    }

    public void displayMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    if(title.equals("Error")) {

                    } else {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
}