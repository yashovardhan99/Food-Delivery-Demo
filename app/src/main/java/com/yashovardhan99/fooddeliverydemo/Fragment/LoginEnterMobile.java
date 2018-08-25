package com.yashovardhan99.fooddeliverydemo.Fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yashovardhan99.fooddeliverydemo.Activity.Login;
import com.yashovardhan99.fooddeliverydemo.R;

import java.util.Locale;

/**
 * Created by Yashovardhan99 on 24-08-2018 as a part of FoodDeliveryDemo.
 */
public class LoginEnterMobile extends Fragment {

    private TextInputEditText phoneEditText;
    private Button save;
    private TextView prompt;

    public LoginEnterMobile(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_login_getmobile, container, false);

        phoneEditText = RootView.findViewById(R.id.textinputedittext_login_togetmobile);
        save = RootView.findViewById(R.id.button_login_submitmobile);
        prompt = RootView.findViewById(R.id.textview_login_mobilenoprompt);

        final TextInputEditText countryEditText = RootView.findViewById(R.id.textinputedittext_login_countrycode);

        if(getArguments()!=null && getArguments().containsKey(Login.INVALID_PHONE_KEY))
            invalidPhoneNumber(getArguments().getString(Login.INVALID_PHONE_KEY));

        phoneEditText.requestFocus();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = countryEditText.getText().toString().trim() + phoneEditText.getText().toString().trim();

                if(phoneEditText.getText().toString().trim().length()<4 || !PhoneNumberUtils.isGlobalPhoneNumber(phone))
                    invalidPhoneNumber(phone);

                else if(!countryEditText.getText().toString().trim().startsWith("+") || countryEditText.getText().toString().trim().length()<2)
                    countryEditText.setError("Please enter a valid coumtry code, like +91 for India");

                else {
                    String formattedPhone;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        formattedPhone = PhoneNumberUtils.formatNumberToE164(phone, Locale.getDefault().getISO3Country());
                    else
                        formattedPhone = PhoneNumberUtils.formatNumber(phone);
                    if(formattedPhone!=null)
                        ((Login) getActivity()).verifyPhoneNumber(formattedPhone);
                    else
                        ((Login) getActivity()).verifyPhoneNumber(phone);
                }
            }
        });
        return RootView;
    }

    public void invalidPhoneNumber(String phone){
        phoneEditText.setError("Please enter a valid phone number");
        String p = "We could not verify the number "+PhoneNumberUtils.formatNumber(phone);
        prompt.setText(p);
        prompt.setTextColor(Color.RED);
    }
}
