package com.example.altice.alticedoapp;

public class Article {

    //DECLARE THE VARIABLES
    private String imageUrl;
    private String title;
    private String description;
    private String location;

    //EMPTY CONSTRUCTOR FOR DATABASE
    public Article() {
    }

    //CONSTRUCTOR TO CREATE AN ARTICLE
    public Article(String imageUrl, String title, String description, String location) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.location = location;
    }

    //SETS AND GETS
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
