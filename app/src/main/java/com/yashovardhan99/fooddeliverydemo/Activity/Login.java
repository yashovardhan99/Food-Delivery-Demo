package com.yashovardhan99.fooddeliverydemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yashovardhan99.fooddeliverydemo.Fragment.LoginEnterMobile;
import com.yashovardhan99.fooddeliverydemo.Fragment.LoginEnterOTPFragment;
import com.yashovardhan99.fooddeliverydemo.R;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity implements LoginEnterOTPFragment.OnCompleteListener, LoginEnterOTPFragment.OnDetachListener {
    //class instance variables:
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String phone;
    private String verificationID;
    private boolean verificationInProgress;
    private boolean OTPResent = false;
    private boolean autoRetrivalTimeOut = false;
    private boolean invalidCreds = false;
    private boolean OTPSent = false;
    private boolean OTPFragmentReady = false;
    //fragments
    LoginEnterMobile enterMobileFragment;
    LoginEnterOTPFragment enterOTPFragment;

    String TAG = "LOGIN"; //debug tag

    public static final String INVALID_PHONE_KEY = "INVALID_PHONE_ENTERED"; //Key used for passing data to fragment
    //private keys for save instance states
    private final String VERIFICATION_IN_PROG_KEY = "VER_IN_PROG";
    private final String PHONE_NUMBER_KEY = "PHONE_NUMBER";
    private final String OTP_SENT_KEY = "OTP_SENT";
    private final String VER_ID_KEY = "VER_ID";
    private final String REDEND_TOKEN_KEY = "RESEND_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterMobileFragment = new LoginEnterMobile();
        enterOTPFragment = new LoginEnterOTPFragment();

        if(savedInstanceState!=null) {

            verificationInProgress = savedInstanceState.getBoolean(VERIFICATION_IN_PROG_KEY, false);
            OTPSent = savedInstanceState.getBoolean(OTP_SENT_KEY, false);

            if (savedInstanceState.containsKey(PHONE_NUMBER_KEY))
                phone = savedInstanceState.getString(PHONE_NUMBER_KEY);
            else
                phone = "";

            verificationID = savedInstanceState.getString(VER_ID_KEY,"");
            resendToken = savedInstanceState.getParcelable(REDEND_TOKEN_KEY);
            Log.d(TAG,"Restored instance state");
        }
        else{
            verificationInProgress = false;
            phone = "";
        }

        if(verificationInProgress)
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_login_fragmentcontainer, enterOTPFragment).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_login_fragmentcontainer, enterMobileFragment).commit();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG,"Verification completed : "+phoneAuthCredential.getSmsCode());
                if(OTPFragmentReady)
                    ((LoginEnterOTPFragment) getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer)).OTPretrived(phoneAuthCredential.getSmsCode());
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG, "Verification failed : " + e.toString());
                Bundle bundle = new Bundle();
                bundle.putString(INVALID_PHONE_KEY, phone);
                enterMobileFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_login_fragmentcontainer, enterMobileFragment).commit();
//                LoginEnterMobile mobileFragment = (LoginEnterMobile) getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer);
//                mobileFragment.invalidPhoneNumber(phone);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG,"Code sent : "+s);
                OTPSent = true;

                if(OTPFragmentReady)
                    ((LoginEnterOTPFragment)getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer))
                            .onOTPSent(phone);

                verificationID = s;
                resendToken = forceResendingToken;
                autoRetrivalTimeOut = false;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.d(TAG,"Auto retrival timeout : "+s);
                autoRetrivalTimeOut = true;
                if(OTPFragmentReady)
                    ((LoginEnterOTPFragment)getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer)).autoRetrivalTimeOut();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(verificationInProgress)
            verifyPhoneNumber(phone);
    }

    public void verifyPhoneNumber(String phone){
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks
            );
        }catch (Exception e){
            Log.e(TAG,"Error : "+e.toString());
        }
        verificationInProgress = true;
        this.phone = phone;
        Log.d(TAG,"Phone number received : "+phone);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_login_fragmentcontainer, enterOTPFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(VERIFICATION_IN_PROG_KEY,verificationInProgress);
        outState.putString(PHONE_NUMBER_KEY, phone);
        outState.putBoolean(OTP_SENT_KEY, OTPSent);
        outState.putString(VER_ID_KEY, verificationID);
        outState.putParcelable(REDEND_TOKEN_KEY, resendToken);
        Log.d(TAG,"Saved instance state");
    }

    void signIn(PhoneAuthCredential credential){
        verificationInProgress = false;
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"Signed In");
                            Intent MoveBack = new Intent(Login.this, getCallingClass());
                            startActivity(MoveBack);
                            finish();

                        }
                        else {
                            Log.d(TAG,"Sign In failed"+task.getException());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                //invalid verification code
                                invalidCreds = true;
                                if(OTPFragmentReady){
                                    ((LoginEnterOTPFragment)(getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer))).invalidCredentials();
                                }
                            }
                        }
                    }
                });
    }

    public void signInWithOtp(String otp){
        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationID, otp);
        signIn(authCredential);
    }

    public void resendOTP(){

        try{
            if(resendToken!=null) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        mCallbacks,
                        resendToken
                        );
                Toast.makeText(this,"OTP Resent", Toast.LENGTH_SHORT).show();
                OTPResent = true;
                if(OTPFragmentReady)
                    ((LoginEnterOTPFragment)getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer)).onOTPResent();
            }
            else
                Toast.makeText(this,"Unable to resend OTP", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Log.e(TAG,"ERROR : "+e.toString());
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    public void changeMobile(){
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_login_fragmentcontainer,enterMobileFragment).commit();
    }

    @Override
    public void onComplete() {
        OTPFragmentReady = true;
        Log.d(TAG,"Fragment OTP ready");

        if(OTPSent)
            ((LoginEnterOTPFragment)getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer)).onOTPSent(phone);

        if(OTPResent)
            ((LoginEnterOTPFragment)getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer)).onOTPResent();

        if(autoRetrivalTimeOut)
            ((LoginEnterOTPFragment)getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer)).autoRetrivalTimeOut();

        if(invalidCreds)
            ((LoginEnterOTPFragment)getSupportFragmentManager().findFragmentById(R.id.framelayout_login_fragmentcontainer)).invalidCredentials();
    }

    @Override
    public void onDetached() {
        OTPFragmentReady = false;
        Log.d(TAG,"Fragment OTP detached");
    }

    private Class getCallingClass(){
        switch(getIntent().getStringExtra(MainActivity.CLASS_NAME)){
            case "MainActivity" : return MainActivity.class;

            default: return MainActivity.class;
        }
    }
}
