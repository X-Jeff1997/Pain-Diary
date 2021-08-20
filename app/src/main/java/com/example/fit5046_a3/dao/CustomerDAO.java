package com.example.fit5046_a3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fit5046_a3.entity.Customer;

import java.util.List;

@Dao
public interface CustomerDAO {
    @Query("SELECT * FROM pain_diary ORDER BY p_date Desc")
    LiveData<List<Customer>> getAll();

    @Query("SELECT * FROM pain_diary WHERE p_date =:date and u_email =:email")
    Customer findByID(String email, String date);

    @Query("Select * From pain_diary WHERE u_email =:email")
    List<Customer> getData(String email);

    @Insert
    void insert(Customer customer);

    @Delete
    void delete(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Query("DELETE FROM pain_diary")
    void deleteAll();
}

