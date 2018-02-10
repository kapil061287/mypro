package com.depex.odepto;

/**
 * Created by we on 1/3/2018.
 */

public class Attachment {

    private String attachmentId;
    private String cardId;
    private String userId;
    private String attachmentUrl;
    private String ckey;
    private boolean coverImage;
    private String status;

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public boolean isCoverImage() {
        return coverImage;
    }

    public void setCoverImage(boolean coverImage) {
        this.coverImage = coverImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        if (isCoverImage() != that.isCoverImage()) return false;
        if (getAttachmentId() != null ? !getAttachmentId().equals(that.getAttachmentId()) : that.getAttachmentId() != null)
            return false;
        if (getCardId() != null ? !getCardId().equals(that.getCardId()) : that.getCardId() != null)
            return false;
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null)
            return false;
        if (getAttachmentUrl() != null ? !getAttachmentUrl().equals(that.getAttachmentUrl()) : that.getAttachmentUrl() != null)
            return false;
        if (getCkey() != null ? !getCkey().equals(that.getCkey()) : that.getCkey() != null)
            return false;
        return getStatus() != null ? getStatus().equals(that.getStatus()) : that.getStatus() == null;
    }

    @Override
    public int hashCode() {
        int result =getAttachmentId().length()+getCardId().length()+getUserId().length()+getAttachmentUrl().length()
              +getCkey().length();
        if(isCoverImage())
            result=result+1;
        return result;
    }
}