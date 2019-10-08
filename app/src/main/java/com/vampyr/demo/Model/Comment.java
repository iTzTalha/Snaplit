package com.vampyr.demo.Model;

public class Comment {

    String comment;
    String publisher;
    String commentID;

    public Comment(String comment, String publisher, String commentID) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentID = commentID;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
