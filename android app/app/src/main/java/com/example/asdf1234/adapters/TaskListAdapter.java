package com.example.asdf1234.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asdf1234.R;
import com.example.asdf1234.models.Task;

import java.util.ArrayList;

public class TaskListAdapter extends BaseAdapter {
    private final ArrayList<Task> listData;
    private final LayoutInflater layoutInflater;
    public TaskListAdapter(Context aContext, ArrayList<Task> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }
    @Override
    public int getCount() {
        return listData.size();
    }
    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvExpenses;
        TextView tvStatus;
        TextView tvPayment;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.list_task, null);
            holder = new ViewHolder();
            holder.tvName = v.findViewById(R.id.taskListName);
            holder.tvExpenses = v.findViewById(R.id.taskListExpenses);
            holder.tvStatus = v.findViewById(R.id.taskListStatus);
            holder.tvPayment = v.findViewById(R.id.taskListPayment);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        String typeMsg = listData.get(position).getStatus();
        if(typeMsg.equals("pending"))
        {
            holder.tvStatus.setTextColor(Color.parseColor("#FFA500"));
        } else if (typeMsg.equals("completed")) {
            holder.tvStatus.setTextColor(Color.parseColor("#00FF00"));
        }

        holder.tvName.setText(listData.get(position).getName());
        holder.tvExpenses.setText("Expenses: " + listData.get(position).getExpenses());
        holder.tvStatus.setText("Status: " + listData.get(position).getStatus());
        holder.tvPayment.setText("Payment: " + listData.get(position).getPayment());
        return v;
    }

}
