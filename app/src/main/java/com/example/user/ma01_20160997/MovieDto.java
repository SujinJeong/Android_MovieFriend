package com.example.user.ma01_20160997;

public class MovieDto {

    private long id;
    private String title;
    private String director;
    private String actor;
    private String image;

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        //       Spanned spanned = Html.fromHtml(title);
        //       return spanned.toString();
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }

    @Override
    public String toString() {
        return "MovieDto{" +
                "_id=" + id +
                ", title='" + title + '\'' +
                ", actor='" + actor + '\'' +
                ", director='" + director + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

}