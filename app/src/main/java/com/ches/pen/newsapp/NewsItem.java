package com.ches.pen.newsapp;

//Object representing a single news story
public class NewsItem {
    private String mHeadline;
    private String mCategory;
    private String mAuthor;
    private String mLink;
    private String mDateTime;


    public NewsItem(String headline, String category, String author, String link, String dateTime) {
        mHeadline = headline;
        mCategory = category;
        mAuthor = author;
        mLink = link;
        mDateTime = dateTime;

    }


    public String getHeadline() {
        return mHeadline;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getLink() {
        return mLink;
    }

    public String getDateTime() {
        return mDateTime;
    }

}
