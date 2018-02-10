package com.depex.odepto;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "board")
public class Board {
        @ColumnInfo(name = "image_url")
        private String imageUrl;

        @ColumnInfo(name = "board_url")
        private String boardUrl;

        @ColumnInfo(name = "board_key")
        private String boardKey;

        @ColumnInfo(name = "team_id")
        private String teamId;

        @ColumnInfo(name = "board_visibility")
        private String boardVisibility;

        @ColumnInfo(name = "board_type1")
        private String boardType1;

        @ColumnInfo(name = "board_title")
        private String boardTitle;

        @ColumnInfo(name = "board_id")
        @PrimaryKey
        private String boardId;

        @ColumnInfo(name = "board_star")
        private String boardStar;

        @ColumnInfo(name = "board_type2")
        private String boardType2;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getBoardUrl() {
            return boardUrl;
        }

        public void setBoardUrl(String boardUrl) {
            this.boardUrl = boardUrl;
        }

        public String getBoardKey() {
            return boardKey;
        }

        public void setBoardKey(String boardKey) {
            this.boardKey = boardKey;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getBoardVisibility() {
            return boardVisibility;
        }

        public void setBoardVisibility(String boardVisibility) {
            this.boardVisibility = boardVisibility;
        }

        public String getBoardType1() {
            return boardType1;
        }

        public void setBoardType1(String boardType1) {
            this.boardType1 = boardType1;
        }

        public String getBoardTitle() {
            return boardTitle;
        }

        public void setBoardTitle(String boardTitle) {
            this.boardTitle = boardTitle;
        }

        public String getBoardId() {
            return boardId;
        }

        public void setBoardId(String boardId) {
            this.boardId = boardId;
        }

        public String getBoardStar() {
            return boardStar;
        }

        public void setBoardStar(String boardStar) {
            this.boardStar = boardStar;
        }

        public String getBoardType2() {
            return boardType2;
        }

        public void setBoardType2(String boardType2) {
            this.boardType2 = boardType2;
        }
}