package com.sourcey.refind;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sourcey.refind.adapter.PostinganAdapter;
import com.sourcey.refind.config.MySingleton;
import com.sourcey.refind.config.Url;
import com.sourcey.refind.model.PostinganModel;
import com.sourcey.refind.session.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener , SwipeRefreshLayout.OnRefreshListener {



    private List<PostinganModel> postinganModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostinganAdapter postinganAdapter;

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

    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView nestedScrollView;
    boolean refreshPage = false;
    private LinearLayout pageContent, linearLoading, linearError;
    private ImageView pageImageInfo;
    private TextView pageTextInfo;
    UserSessionManager userSessionManager;
    String usersId="";
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_home, container, false);
        return fragmentLayout;
    }


    @Override
    public void onViewCreated(View fragmentLayout, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragmentLayout, savedInstanceState);

        userSessionManager = new UserSessionManager(getActivity());
        if (!userSessionManager.isUserLoggedIn()) {
            userSessionManager.logoutUser();
        }
        HashMap<String, String> usersDetails = userSessionManager.getUserDetails();
        usersId = usersDetails.get(UserSessionManager.KEY_ID);



        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

        setUpView();
        setupRecyler();

    }

    private void setUpView(){
       linearLoading = (LinearLayout) getActivity().findViewById(R.id.linearLoading);
        linearError = (LinearLayout)  getActivity().findViewById(R.id.linearError);
        pageImageInfo = (ImageView)  getActivity().findViewById(R.id.pageImageInfo);
        pageTextInfo = (TextView)  getActivity().findViewById(R.id.pageTextInfo);
        pageContent = (LinearLayout)  getActivity().findViewById(R.id.layout_content);
        swipeRefreshLayout = (SwipeRefreshLayout)  getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        nestedScrollView = (NestedScrollView)  getActivity().findViewById(R.id.nestedScroll);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recylerview_posting);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),StatusActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }

    private void setupRecyler(){

        postinganAdapter = new PostinganAdapter(postinganModelList,getActivity());
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(postinganAdapter);


    }


    private void getData(){
        if (!refreshPage) {
            linearLoading.setVisibility(View.VISIBLE);
            linearError.setVisibility(View.GONE);
            pageContent.setVisibility(View.GONE);
        }
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url.getPosting+"1&latitude="+latitude+"&longitude="+longtitude,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                postinganModelList.clear();
                                JSONArray data = response.getJSONArray("result");
                                for (int i = 0; i < data.length(); i++) {
                               PostinganModel postinganModel = new PostinganModel();
                                    JSONObject dataJson = null;
                                    try {
                                        dataJson = data.getJSONObject(i);
                                        postinganModel.setId(dataJson.getString("id"));
                                        postinganModel.setUsers_id(dataJson.getString("users_id"));
                                        postinganModel.setUsers(dataJson.getString("users"));
                                        postinganModel.setLatitude(dataJson.getString("latitude"));
                                        postinganModel.setLongitude(dataJson.getString("longitude"));
                                        postinganModel.setPostingan(dataJson.getString("postingan"));
                                        postinganModel.setGambar(dataJson.getString("gambar"));
                                        postinganModel.setTgl_buat(dataJson.getString("tgl_buat"));
                                        postinganModel.setKomentar(Integer.valueOf(dataJson.getString("komentar")));
                                        postinganModel.setSuka(Integer.valueOf(dataJson.getString("suka")));
                                        postinganModel.setLikestat(Integer.valueOf(dataJson.getString("suka_check")));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    postinganModelList.add(postinganModel);

                                }

                                postinganAdapter.notifyDataSetChanged();

                                linearLoading.setVisibility(View.GONE);
                                linearError.setVisibility(View.GONE);
                                pageContent.setVisibility(View.VISIBLE);

                            } else {
                                pageImageInfo.setImageResource(R.drawable.logo);
                                pageTextInfo.setText(response.getString("message"));
                                pageContent.setVisibility(View.GONE);
                                linearLoading.setVisibility(View.GONE);
                                linearError.setVisibility(View.VISIBLE);
                            }


                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    pageTextInfo.setText(R.string.error_timeout);
                } else if (error instanceof AuthFailureError) {
                    pageTextInfo.setText(R.string.error_server);
                } else if (error instanceof ServerError) {
                    pageTextInfo.setText(R.string.error_server);
                } else if (error instanceof NetworkError) {
                    pageTextInfo.setText(R.string.error_networkerror);
                } else if (error instanceof ParseError) {
                    pageTextInfo.setText(R.string.error_parseerror);
                }

                pageImageInfo.setImageResource(R.drawable.logo);
                pageContent.setVisibility(View.GONE);
                linearLoading.setVisibility(View.GONE);
                linearError.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                error.printStackTrace();
            }
        });
        JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                R.integer.limitConnection,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequestque(JsonObjectRequest);
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

            getData();
        } else {
            new MaterialDialog.Builder(getActivity())
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
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.d("Response", "masuk");
            } else {
                new MaterialDialog.Builder(getActivity())
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
                getActivity().finish();
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
        if (mLastLocation != null) {
           // sendLocation();
        } else {
            displayLocation();
        }
//        togglePeriodLocationUpdates();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        new MaterialDialog.Builder(getActivity())
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshPage = true;
        getData();
    }

}
