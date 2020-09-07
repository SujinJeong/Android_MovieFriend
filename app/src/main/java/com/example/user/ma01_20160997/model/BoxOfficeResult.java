package com.example.user.ma01_20160997.model;

import java.util.List;

public class BoxOfficeResult {

    private String showRange;

//    배열 형태를 필요에 따라 List<E> 로 변경
//    private DailyBoxOfficeList[] dailyBoxOfficeList;
    private List<DailyBoxOfficeList> dailyBoxOfficeList;

    private String boxofficeType;

    public String getShowRange ()
    {
        return showRange;
    }

    public void setShowRange (String showRange)
    {
        this.showRange = showRange;
    }

    public List<DailyBoxOfficeList> getDailyBoxOfficeList() {
        return dailyBoxOfficeList;
    }

    public void setDailyBoxOfficeList(List<DailyBoxOfficeList> dailyBoxOfficeList) {
        this.dailyBoxOfficeList = dailyBoxOfficeList;
    }


//    public DailyBoxOfficeList[] getDailyBoxOfficeList ()
//    {
//        return dailyBoxOfficeList;
//    }
//
//    public void setDailyBoxOfficeList (DailyBoxOfficeList[] dailyBoxOfficeList)
//    {
//        this.dailyBoxOfficeList = dailyBoxOfficeList;
//    }

    public String getBoxofficeType ()
    {
        return boxofficeType;
    }

    public void setBoxofficeType (String boxofficeType)
    {
        this.boxofficeType = boxofficeType;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [showRange = "+showRange+", dailyBoxOfficeList = "+ dailyBoxOfficeList +", boxofficeType = "+boxofficeType+"]";
    }
}
