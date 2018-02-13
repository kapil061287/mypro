package com.depex.odepto;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by we on 1/3/2018.
 */

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

    List<Attachment> attachmentList;
    Activity activity;
    AttachmentAdapter(Activity activity, List<Attachment> attachmentList){
        this.attachmentList=attachmentList;
        this.activity=activity;
    }

    @Override
    public AttachmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.recyclerview_attachment_layout, null, false);
        return new AttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttachmentViewHolder holder, int position) {
        Attachment attachment= attachmentList.get(position);
        String url=attachment.getAttachmentUrl();
        GlideApp.with(activity).load(url).into(holder.attachmentImage);
        holder.attachmentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    public class AttachmentViewHolder extends RecyclerView.ViewHolder{

        ImageView attachmentImage;
        public AttachmentViewHolder(View itemView) {
            super(itemView);
            attachmentImage=itemView.findViewById(R.id.attachment_recyceler_img);
        }
    }
}
