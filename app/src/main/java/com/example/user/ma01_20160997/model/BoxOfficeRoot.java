package com.example.user.ma01_20160997.model;

public class BoxOfficeRoot {
    private BoxOfficeResult boxOfficeResult;

    public BoxOfficeResult getBoxOfficeResult ()
    {
        return boxOfficeResult;
    }

    public void setBoxOfficeResult (BoxOfficeResult boxOfficeResult)
    {
        this.boxOfficeResult = boxOfficeResult;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [boxOfficeResult = "+boxOfficeResult+"]";
    }
}


