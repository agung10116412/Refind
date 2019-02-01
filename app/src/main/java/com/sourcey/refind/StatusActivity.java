package com.sourcey.refind;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sourcey.refind.config.MySingleton;
import com.sourcey.refind.config.Url;
import com.sourcey.refind.session.UserSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StatusActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {



    private static final String TAG = HomeFragment.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    String latitude="",longtitude="";
    Geocoder geocoder;

    UserSessionManager userSessionManager;
    String usersId="";



    public EditText status;
    public ImageView kirim;
    MaterialDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);


        userSessionManager = new UserSessionManager(this);
        if (!userSessionManager.isUserLoggedIn()) {
            userSessionManager.logoutUser();
        }
        HashMap<String, String> usersDetails = userSessionManager.getUserDetails();
        usersId = usersDetails.get(UserSessionManager.KEY_ID);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }


        status = (EditText) findViewById(R.id.input_status);
        kirim = (ImageView) findViewById(R.id.send);

        dialogLoading = new MaterialDialog.Builder(StatusActivity.this)
                .autoDismiss(false)
                .cancelable(false)
                .content("Loading ...")
                .progress(true, 0)
                .build();

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                togglePeriodLocationUpdates();

            }
        });

    }

 private void sendPosting() {
                dialogLoading.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.main_url_post,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    final JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    if (status.equals("success")) {

                                    } else {

                                        Log.d("Response", response);
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
                        params.put("users_id", usersId);
                        params.put("type", "posting");
                        params.put("lat", latitude);
                        params.put("lng", longtitude);
                        params.put("postingan", status.getText().toString());
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        R.integer.limitConnection,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getmInstance(StatusActivity.this).addToRequestque(stringRequest);

        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        Log.d("Response", "Start");
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
                startLocationUpdates();
            }
        }

        Log.d("Response", "Resume");
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Log.d("Response", "Stop");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        Log.d("Response", "Pause");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("Response", "Destroy");
        mGoogleApiClient.disconnect();
    }

    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude =String.valueOf(mLastLocation.getLatitude());
            longtitude = String.valueOf(mLastLocation.getLongitude());

        } else {
            new MaterialDialog.Builder(this)
                    .title("Informasi")
                    .content("Google Play Service Tidak Tersedia , Silahkan Buka Google Maps Terlebih Dahulu Setelah Itu Buka Kembali Aplikasi Refind")
                    .positiveText("OK")
                    .cancelable(false)
                    .titleColorRes(R.color.black)
                    .contentColorRes(R.color.black)
                    .backgroundColorRes(R.color.white)
                    .positiveColor(getResources().getColor(R.color.primary))
                    .show();

        }
    }



    private void togglePeriodLocationUpdates() {
        if (!mRequestLocationUpdates) {
            mRequestLocationUpdates = true;
            startLocationUpdates();
        } else {
            mRequestLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.d("Response", "masuk");
            } else {
                new MaterialDialog.Builder(this)
                        .title("Informasi")
                        .content("Google Play Service Tidak Tersedia , Silahkan Buka Google Maps Terlebih Dahulu Setelah Itu Buka Kembali Aplikasi Refind")
                        .positiveText("OK")
                        .cancelable(false)
                        .typeface("Rubik-Regular.ttf", "Rubik-Regular.ttf")
                        .titleColorRes(R.color.black)
                        .contentColorRes(R.color.black)
                        .backgroundColorRes(R.color.white)
                        .positiveColor(getResources().getColor(R.color.primary))
                        .show();
                this.finish();
            }
            return false;
        }
        return true;
    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();

        if (mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null){
            latitude =String.valueOf(mLastLocation.getLatitude());
            longtitude = String.valueOf(mLastLocation.getLongitude());
          sendPosting();
        } else {
            displayLocation();
        }
//        togglePeriodLocationUpdates();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        new MaterialDialog.Builder(this)
                .title("Informasi")
                .content("Koneksi Failed" + connectionResult.getErrorCode())
                .positiveText("OK")
                .cancelable(false)
                .titleColorRes(R.color.black)
                .contentColorRes(R.color.black)
                .backgroundColorRes(R.color.white)
                .positiveColor(getResources().getColor(R.color.primary))
                .show();

    }

}
