package com.depex.odepto;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewholder> {


    List<Comment> commentList;
    Activity activity;

    CommentAdapter(List<Comment>  commentList, Activity activity){
        this.activity=activity;
        Collections.sort(commentList, new Comment.CommentComparator());
        this.commentList=commentList;
    }

    public void addItem(Comment comment){
        commentList.add(0, comment);
        notifyItemInserted(0);
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
        Date date=comment.getTime();
        String time=updateTimer(date.getTime());
        holder.dateTimeComment.setText(time);
    }




    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewholder extends RecyclerView.ViewHolder{
        @BindView(R.id.comment_edit_text)
        TextView comment_edit_text;
        @BindView(R.id.name_initials)
        Button name_initials;
        @BindView(R.id.user_id)
        TextView user_name;
        @BindView(R.id.date_time_comment)
        TextView dateTimeComment;
        public CommentViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public String updateTimer(long millis){

        String str=new String();
        long currentTimeMillis=System.currentTimeMillis();
        long time=currentTimeMillis-millis;
        long seconds=time/1000;

        long hours=0;
        long minuts=0;
        long seconds2=0;

        long remainder=0;
        long days=seconds/(24*60*60);
        remainder=seconds%(24*60*60);
        hours=remainder/(60*60);
        remainder=remainder%(60*60);
        minuts=remainder/60;
        remainder=remainder%60;
        seconds2=remainder;

        if(days==0){
            if(hours==0){
                if(minuts==0){
                    str="Just Now";
                }else {
                    str=minuts+" min. ago";
                }
            }else {
                if(hours>1){
                    str=hours+" hours ago";
                }else {
                    str=hours+" hour ago";
                }
            }
        }else {
            if(days>1){
                str=days+" days ago";
            }else {
                str=days+" day ago";
            }
        }
       return str;
    }
}