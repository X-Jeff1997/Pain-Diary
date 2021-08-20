package com.example.fit5046_a3.viewmodel;

import android.content.Context;
import android.service.autofill.UserData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit5046_a3.R;
import com.example.fit5046_a3.databinding.CardviewLayoutBinding;
import com.example.fit5046_a3.databinding.RecordFragmentBinding;
import com.example.fit5046_a3.entity.Customer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ViewHolder> {
    List<Customer> customers = new ArrayList<>();
    //this method will be used by LiveData to keep updating the recyclerview

    public void setData(List<Customer> customers) {

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String email = fAuth.getCurrentUser().getEmail();
        for (int i=0; i < customers.size(); i++) {
            if (customers.get(i).email.equals(email)) {
                this.customers.add(customers.get(i));
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        CardviewLayoutBinding binding =
                CardviewLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    // this method binds the view holder created with Room livedata
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int
            position) {

            Customer customer = customers.get(position);
            viewHolder.binding.email.setText((customer.email));
            viewHolder.binding.details.setText(" Pain Level: " + customer.painLevel + "       Pain Location: " + customer.painLocation +
                    "\n Pain Rating: " + customer.painRating + "       Total Step: " + customer.totalStep + "\n Temp: " + customer.temperature + "°C" +
                    "       Humi: " + customer.humidity + "       Pres:" + customer.pressure);
            viewHolder.binding.date.setText(customer.date);

    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardviewLayoutBinding binding;

        public ViewHolder(CardviewLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

