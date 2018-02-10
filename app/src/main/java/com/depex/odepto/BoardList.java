package com.depex.odepto;

/**
 * Created by we on 12/14/2017.
 */

public class BoardList {

    private  String title;
    private  String id;

    private String boardId;

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

        if (getTitle() != null ? !getTitle().equals(boardList.getTitle()) : boardList.getTitle() != null)
            return false;
        if (getId() != null ? !getId().equals(boardList.getId()) : boardList.getId() != null)
            return false;
        return getBoardId() != null ? getBoardId().equals(boardList.getBoardId()) : boardList.getBoardId() == null;
    }

    @Override
    public int hashCode() {
        int result=getTitle().length()+getId().length()+getBoardId().length();
        return result;
    }

    @Override
    public String toString() {
        return getTitle()+" and "+getId();
    }
}