package com.example.asdf1234.ui.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asdf1234.R;
import com.example.asdf1234.databinding.FragmentDashboardBinding;
import com.example.asdf1234.services.UrlService;
import com.example.asdf1234.models.Task;
import com.example.asdf1234.adapters.TaskListAdapter;
import com.example.asdf1234.ui.tasks.CreateTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    Map<Integer, JSONObject> taskMap = new HashMap<>();
    Map<Integer, String> taskMapId = new HashMap<>();

    UrlService urlService = new UrlService();
    SharedPreferences sharedPreferences;
    String user_id, task_id;
    String TAG = "delete_task.php";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        sharedPreferences = this.requireActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        getTasks();
        final Button button = requireView().findViewById(R.id.btnAddNewTask);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity().getApplicationContext(), CreateTask.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showAlert(String title, String message, String task_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    Toast.makeText(getContext(), task_id, Toast.LENGTH_SHORT).show();
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    String url = this.urlService.getUrl() + "tasks/delete_task.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                Log.d(TAG, response);
                                getTasks();
                            }, error -> {
                        Log.e(TAG, error.toString());
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("task_id", task_id);
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
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private ArrayList<Task> getListData(String response) {
        ArrayList<Task> results = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                Task item = new Task();
                JSONObject obj = jsonArray.getJSONObject(i);
                taskMap.put(i, obj);
                taskMapId.put(i, obj.getString("task_id"));
                item.setId(obj.getString("task_id"));
                item.setName(obj.getString("task_name"));
                item.setExpenses(obj.getString("task_expenses"));
                item.setPayment(obj.getString("task_payment"));
                item.setStatus(obj.getString("task_status"));
                item.setEventId(obj.getString("event_id"));
                results.add(item);
            }

        } catch (JSONException e) {
            Log.e("json err", e.getMessage());
        }
        return results;
    }

    public void getTasks() {
        SharedPreferences sharedPreferences = this.requireActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", null);
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        String url = this.urlService.getUrl() + "tasks/available_tasks.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("available_tasks: ", response);
                    ArrayList<Task> taskList = getListData(response);
                    final ProgressBar progressBar = requireView().findViewById(R.id.pbarTask);
                    progressBar.setVisibility(View.GONE);
                    final ListView lv = requireView().findViewById(R.id.list_tasks_fragment);
                    lv.setVisibility(View.VISIBLE);
                    Parcelable state = lv.onSaveInstanceState();
                    lv.setAdapter(new TaskListAdapter(requireActivity().getApplicationContext(), taskList));
                    lv.onRestoreInstanceState(state);
                    lv.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
                        task_id = taskMapId.get(pos);
                        showAlert("Delete", "Are you sure you want to delete this task?", task_id);
                        return true;
                    });
                }, error -> {
            Log.e("available_tasks: ", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
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


}