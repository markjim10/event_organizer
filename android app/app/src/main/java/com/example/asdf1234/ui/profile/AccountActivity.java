package com.example.asdf1234.ui.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.R;
import com.example.asdf1234.services.UrlService;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    UrlService urlService = new UrlService();
    ProgressBar progressBar;
    EditText etEmail, etMobile;
    String user_id, email, mobile;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sharedPreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);
        email = sharedPreferences.getString("email", null);
        mobile = sharedPreferences.getString("mobile", null);

        progressBar = findViewById(R.id.pbarAccount);

        etEmail = findViewById(R.id.etUpdateEmail);
        etMobile = findViewById(R.id.etUpdateMobileNum);

        etEmail.setText(email);
        etMobile.setText(mobile);

    }

    public void updateAccount(View view) {
        email = etEmail.getText().toString();
        mobile = etMobile.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "accounts/update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.i("update", response);
                    if(response.equals("success")) {
                        showAlert("Success", "Account updated");
                    } else {
                        showAlert("Err", response);
                    }
                    progressBar.setVisibility(View.GONE);
                }, error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("update", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("email", email);
                params.put("mobile", mobile);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void changePass(View view) {
        Intent intent = new Intent(this, ChangePassActivity.class);
        startActivity(intent);
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    //
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
}