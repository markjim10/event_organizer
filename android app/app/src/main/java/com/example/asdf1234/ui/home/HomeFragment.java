package com.example.asdf1234.ui.home;

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
import com.example.asdf1234.adapters.EventListAdapter;
import com.example.asdf1234.databinding.FragmentHomeBinding;
import com.example.asdf1234.models.Event;
import com.example.asdf1234.models.User;
import com.example.asdf1234.services.UrlService;
import com.example.asdf1234.ui.events.CreateEvent;
import com.example.asdf1234.ui.events.ShowEvent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    Map<Integer, JSONObject> eventMap = new HashMap<>();
    Map<Integer, String> eventMapId = new HashMap<>();

    UrlService urlService = new UrlService();
    SharedPreferences sharedPreferences;
    String id, event_id;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = this.requireActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("user_id", null);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        getEvents();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        getEvents();
        final Button button = requireView().findViewById(R.id.btnAddNewEvent);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity().getApplicationContext(), CreateEvent.class);
            startActivity(intent);
        });
    }

    private void showAlert(String title, String message, String event_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    String url = this.urlService.getUrl() + "events/delete.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                Log.d("delete", response);
                                getEvents();
                            }, error -> {
                        Log.e("delete", error.toString());
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("event_id", event_id);
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

    private ArrayList getListData(String response) {
        ArrayList<Event> results = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                Event item = new Event();
                JSONObject obj = jsonArray.getJSONObject(i);
                eventMap.put(i, obj);
                eventMapId.put(i, obj.getString("event_id"));
                item.setDaysLeft(obj.getString("days_left"));
                item.setName(obj.getString("event_name"));
                item.setDate(obj.getString("event_date"));
                item.setStatus(obj.getString("event_status"));
                item.setTasks(obj.getString("event_task_status"));
                results.add(item);
            }

        } catch (JSONException e) {
            Log.e("json err", e.getMessage());
        }
        return results;
    }

    public void getEvents() {
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        String url = this.urlService.getUrl() + "events/get_events.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    ProgressBar progressBar = requireView().findViewById(R.id.pbarHome);
                    progressBar.setVisibility(View.GONE);
                    Log.d("get_events: ", response);
                    ArrayList eventList = getListData(response);
                    final ListView lv = requireView().findViewById(R.id.list_events);
                    lv.setVisibility(View.VISIBLE);
                    Parcelable state = lv.onSaveInstanceState();
                    lv.setAdapter(new EventListAdapter(requireActivity().getApplicationContext(), eventList));
                    lv.onRestoreInstanceState(state);
                    lv.setOnItemClickListener((parent, view, position, id) -> {
                        Log.d("get_events: ", String.valueOf(eventMap.get(position)));
                        Intent intent = new Intent(requireActivity().getApplicationContext(), ShowEvent.class);
                        intent.putExtra("event", String.valueOf(eventMap.get(position)));
                        startActivity(intent);
                    });
                    lv.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
                        event_id = eventMapId.get(pos);
                        showAlert("Delete", "Are you sure you want to delete this event?", event_id);
                        return true;
                    });
                }, error -> {
                    Log.e("get_events: ", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
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