package com.junjunguo.pocketmaps.db;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FBauth {

    private static final String TAG = "";
    private static FirebaseAuth mAuth;

    public static void initialiseFBauth(){
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public static void createAccount(){
        mAuth.createUserWithEmailAndPassword("valegianisagnotti@scu.edu", "password");
    }

    public static void accessAccount(){
        mAuth.signInWithEmailAndPassword("valegianisagnotti@scu.edu", "password");
    }

    public FirebaseAuth getmAuth() {
        //TODO check for null
        return mAuth;
    }
}
