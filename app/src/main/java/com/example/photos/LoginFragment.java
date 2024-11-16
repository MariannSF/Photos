/*
    InClass08
    LoginFragment.java
    Mariann Szabo-Freund,
    

 */

package com.example.photos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.photos.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    final private String TAG = "demo";
    EditText editTextEmail, editTextPw;
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentLoginBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Login");


        editTextEmail = binding.editTextEmail;
        editTextPw = binding.editTextPassword;


        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editTextEmail.getText().toString();
                String pw = editTextPw.getText().toString();

                if(email.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Error!!")
                            .setMessage("Missing email!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.create().show();
                } else if(pw.isEmpty()) {
                    //Toast.makeText(getActivity(), "Enter valid password !!", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error By API")
                            .setMessage("Enter valid password !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.create().show();
                }else {

                    //call firebase to login
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email, pw)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        Log.d("TAG", "onComplete: Logged in Success ");
                                        Log.d(TAG, "onComplete: user"+ mAuth.getCurrentUser().getUid());
                                        Log.d(TAG, "onComplete: The name form Login "+ mAuth.getCurrentUser().getDisplayName());
                                        //this is how i can get the current user if the user is logged in
                                        //otherwise it is null = not logged in.
                                        mAuth.getCurrentUser();

                                        mListener.goToListing();

                                        //mListener.goToProfile();
                                    }else {

                                        Log.d(TAG, "onComplete: "+ task.getException().getMessage());

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Login not successful")
                                                .setMessage(task.getException().getMessage())
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                        builder.create().show();
                                    }
                                }
                            });





                }



            }
        });
        binding.buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreateAccount();
            }
        });




    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof LoginListener){
            mListener = (LoginListener) context;
        }
    }

    LoginListener mListener;

    interface LoginListener{
        void goToCreateAccount();
        void goToListing();
        void goToProfile();
    }
}

