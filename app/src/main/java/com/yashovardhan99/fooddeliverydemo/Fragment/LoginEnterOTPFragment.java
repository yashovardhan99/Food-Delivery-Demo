package com.yashovardhan99.fooddeliverydemo.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yashovardhan99.fooddeliverydemo.Activity.Login;
import com.yashovardhan99.fooddeliverydemo.R;

/**
 * Created by Yashovardhan99 on 24-08-2018 as a part of FoodDeliveryDemo.
 */
public class LoginEnterOTPFragment extends Fragment {

    private TextInputEditText editTextOTP;
    private Button submit;
    private TextView autoRetrive;
    private TextView otpSent;
    private TextInputLayout inputLayoutOTP;
    private TextView otpResend;
    private TextView changeMobile;
    private LinearLayout progressLayout;
    private TextView enterOTPprompt;

    public LoginEnterOTPFragment(){

    }

    public static interface OnCompleteListener{
        public abstract void onComplete();
    }

    public static interface OnDetachListener{
        public abstract void onDetached();
    }
    private OnCompleteListener mListener;
    private OnDetachListener onDestroyViewListener;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_login_verifymobile, container, false);
        editTextOTP = RootView.findViewById(R.id.textinputedittext_login_otp);
        submit = RootView.findViewById(R.id.button_login_submitotp);
        autoRetrive = RootView.findViewById(R.id.textview_login_verifyingprompt);
        otpSent = RootView.findViewById(R.id.textview_login_otpsentprompt);
        inputLayoutOTP = RootView.findViewById(R.id.textinputlayout_login_enterotp);
        otpResend = RootView.findViewById(R.id.textview_login_otpResendPrompt);
        changeMobile = RootView.findViewById(R.id.textview_login_changenumber);
        progressLayout = RootView.findViewById(R.id.linearlayout_login_indicateautoretriving);
        enterOTPprompt = RootView.findViewById(R.id.textview_login_enterotpprompt);
        mListener.onComplete();
        return RootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCompleteListener) context;
        }catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }


        try {
            onDestroyViewListener = (OnDetachListener) context;
        }catch (final ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnDetachListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroyViewListener.onDetached();
    }

    View.OnClickListener otpResendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity()!=null)
                ((Login)getActivity()).resendOTP();
        }
    };

    View.OnClickListener onMobileChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity()!=null)
                ((Login)getActivity()).changeMobile();
        }
    };

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity()!=null){
                String otp = editTextOTP.getText().toString();
                if(otp.length()!=6)
                    editTextOTP.setError("Invalid OTP");
                else{
                    ((Login)getActivity()).signInWithOtp(otp);
                    OTPSubmitted();
                }
            }
        }
    };


    public void onOTPSent(String phone){
        editTextOTP.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
        autoRetrive.setText("Trying to auto retrieve OTP");
        otpSent.setText("OTP has been sent to "+phone);
        otpSent.setVisibility(View.VISIBLE);
        inputLayoutOTP.setVisibility(View.VISIBLE);
        otpResend.setVisibility(View.VISIBLE);
        changeMobile.setVisibility(View.VISIBLE);

        editTextOTP.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editTextOTP.setShowSoftInputOnFocus(true);
        }

        submit.setOnClickListener(submitListener);
        otpResend.setOnClickListener(otpResendListener);
        changeMobile.setOnClickListener(onMobileChangeListener);
    }

    public void onOTPResent(){
        progressLayout.setVisibility(View.VISIBLE);
        enterOTPprompt.setVisibility(View.INVISIBLE);
    }

    public void autoRetrivalTimeOut(){
        progressLayout.setVisibility(View.INVISIBLE);
        enterOTPprompt.setVisibility(View.VISIBLE);
    }

    public void OTPretrived(String otp){
        OTPSubmitted();
        editTextOTP.setText(otp);
    }

    public void invalidCredentials(){
        progressLayout.setVisibility(View.INVISIBLE);
        enterOTPprompt.setVisibility(View.VISIBLE);
        enterOTPprompt.setText("We couldn't verify your credentials");
        enterOTPprompt.setTextColor(Color.RED);
        inputLayoutOTP.setVisibility(View.VISIBLE);
        editTextOTP.setError("Invalid OTP");
        editTextOTP.setText(null);
        editTextOTP.requestFocus();
        submit.setVisibility(View.VISIBLE);
        inputLayoutOTP.setEnabled(true);
        otpResend.setVisibility(View.VISIBLE);
        changeMobile.setVisibility(View.VISIBLE);
    }

    public void OTPSubmitted(){
        editTextOTP.setError(null);
        enterOTPprompt.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        autoRetrive.setText("Verifying your credentials...");
        otpSent.setVisibility(View.GONE);
        editTextOTP.clearFocus();
        submit.setVisibility(View.GONE);
        inputLayoutOTP.setEnabled(false);
        otpResend.setVisibility(View.GONE);
        changeMobile.setVisibility(View.GONE);
    }
}
