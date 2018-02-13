package com.depex.odepto.adpater;

import android.content.ContentProvider;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.depex.odepto.R;

import java.util.List;

/**
 * Created by we on 2/13/2018.
 */

public class AttachFromAdapter extends BaseAdapter {




    List<AttachmentListModel> attachmentListModels;
    Context context;
    public AttachFromAdapter(List<AttachmentListModel> attachmentListModels, Context context){
            this.attachmentListModels=attachmentListModels;
            this.context=context;
    }

    @Override
    public int getCount() {
        return attachmentListModels.size();
    }

    @Override
    public Object getItem(int i) {
        return attachmentListModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return attachmentListModels.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view!=null){
            ViewHolder viewHolder= (ViewHolder) view.getTag();
            viewHolder.textView.setText(attachmentListModels.get(i).getAttachMentTypeName());
            viewHolder.imageView.setBackgroundResource(attachmentListModels.get(i).getImage_res());

        }else {
            ViewHolder viewHolder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.content_attachment_from_layout, viewGroup, false);
            viewHolder.textView=view.findViewById(R.id.attachment_from_text_view);
            viewHolder.imageView=view.findViewById(R.id.attach_from_image_view);
            viewHolder.textView.setText(attachmentListModels.get(i).getAttachMentTypeName());
            viewHolder.imageView.setBackgroundResource(attachmentListModels.get(i).getImage_res());
            view.setTag(viewHolder);
        }
        return view;
    }

    class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}