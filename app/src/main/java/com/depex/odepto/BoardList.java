package com.depex.odepto;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.depex.odepto.recent.Card;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BoardList {
    @ColumnInfo(name = "list_title")
    @SerializedName("list_title")
    private  String title;
    @PrimaryKey
    @NonNull
    @SerializedName("list_id")
    private  String id;

    @ColumnInfo(name = "board_id")
    @SerializedName("board_id")
    private String boardId;

    @SerializedName("cards")
    private List<Card> cards=new ArrayList<>();


    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardList boardList = (BoardList) o;

        if (title != null ? !title.equals(boardList.title) : boardList.title != null) return false;
        if (!id.equals(boardList.id)) return false;
        if (boardId != null ? !boardId.equals(boardList.boardId) : boardList.boardId != null)
            return false;
        return cards != null ? cards.equals(boardList.cards) : boardList.cards == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + id.hashCode();
        result = 31 * result + (boardId != null ? boardId.hashCode() : 0);
        result = 31 * result + (cards != null ? cards.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BoardList{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", boardId='" + boardId + '\'' +
                ", cards=" + cards +
                '}';
    }
}