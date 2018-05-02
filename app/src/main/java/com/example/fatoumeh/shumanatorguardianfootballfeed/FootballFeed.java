package com.example.fatoumeh.shumanatorguardianfootballfeed;

/**
 * Created by fatoumeh on 01/03/2018.
 */

public class FootballFeed {
    private String section;
    private String headline;
    private String author;
    private String date;
    private String url;

    public FootballFeed(String section, String headline, String author, String date, String url) {
        this.section = section;
        this.headline = headline;
        this.author = author;
        this.date = date;
        this.url = url;
    }

    public String getSection() {
        return section;
    }

    public String getHeadline() {
        return headline;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
