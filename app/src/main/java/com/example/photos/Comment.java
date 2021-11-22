package com.example.photos;


public class Comment {

    String commentCreator, comment, photoId, userID, commentId;


    public Comment(String commentCreator, String comment, String photoId, String userID, String commentId) {
        this.commentCreator = commentCreator;
        this.comment = comment;
        this.photoId = photoId;
        this.userID = userID;
        this.commentId = commentId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment_creator='" + commentCreator + '\'' +
                ", comment='" + comment + '\'' +
                ", forumId='" + photoId + '\'' +
                ", uID='" + userID + '\'' +
                ", commentId='" + commentId + '\'' +
                '}';
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getuID() {
        return userID;
    }

    public void setuID(String uID) {
        this.userID = uID;
    }

    public String getComment_creator() {
        return commentCreator;
    }

    public void setComment_creator(String comment_creator) {
        this.commentCreator = comment_creator;
    }

    public String getInput() {
        return comment;
    }

    public void setInput(String input) {
        this.comment = input;
    }

    public String getForumId() {
        return photoId;
    }

    public void setForumId(String forumId) {
        this.photoId = forumId;
    }
}
