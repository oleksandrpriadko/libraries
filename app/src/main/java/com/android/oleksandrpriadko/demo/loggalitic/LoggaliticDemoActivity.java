package com.android.oleksandrpriadko.demo.loggalitic;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.oleksandrpriadko.demo.R;
import com.android.oleksandrpriadko.loggalitic.Loggalitic;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoggaliticDemoActivity extends AppCompatActivity {

    @BindView(R.id.editText_name) TextInputEditText mInputName;
    @BindView(R.id.editText_description) TextInputEditText mInputDescription;
    @BindView(R.id.scrollView_published_logged) ScrollView mScrollViewPublishedLogged;
    @BindView(R.id.textView_published_logged) TextView mTextViewPublishedLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggalitic_demo);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_publish_log)
    void click() {
        if (!isInputFilled(mInputName)) {
            Toast.makeText(this, "empty event name!", Toast.LENGTH_SHORT).show();
        } else {
            if (isInputFilled(mInputDescription)) {
                event(mInputName.getText().toString().replace(" ", "_"),
                    mInputDescription.getText().toString().replace(" ", "_"));
            } else {
                event(mInputName.getText().toString().replace(" ", "_"));
            }
        }
    }

    private boolean isInputFilled(TextInputEditText textInputEditText) {
        return !TextUtils.isEmpty(textInputEditText.getText());
    }

    private void event(String eventName) {
        boolean isSent = Loggalitic.publisher().event(eventName);
        String concatenated = extendLogs(eventName);
        displayLogs(isSent, concatenated);
    }

    private void event(String eventName, String description) {
        boolean isSent = Loggalitic.publisher().event(eventName, description);
        String concatenated = extendLogs(eventName, description);
        displayLogs(isSent, concatenated);
    }

    private String extendLogs(String eventName) {
        return mTextViewPublishedLogged.getText().toString().concat("\n").concat(eventName);
    }

    private String extendLogs(String eventName, String description) {
        return extendLogs(eventName).concat(", ").concat(description);
    }

    private void displayLogs(boolean isSent, String concatenated) {
        if (isSent) {
            mTextViewPublishedLogged.setText(concatenated);
            mScrollViewPublishedLogged.scrollTo(0, mScrollViewPublishedLogged.getBottom());
        } else {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }
    }
}
