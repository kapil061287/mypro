package com.depex.odepto.recent;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;

import com.depex.odepto.BoardCard;
import com.depex.odepto.BoardList;
import com.depex.odepto.Label;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "card")
/*
,
        foreignKeys = @ForeignKey(entity = BoardList.class, parentColumns ="list_id",
                childColumns = "list_id", onDelete = ForeignKey.CASCADE))
*/
public class Card {

    @ColumnInfo(name = "list_id")
    @SerializedName("list_id")
    String list_id;

    @ColumnInfo(name="card_title")
    @SerializedName("card_title")
    String title;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "card_id")
    @SerializedName("card_id")
    String cardId;
    @ColumnInfo(name = "cardComments")
    @SerializedName("cardComments")
    String cardComments;
    @ColumnInfo(name = "del_status")
    @SerializedName("del_status")
    String delStatus;
    @ColumnInfo(name = "cover_image")
    @SerializedName("cover_image")
    String coverImage;
    @ColumnInfo(name = "total_attachment")
    @SerializedName("total_attachment")
    String totalAttachments;






    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }



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
        Card card = (Card) o;
        if (list_id != null ? !list_id.equals(card.list_id) : card.list_id != null) return false;
        if (title != null ? !title.equals(card.title) : card.title != null) return false;
        if (!cardId.equals(card.cardId)) return false;
        if (cardComments != null ? !cardComments.equals(card.cardComments) : card.cardComments != null)
            return false;
        if (delStatus != null ? !delStatus.equals(card.delStatus) : card.delStatus != null)
            return false;
        if (coverImage != null ? !coverImage.equals(card.coverImage) : card.coverImage != null)
            return false;
        return totalAttachments != null ? totalAttachments.equals(card.totalAttachments) : card.totalAttachments == null;
    }

    @Override
    public int hashCode() {
        int result = list_id != null ? list_id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + cardId.hashCode();
        result = 31 * result + (cardComments != null ? cardComments.hashCode() : 0);
        result = 31 * result + (delStatus != null ? delStatus.hashCode() : 0);
        result = 31 * result + (coverImage != null ? coverImage.hashCode() : 0);
        result = 31 * result + (totalAttachments != null ? totalAttachments.hashCode() : 0);
        return result;
    }

    public boolean hasCardCover(){
        if(this.getCoverImage()==null || this.getCoverImage().equals("")) {
            return false;
        }
        return true;
    }



    @Override
    public String toString() {
        return "Card{" +
                "title='" + title + '\'' +
                ", cardId='" + cardId + '\'' +
                ", cardComments='" + cardComments + '\'' +
                ", delStatus='" + delStatus + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", totalAttachments='" + totalAttachments + '\'' +
                '}';
    }
}