package com.example.asdf1234.ui.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ChangePassActivity extends AppCompatActivity {

    UrlService urlService = new UrlService();
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;

    EditText etOldPass, etNewPass, etConfirmPass;
    String user_id, oldPass, newPass, confirmPass;
    String TAG = "changepass.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        sharedPreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);

        etOldPass = findViewById(R.id.etChangePassOld);
        etNewPass = findViewById(R.id.etChangePassNew);
        etConfirmPass = findViewById(R.id.etChangePassConfirm);
        progressBar = findViewById(R.id.pbarChangePass);
    }

    public void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

    public void submit(View view) {
        oldPass = etOldPass.getText().toString();
        newPass = etNewPass.getText().toString();
        confirmPass = etConfirmPass.getText().toString();

        if(oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            displayMessage("Fill in all of the fields");
            return;
        } else if(!newPass.equals(confirmPass)) {
            displayMessage("Passwords do not match");
            return;
        }
        changePass();
    }

    public void changePass() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.urlService.getUrl() + "accounts/changepass.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.i(TAG, response);
                    if(response.equals("success")) {
                        showAlert("Success", "Password changed");
                    } else {
                        showAlert("Err", response);
                    }
                    progressBar.setVisibility(View.GONE);
                }, error -> {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("oldPass", oldPass);
                params.put("newPass", newPass);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
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
}