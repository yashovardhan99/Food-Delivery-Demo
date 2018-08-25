package com.yashovardhan99.fooddeliverydemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.yashovardhan99.fooddeliverydemo.R;

public class MainActivity extends AppCompatActivity {

    public static final String CLASS_NAME = "CLASS_NAME";// to keep record of calling class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            signOut();
        }
        findViewById(R.id.tempButton_Main_signOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut(){
        Intent signIn = new Intent(this,Login.class);
        signIn.putExtra(CLASS_NAME,"MainActivity");
        startActivity(signIn);
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
