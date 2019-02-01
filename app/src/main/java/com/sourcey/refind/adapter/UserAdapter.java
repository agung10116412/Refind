package com.sourcey.refind.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sourcey.refind.ChatActivity;
import com.sourcey.refind.CommentActivity;
import com.sourcey.refind.R;
import com.sourcey.refind.model.PostinganModel;
import com.sourcey.refind.model.UsersModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<UsersModel> usersModelList;
    Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView gender_nearby, jarak_nearby;
        public ImageView gambar_nearby;
        public RecyclerView recycle_user;

        public MyViewHolder(View view) {
            super(view);
            gender_nearby = (TextView) view.findViewById(R.id.gender_nearby);
            jarak_nearby = (TextView) view.findViewById(R.id.jarak_nearby);
            gambar_nearby = (ImageView) view.findViewById(R.id.gambar_nearby);
            recycle_user = (RecyclerView) view.findViewById(R.id.recylerview_users);

        }
    }


    public UserAdapter(List<UsersModel> usersModelList ,Context context) {
        this.usersModelList = usersModelList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_users, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UsersModel usersModel = usersModelList.get(position);
        holder.gender_nearby.setText(usersModel.getJenis_kelamin());
        holder.jarak_nearby.setText(usersModel.getJarak());
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(usersModel.getNama().charAt(0)), color);
        holder.gambar_nearby.setImageDrawable(drawable);

        holder.gambar_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ChatActivity.class);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersModelList.size();
    }
}
