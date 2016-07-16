package v_go.version10.ActivityClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import v_go.version10.ApiClasses.UserApi;
import v_go.version10.R;

public class LoginNew extends AppCompatActivity{

    private EditText phone;
    private EditText password;

    private static Context context;
    public static Context getAppContext(){
        return  context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        context = getApplicationContext();

        phone = (EditText)findViewById(R.id.phone);
        password = (EditText)findViewById(R.id.password);

        // set hint color match background
        phone.setHintTextColor(Color.parseColor("#50B9AC"));

        password.setHintTextColor(Color.parseColor("#50B9AC"));
    }

    public void onLoginClicked(View view){

        boolean error = true;

        if(phone.getText().toString().trim().length() == 0) {
            phone.setError("Empty Phone Number");
            error = false;
        }
        else if (!phone.getText().toString().matches("\\d+"))
        {
            phone.setError("Invalid Phone Number");
            error = false;
        }
        if(password.getText().toString().length() == 0)
        {
            password.setError("Empty Password");
            error = false;
        }
        if(!error){
            return;
        }


        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in...");
        pDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(LoginNew.this).create();

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String phone_number = phone.getText().toString().trim();
                    String pwd = password.getText().toString();
                    if(UserApi.Login(phone_number, pwd).getString("code").equals("1"))
                    {
                        // if login succeeded save status to local and no more login next time
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("cache", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("is_logged_in", true);
                        editor.apply();

                        Intent main = new Intent(LoginNew.this, Main.class);
                        startActivity(main);
                        pDialog.dismiss();
                    }
                    else
                    {
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.setMessage("Incorrect phone number or password.");
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                                        "OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                    }

                } catch (Exception e) {
                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.setMessage("Unable to reach server.\n" + "Please check your network connection.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                                    "OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            alertDialog.show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();
    }

    public void onBackArrowClicked(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}

