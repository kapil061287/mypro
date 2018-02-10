package com.depex.odepto;


public class BoardCard {

    String title;
    String cardId;
    String cardComments;
    String delStatus;
    String coverImage;
    String totalAttachments;





    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTotalAttachments() {
        return totalAttachments;
    }

    public void setTotalAttachments(String totalAttachments) {
        this.totalAttachments = totalAttachments;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardComments() {
        return cardComments;
    }

    public void setCardComments(String cardComments) {
        this.cardComments = cardComments;
    }

    public String getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(String delStatus) {
        this.delStatus = delStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardCard card = (BoardCard) o;

        if (getTitle() != null ? !getTitle().equals(card.getTitle()) : card.getTitle() != null)
            return false;
        if (getCardId() != null ? !getCardId().equals(card.getCardId()) : card.getCardId() != null)
            return false;
        if (getCardComments() != null ? !getCardComments().equals(card.getCardComments()) : card.getCardComments() != null)
            return false;
        if (getDelStatus() != null ? !getDelStatus().equals(card.getDelStatus()) : card.getDelStatus() != null)
            return false;
        if (getCoverImage() != null ? !getCoverImage().equals(card.getCoverImage()) : card.getCoverImage() != null)
            return false;
        return getTotalAttachments() != null ? getTotalAttachments().equals(card.getTotalAttachments()) : card.getTotalAttachments() == null;
    }

    @Override
    public int hashCode() {
        int result =getCardComments().length()+getCardId().length()+getDelStatus().length()+getTitle().length()
                +getTotalAttachments().length()+getCoverImage().length();
        return result;
    }
}
