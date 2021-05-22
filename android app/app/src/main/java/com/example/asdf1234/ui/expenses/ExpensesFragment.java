package com.example.asdf1234.ui.expenses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.asdf1234.adapters.ExpenseListAdapter;
import com.example.asdf1234.databinding.FragmentExpensesBinding;
import com.example.asdf1234.models.Expense;
import com.example.asdf1234.services.UrlService;
import com.example.asdf1234.ui.tasks.CreateTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpensesFragment extends Fragment {

    private ExpensesViewModel expensesViewModel;
    private FragmentExpensesBinding binding;
    UrlService urlService = new UrlService();
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        expensesViewModel = new ViewModelProvider(this).get(ExpensesViewModel.class);

        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = requireView().findViewById(R.id.pbarExpenses);
        getExpenses();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ArrayList<Expense> getListData(String response) {
        ArrayList<Expense> results = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                Expense item = new Expense();
                JSONObject obj = jsonArray.getJSONObject(i);
                item.setEventName(obj.getString("event_name"));
                item.setExpense(obj.getString("total"));
                results.add(item);
            }
        } catch (JSONException e) {
            Log.e("json err", e.getMessage());
        }
        return results;
    }

    public void getExpenses() {
        SharedPreferences sharedPreferences = this.requireActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", null);
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        String url = this.urlService.getUrl() + "expenses/get_expenses.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("get_expenses", response);

                    progressBar.setVisibility(View.GONE);

                    ArrayList<Expense> expenseList = getListData(response);
                    final ListView lv = requireView().findViewById(R.id.list_expenses);
                    lv.setVisibility(View.VISIBLE);
                    Parcelable state = lv.onSaveInstanceState();
                    lv.setAdapter(new ExpenseListAdapter(requireActivity().getApplicationContext(), expenseList));
                    lv.onRestoreInstanceState(state);
                }, error -> {
            Log.e("get_expenses", error.toString());
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