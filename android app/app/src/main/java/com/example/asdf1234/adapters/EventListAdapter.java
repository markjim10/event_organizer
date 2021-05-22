package com.example.asdf1234.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asdf1234.R;
import com.example.asdf1234.models.Event;

import java.util.ArrayList;

public class EventListAdapter extends BaseAdapter {
    private ArrayList<Event> listData;
    private LayoutInflater layoutInflater;
    public EventListAdapter(Context aContext, ArrayList<Event> listData) {
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
        TextView tvDays;
        TextView tvName;
        TextView tvDate;
        TextView tvStatus;
        TextView tvTasks;
    }

    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.list_event, null);
            holder = new ViewHolder();
            holder.tvDays= v.findViewById(R.id.eventListDaysLeft);
            holder.tvName = v.findViewById(R.id.eventListName);
            holder.tvDate = v.findViewById(R.id.eventListDate);
            holder.tvStatus = v.findViewById(R.id.eventListStatus);
            holder.tvTasks =  v.findViewById(R.id.eventListTasks);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        String typeMsg = listData.get(position).getStatus();

        if(typeMsg.equals("in-progress"))
        {
            holder.tvStatus.setTextColor(Color.parseColor("#FFA500"));
        } else if (typeMsg.equals("completed")) {
            holder.tvStatus.setTextColor(Color.parseColor("#00FF00"));
        }

        holder.tvName.setText(listData.get(position).getName());
        holder.tvDate.setText(listData.get(position).getDate());
        holder.tvStatus.setText(listData.get(position).getStatus());
        holder.tvTasks.setText(listData.get(position).getTasks());
        holder.tvDays.setText(listData.get(position).getDaysLeft());
        return v;
    }

}
