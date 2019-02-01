package com.sourcey.refind.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
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
import com.sourcey.refind.CommentActivity;
import com.sourcey.refind.LoginActivity;
import com.sourcey.refind.MainActivity;
import com.sourcey.refind.R;
import com.sourcey.refind.config.MySingleton;
import com.sourcey.refind.config.Url;
import com.sourcey.refind.model.PostinganModel;
import com.sourcey.refind.session.UserSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostinganAdapter extends RecyclerView.Adapter<PostinganAdapter.MyViewHolder> {

    private List<PostinganModel> postinganModelList;
    Context mContext;
    MaterialDialog dialogLoading;

    UserSessionManager userSessionManager;
    String usersId="";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status, qty_suka, qty_komen;
        public ImageView suka, komen, profile;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            status = (TextView) view.findViewById(R.id.status);
            qty_suka = (TextView) view.findViewById(R.id.qty_suka);
            qty_komen = (TextView) view.findViewById(R.id.qty_komen);
            suka = (ImageView) view.findViewById(R.id.like);
            komen = (ImageView) view.findViewById(R.id.komen);
            profile = (ImageView) view.findViewById(R.id.profile);

        }
    }


    public PostinganAdapter(List<PostinganModel> pengaturanModelList ,Context context) {
        this.postinganModelList = pengaturanModelList;
        this.mContext = context;



        userSessionManager = new UserSessionManager(mContext);
        if (!userSessionManager.isUserLoggedIn()) {
            userSessionManager.logoutUser();
        }
        HashMap<String, String> usersDetails = userSessionManager.getUserDetails();
        usersId = usersDetails.get(UserSessionManager.KEY_ID);

        dialogLoading = new MaterialDialog.Builder(mContext)
                .autoDismiss(false)
                .cancelable(false)
                .content("Loading ...")
                .progress(true, 0)
                .build();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_posting, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PostinganModel postinganModel = postinganModelList.get(position);
        holder.name.setText(postinganModel.getUsers());
        holder.status.setText(postinganModel.getPostingan());
        holder.komen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CommentActivity.class);
                mContext.startActivity(intent);
            }
        });

        if (postinganModel.getLikestat()== 1){
            holder.suka.setImageResource(R.drawable.like);
        }else {
            holder.suka.setImageResource(R.drawable.like_kosong);
        }

      holder.qty_suka.setText(String.valueOf(postinganModel.getSuka()));
        holder.qty_komen.setText(String.valueOf(postinganModel.getKomentar()));


        holder.suka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.main_url_post,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    final JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    if (status.equals("success")) {
                                        if (postinganModel.getLikestat()==1){
                                            postinganModel.setLikestat(0);
                                            holder.suka.setImageResource(R.drawable.like_kosong);
                                            postinganModel.setSuka(Integer.valueOf(postinganModel.getSuka())-1);
                                            holder.qty_suka.setText(String.valueOf(postinganModel.getSuka()));
                                        }else {
                                            postinganModel.setLikestat(1);
                                            holder.suka.setImageResource(R.drawable.like);
                                            postinganModel.setSuka(Integer.valueOf(postinganModel.getSuka())+1);
                                           holder.qty_suka.setText(String.valueOf(postinganModel.getSuka()));
                                        }
                                    } else {

                                        Log.d("Response", response);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id",postinganModel.getId() );
                        params.put("users_id", usersId);
                        params.put("type","like" );
                        Log.d("Response", String.valueOf(params));
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        R.integer.limitConnection,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getmInstance(mContext).addToRequestque(stringRequest);


            }
        });
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(postinganModel.getUsers().charAt(0)), color);
        holder.profile.setImageDrawable(drawable);
        /*holder.suka.setSuka(postinganModel.getSuka());
        holder.profile.setGambar(postinganModel.getGambar());
        holder.komen.setKomentar(postinganModel.getKomentar());*/

    }

    @Override
    public int getItemCount() {
        return postinganModelList.size();
    }
}
