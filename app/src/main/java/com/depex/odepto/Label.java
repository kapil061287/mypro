package com.depex.odepto;


import com.google.gson.annotations.SerializedName;

public class Label {

    @SerializedName("labels")
    private  String color;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("cardid")
    private String cardId;
    @SerializedName("userid")
    private String userid;
    @SerializedName("status")
    private String status;
    @SerializedName("ckey")
    private String ckey;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (color != null ? !color.equals(label.color) : label.color != null) return false;
        if (name != null ? !name.equals(label.name) : label.name != null) return false;
        if (id != null ? !id.equals(label.id) : label.id != null) return false;
        if (cardId != null ? !cardId.equals(label.cardId) : label.cardId != null) return false;
        if (userid != null ? !userid.equals(label.userid) : label.userid != null) return false;
        if (status != null ? !status.equals(label.status) : label.status != null) return false;
        return ckey != null ? ckey.equals(label.ckey) : label.ckey == null;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (cardId != null ? cardId.hashCode() : 0);
        result = 31 * result + (userid != null ? userid.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (ckey != null ? ckey.hashCode() : 0);
        return result;
    }
}