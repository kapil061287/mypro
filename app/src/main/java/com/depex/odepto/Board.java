package com.depex.odepto;
import com.google.gson.annotations.SerializedName;
public class Board {
        @SerializedName("bg_img")
        private String imageUrl;

        @SerializedName("board_url")
        private String boardUrl;

        @SerializedName("board_key")
        private String boardKey;

        @SerializedName( "team_id")
        private String teamId;

        @SerializedName("board_visibility")
        private String boardVisibility;

        @SerializedName("BoardType")
        private String boardType1;

        @SerializedName("board_title")
        private String boardTitle;

       @SerializedName("board_id")
        private String boardId;

        @SerializedName("board_star")
        private String boardStar;

        @SerializedName("board_type2")
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