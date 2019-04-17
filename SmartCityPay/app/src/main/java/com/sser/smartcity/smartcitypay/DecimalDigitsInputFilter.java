package com.sser.smartcity.smartcitypay;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Class for setting up custom number of decimal places in the EditText (needed for payment input)
class DecimalDigitsInputFilter implements InputFilter {

    private Pattern mPattern;

    DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
        mPattern= Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher=mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }

}