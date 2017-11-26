package com.xeris.zinj.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import com.xeris.zinj.R;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.SettingRepository;

public class LoginActivity extends Activity {

    private EditText txtPassword;
    private SettingRepository settingRepository;
    private Setting setting;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
        bar.setDisplayShowHomeEnabled(true);

        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView title = (TextView)findViewById(titleId);
        title.setTextColor(Color.WHITE);

        setTitle("Login");

        settingRepository=new SettingRepository(this);
        setting=settingRepository.getById(1);
        txtPassword=(EditText)findViewById(R.id.txtLoginPassword);

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (txtPassword.getText().toString().length()==4)
                {
                    validate();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }


    private void validate()
    {
        if (txtPassword.getText().toString().toUpperCase().equals(setting.getPassword().toUpperCase()))
        {
            Bundle extras=new Bundle();
            extras.putString("IS_LOGIN", "True");
            Intent i=new Intent(getApplicationContext(),MainActivity.class);

            i.putExtras(extras);

            startActivityForResult(i, 1);

            finish();
        }
    }

}