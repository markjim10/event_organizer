package com.example.asdf1234.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asdf1234.R;
import com.example.asdf1234.models.Expense;

import java.util.ArrayList;

public class ExpenseListAdapter extends BaseAdapter {
    private ArrayList<Expense> listData;
    private LayoutInflater layoutInflater;
    public ExpenseListAdapter(Context aContext, ArrayList<Expense> listData) {
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
        TextView tvEvent;
        TextView tvExpense;
    }

    public View getView(int position, View v, ViewGroup vg) {
        ExpenseListAdapter.ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.list_expense, null);
            holder = new ExpenseListAdapter.ViewHolder();
            holder.tvEvent= v.findViewById(R.id.expenseListEventName);
            holder.tvExpense =  v.findViewById(R.id.expenseListExpense);
            v.setTag(holder);
        } else {
            holder = (ExpenseListAdapter.ViewHolder) v.getTag();
        }

        holder.tvEvent.setText("Event: " +listData.get(position).getEventName());
        holder.tvExpense.setText("Total Expenses: PHP " + listData.get(position).getExpense());
        return v;
    }
}
