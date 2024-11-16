/*
    InClass08
    RegisterFragment.java
    Mariann Szabo-Freund

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

import com.example.photos.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment {

    final private String TAG = "demo";
    private FirebaseAuth mAuth;
    EditText editTextEmail, editTextName, editTextPassword;
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentRegisterBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Register");
        editTextEmail = binding.editTextEmail;
        editTextName = binding.editTextTextPersonName;
        editTextPassword = binding.editTextPassword;

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String pw = editTextPassword.getText().toString();
                String name = editTextName.getText().toString();
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
                }else if( pw.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Error!!")
                            .setMessage("Missing password!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.create().show();

                }else {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email, pw)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        Log.d("TAG", "onComplete: Registration  is Successful ");
                                        Log.d(TAG, "onComplete: the name of USER IS "+name);
                                        Log.d(TAG, "onComplete: user"+ mAuth.getCurrentUser().getUid());
                                        //this is how i can get the current user if the user is logged in
                                        //otherwise it is null = not logged in.
                                        mAuth.getCurrentUser();

                                        mListener.goToListing();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name).build();

                                        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "onSuccess: User updated ");
                                            }
                                        });

                                        Log.d(TAG, "onComplete: the name of USER IS "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


                                    }else {
                                        Log.d(TAG, "onComplete: Error !!");
                                        Log.d(TAG, "onComplete: "+ task.getException().getMessage());
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage("Registration Unsuccessful!!")
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

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToLogin();

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof RegisterIlistener){
            mListener = (RegisterIlistener) context;
        }
    }

    RegisterIlistener mListener;

    interface RegisterIlistener{
        void  goToLogin();
        void goToListing();
    }
}

