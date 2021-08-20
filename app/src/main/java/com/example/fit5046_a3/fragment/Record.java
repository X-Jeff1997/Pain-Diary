package com.example.fit5046_a3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.fit5046_a3.database.CustomerDatabase;
import com.example.fit5046_a3.databinding.RecordFragmentBinding;

import com.example.fit5046_a3.entity.Customer;
import com.example.fit5046_a3.viewmodel.CustomerViewModel;
import com.example.fit5046_a3.viewmodel.RecyclerViewAdapter;

import java.util.List;

public class Record extends Fragment {
    private CustomerViewModel customerViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private List<Customer> customers;
    private RecyclerViewAdapter adapter;
    private RecordFragmentBinding binding;

    public Record(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this com.example.navigation.fragment using the binding
        binding = RecordFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        adapter = new RecyclerViewAdapter();
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);

        customerViewModel= ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(CustomerViewModel.class);
        customerViewModel.getAllCustomers().observe(getViewLifecycleOwner(), new Observer<List<Customer>>() {
                    @Override
                    public void onChanged(@Nullable final List<Customer> customers) {
                        //here we use the adapter to update the data in RecyclerView
                        adapter.setData(customers);
                    }
                });
    return view;
    }
}