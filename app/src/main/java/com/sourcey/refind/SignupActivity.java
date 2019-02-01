package com.sourcey.refind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.sourcey.refind.config.MySingleton;
import com.sourcey.refind.config.Url;
import com.sourcey.refind.model.PostinganModel;
import com.sourcey.refind.session.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    MaterialDialog dialog , dialogLoading;
    @BindView(R.id.input_username) EditText _usernameText;
    @BindView(R.id.input_nama) EditText _namatext;
    @BindView(R.id.radiogroup) RadioGroup _radioGroup;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    UserSessionManager sessionManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);


        sessionManager = new UserSessionManager(getApplicationContext());
        if (sessionManager.checkLoginTrue())
            finish();

        dialogLoading = new MaterialDialog.Builder(SignupActivity.this)
                .autoDismiss(false)
                .cancelable(false)
                .content("Loading ...")
                .progress(true, 0)
                .build();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    private void prosesSignUp(){
        dialogLoading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.main_url_post,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                new MaterialDialog.Builder(SignupActivity.this)
                                        .title("Informasi")
                                        .content(jsonObject.getString("message"))
                                        .positiveText("OK")
                                        .cancelable(false)
                                        .titleColorRes(R.color.black)
                                        .contentColorRes(R.color.black)
                                        .backgroundColorRes(R.color.white)
                                        .positiveColor(getResources().getColor(R.color.primary))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                                            startActivity(i);
                                                        }

                                                    }
                                        )
                                        .show();

                                _signupButton.setEnabled(true);
                            } else {
                                new MaterialDialog.Builder(SignupActivity.this)
                                        .title("Informasi")
                                        .content(jsonObject.getString("message"))
                                        .positiveText("OK")
                                        .cancelable(false)
                                        .titleColorRes(R.color.black)
                                        .contentColorRes(R.color.black)
                                        .backgroundColorRes(R.color.white)
                                        .positiveColor(getResources().getColor(R.color.primary))
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialogLoading.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", String.valueOf(error));
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                } else if (error instanceof AuthFailureError) {

                } else if (error instanceof ServerError) {

                } else if (error instanceof NetworkError) {

                } else if (error instanceof ParseError) {

                }
                dialogLoading.dismiss();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "daftar");
                params.put("jenis_kelamin", String.valueOf(_radioGroup.getCheckedRadioButtonId()));
                params.put("nama", _namatext.getText().toString());
                params.put("username", _usernameText.getText().toString());
                params.put("email", _emailText.getText().toString());
                params.put("password", _passwordText.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                R.integer.limitConnection,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(SignupActivity.this).addToRequestque(stringRequest);

    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

     prosesSignUp();
    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Pendaftaran Tidak Valid", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String nama = _namatext.getText().toString();
        String username = _usernameText.getText().toString();

        if (nama.isEmpty()) {
            _namatext.setError("Isikan Kolom Nama");
            valid = false;
        } else {
            _namatext.setError(null);
        }

        if (username.isEmpty()) {
            _usernameText.setError("Isikan Kolom Username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Masukan E-mail dengan benar");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Password minimal 4 karakter");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

       /* if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password tidak sesuai");
            valid = false;
        } else {*//*
            _reEnterPasswordText.setError(null);*/

        return valid;
    }
}