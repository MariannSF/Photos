/*
    InClass08
    MainActivity.java
    Mariann Szabo-Freund,
    Bhaskararayuni Sai Datta

    Group 07

 */

package com.example.photos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterIlistener, ProfileFragment.ProfileIlistener {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // contextOfApplication = getContextOfApplication();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerView, new LoginFragment())
                    .commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerView, new ListingFragment(),"listing")
                    .commit();
        }
    }

    /*public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }*/
    @Override
    public void goToCreateAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView,new  RegisterFragment())
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void goToListing() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new ListingFragment())
                .commit();

    }

    @Override
    public void goToProfile() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new ProfileFragment())
                .commit();
    }

    @Override
    public void goToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }


    @Override
    public void goToprofiel() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new ProfileFragment())
                .commit();
    }
}