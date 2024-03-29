package com.sizeit.findmysize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sizeit.utils.Constants;
import com.sizeit.utils.LocaleHelper;
import com.sizeit.utils.Preferences;
import com.sizeit.utils.ProgressDialog;

abstract public class BaseActivity extends AppCompatActivity {

    public static int REQUEST_CODE = 228;

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(LocaleHelper.onAttach(newBase));
//    }
//
//    @Override
//    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
//        if (overrideConfiguration != null) {
//            int uiMode = overrideConfiguration.uiMode;
//            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
//            overrideConfiguration.uiMode = uiMode;
//        }
//        super.applyOverrideConfiguration(overrideConfiguration);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocaleContext(this, Preferences.getPreferences(this).getString(Constants.language));
    }

    public void start(Class<? extends Activity> activity) {
        startActivityForResult(new Intent(this, activity), REQUEST_CODE);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void startWithClearStack(Class<? extends Activity> activity) {
        startActivity(new Intent(this, activity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        this.finish();
    }

    public void finishWithAnimation() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void finishWithResultAndAnimation(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void showProgress() {
        ProgressDialog.getInstance().show(this);
    }

    public void hideProgress() {
        ProgressDialog.getInstance().dismiss();
    }
}