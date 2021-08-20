package com.example.fit5046_a3.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fit5046_a3.entity.Customer;
import com.example.fit5046_a3.repository.CustomerRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CustomerViewModel extends AndroidViewModel {
    private final CustomerRepository cRepository;
    private final LiveData<List<Customer>> allCustomers;
    private List<Customer> alldata;
    public CustomerViewModel (Application application) {
        super(application);
        cRepository = new CustomerRepository(application);
        allCustomers = cRepository.getAllCustomers();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Customer> findByIDFuture(final String email, final String date){
        return cRepository.findByID(email, date);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<Customer>> getData(final String email){
        return cRepository.getData(email);
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return allCustomers;
    }
    public void insert(Customer customer) {
        cRepository.insert(customer);
    }

    public void deleteAll() {
        cRepository.deleteAll();
    }
    public void update(Customer customer) {
        cRepository.updateCustomer(customer);
    }
}
