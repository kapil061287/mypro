package com.depex.odepto;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewholder> {


    List<Comment> commentList;
    Activity activity;

    CommentAdapter(List<Comment>  commentList, Activity activity){
        this.activity=activity;
        this.commentList=commentList;
    }


    @Override
    public CommentViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(activity).inflate(R.layout.comment_recyclerview_layout, parent, false);
        return new CommentViewholder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewholder holder, int position) {
        Comment comment=commentList.get(position);
        holder.comment_edit_text.setText(comment.getComment());
        holder.user_name.setText(comment.getUserName());
        String initials=Utility.getInitialsFromName(comment.getUserName());
        holder.name_initials.setText(initials);
    }




    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewholder extends RecyclerView.ViewHolder{
        EditText comment_edit_text;
        Button name_initials;
        TextView user_name;
        public CommentViewholder(View itemView) {
            super(itemView);
            comment_edit_text =itemView.findViewById(R.id.comment_edit_text);
            user_name=itemView.findViewById(R.id.user_id);
            name_initials=itemView.findViewById(R.id.name_initials);
        }
    }
}