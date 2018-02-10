package com.depex.odepto;



public class Label {

    private  String color;
    private String name;
    private String id;
    private String cardId;
    private String status;
    private String ckey;


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

        if (getColor() != null ? !getColor().equals(label.getColor()) : label.getColor() != null)
            return false;
        if (getName() != null ? !getName().equals(label.getName()) : label.getName() != null)
            return false;
        if (getId() != null ? !getId().equals(label.getId()) : label.getId() != null) return false;
        if (getCardId() != null ? !getCardId().equals(label.getCardId()) : label.getCardId() != null)
            return false;
        if (getStatus() != null ? !getStatus().equals(label.getStatus()) : label.getStatus() != null)
            return false;
        return getCkey() != null ? getCkey().equals(label.getCkey()) : label.getCkey() == null;
    }

    @Override
    public int hashCode() {
        int result = getColor() != null ? getColor().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getCardId() != null ? getCardId().hashCode() : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getCkey() != null ? getCkey().hashCode() : 0);
        return result;
    }
}