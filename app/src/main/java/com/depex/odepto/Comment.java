package com.depex.odepto;


import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Date;

public class Comment {


    @SerializedName("userid")
    private String usreid;
    @SerializedName("comments")
    private String comment;
    @SerializedName("username")
    private String userName;
    @SerializedName("card_id")
    private String cardId;
    @SerializedName("ckey")
    private String ckey;
    @SerializedName("date_time")
    private Date time;


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUsreid() {
        return usreid;
    }

    public void setUsreid(String usreid) {
        this.usreid = usreid;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public static class CommentComparator implements Comparator<Comment>{
        @Override
        public int compare(Comment comment, Comment t1) {
            return -1*(comment.getTime().compareTo(t1.getTime()));
        }
    }
}