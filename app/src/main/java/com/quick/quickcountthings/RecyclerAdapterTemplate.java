package com.quick.quickcountthings;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Locale;


public class RecyclerAdapterTemplate extends RecyclerView.Adapter<RecyclerAdapterTemplate.ViewHolder> {
    Context mContext;
    Locale localeID;
    boolean clicked = false;
    private ArrayList<String> mId, mName, mDesc, mImage;

    public RecyclerAdapterTemplate(Context mContext,
                                   ArrayList<String> mId,
                                   ArrayList<String> mName,
                                   ArrayList<String> mDesc,
                                   ArrayList<String> mImage) {
        this.mId = mId;
        this.mName = mName;
        this.mDesc = mDesc;
        this.mImage = mImage;

        this.mContext = mContext;
        this.localeID = new Locale("in", "ID");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.row_list_template, parent, false);
        return new RecyclerAdapterTemplate.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tv_tmp_name.setText(mName.get(position));
        holder.tv_tmp_desc.setText(mDesc.get(position));

        holder.cv_tmp_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PickActivity.class);
                intent.putExtra("id", mId.get(position));
                intent.putExtra("nama_template", mName.get(position));
                intent.putExtra("deskripsi", mDesc.get(position));
                intent.putExtra("foto_template", mImage.get(position));
                view.getContext().startActivity(intent);
            }
        });


        Glide.with(mContext)
                .load(mImage.get(position))
                .placeholder(R.drawable.counts)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_tmp_image);

    }

    @Override
    public int getItemCount() {
        return mId.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv_tmp_card;
        ImageView iv_tmp_image;
        private TextView tv_tmp_name, tv_tmp_desc;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_tmp_image = (ImageView) itemView.findViewById(R.id.iv_tmp_image);
            tv_tmp_name = (TextView) itemView.findViewById(R.id.tv_tmp_name);
            tv_tmp_desc = (TextView) itemView.findViewById(R.id.tv_tmp_desc);
            cv_tmp_card = (CardView) itemView.findViewById(R.id.cv_tmp_card);
        }
    }
}
