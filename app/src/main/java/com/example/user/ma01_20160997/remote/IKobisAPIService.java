package com.example.user.ma01_20160997.remote;

import com.example.user.ma01_20160997.model.BoxOfficeRoot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IKobisAPIService {
//    URL 에서 path 일부와 file 명을 함께 기록할 경우
//    기본 주소: http://www.kobis.or.kr/
    @GET ("/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.{type}")
    Call<BoxOfficeRoot> getDailyBoxOfficeResult(@Path("type") String type, @Query("key") String key, @Query("targetDt") String targetDate);

//    URL 에서 파일명 부분만을 기록할 경우
//    기본 주소: http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/
//    @GET ("/searchDailyBoxOfficeList.{type}")
//    Call<BoxOfficeRoot> getDailyBoxOfficeResult(@Path("type") String type, @Query("key") String key, @Query("targetDt") String targetDate);
}

