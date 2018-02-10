package com.depex.odepto;

/**
 * Created by we on 1/2/2018.
 */

public class Comment {


    private String usreid;
    private String comment;
    private String userName;
    private String cardId;
    private String ckey;


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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment1 = (Comment) o;

        if (getUsreid() != null ? !getUsreid().equals(comment1.getUsreid()) : comment1.getUsreid() != null)
            return false;
        if (getComment() != null ? !getComment().equals(comment1.getComment()) : comment1.getComment() != null)
            return false;
        if (getUserName() != null ? !getUserName().equals(comment1.getUserName()) : comment1.getUserName() != null)
            return false;
        if (getCardId() != null ? !getCardId().equals(comment1.getCardId()) : comment1.getCardId() != null)
            return false;
        return getCkey() != null ? getCkey().equals(comment1.getCkey()) : comment1.getCkey() == null;
    }

    @Override
    public int hashCode() {
        int result = getComment().length()+getCardId().length()+getCkey().length()+getUserName().length()+getUsreid().length();
        return result;
    }
}
