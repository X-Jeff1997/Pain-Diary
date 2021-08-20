package com.example.fit5046_a3.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.fit5046_a3.dao.CustomerDAO;
import com.example.fit5046_a3.database.CustomerDatabase;
import com.example.fit5046_a3.entity.Customer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CustomerRepository {
    private final CustomerDAO customerDao;
    private final LiveData<List<Customer>> allCustomers;

    public CustomerRepository(Application application) {
        CustomerDatabase db = CustomerDatabase.getInstance(application);
        customerDao = db.customerDao();
        allCustomers = customerDao.getAll();
    }

    // Room executes this query on a separate thread
    public LiveData<List<Customer>> getAllCustomers() {
        return allCustomers;
    }

    public void insert(final Customer customer) {
        CustomerDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDao.insert(customer);
            }
        });
    }

    public void deleteAll() {
        CustomerDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDao.deleteAll();
            }
        });
    }

    public void delete(final Customer customer) {
        CustomerDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDao.delete(customer);
            }
        });
    }

    public void updateCustomer(final Customer customer) {
        CustomerDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDao.updateCustomer(customer);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Customer> findByID(final String email, final String date) {
        return CompletableFuture.supplyAsync(new Supplier<Customer>() {
            @Override
            public Customer get() { return customerDao.findByID(email, date); }
            }, CustomerDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<Customer>> getData(final String email) {
        return CompletableFuture.supplyAsync(new Supplier<List<Customer>>() {
            @Override
            public List<Customer> get() { return customerDao.getData(email); }
        }, CustomerDatabase.databaseWriteExecutor);
    }
}