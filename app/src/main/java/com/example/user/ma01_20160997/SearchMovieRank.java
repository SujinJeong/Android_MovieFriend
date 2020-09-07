package com.example.user.ma01_20160997;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.ma01_20160997.model.BoxOfficeRoot;
import com.example.user.ma01_20160997.model.DailyBoxOfficeList;
import com.example.user.ma01_20160997.remote.IKobisAPIService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchMovieRank extends AppCompatActivity {

    final static String TAG = "MainActivity";

    EditText editText;
    TextView tvResult;

    String apiUrl;
    String apiKey;

    // Retrofit 서비스 객체

    private Retrofit retrofit = null;

    private IKobisAPIService mRetrofitService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        editText = (EditText) findViewById(R.id.etDate);
        tvResult = (TextView) findViewById(R.id.tvResult);

        apiUrl = getResources().getString(R.string.api_movierank_url);
        apiKey = getResources().getString(R.string.kobis_key);


        // Retrofit 객체 생성
        if (retrofit == null) {
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(apiUrl)        // apiUrl 은 반드시 / 로 끝나는 부분만으로 구성 (domain 또는 path 로만 구성)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Retrofit 객체를 사용하여 사용자가 지정한 인터페이스를 구현한 서비스 생성
        mRetrofitService = retrofit.create(IKobisAPIService.class);


    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String targetDate = editText.getText().toString();

                // Retrofit 서비스로 구현한 인터페이스 객체를 통해 서버호출객체(Call) 생성
                Call<BoxOfficeRoot> apiCall = mRetrofitService.getDailyBoxOfficeResult("json", apiKey, targetDate);

                // 서버호출객체를 사용하여 서버 비동기(enqueue)로 요청, 결과를 수신하면 apiCallback 의 메소드 수행
                apiCall.enqueue(apiCallback);

                break;
        }
    }

    // API의 요청 결과 처리 인터페이스 객체
    Callback<BoxOfficeRoot> apiCallback = new Callback<BoxOfficeRoot>() {
        @Override
        public void onResponse(Call<BoxOfficeRoot> call, Response<BoxOfficeRoot> response) {
            if (response.isSuccessful()) {
                StringBuffer result = new StringBuffer();

                // POJO 클래스 BoxOfficeResult 의 멤버변수 dailyBoxOfficeList 를 배열형에서 List 형으로 교체하였을 경우
                ArrayList<DailyBoxOfficeList> list = (ArrayList<DailyBoxOfficeList>) response.body().getBoxOfficeResult().getDailyBoxOfficeList();

                for (DailyBoxOfficeList item : list) {
                    result.append(item);    // item의 toString() 호출
                    result.append("\n");
                }
                tvResult.setText(result.toString());
            }
        }

        // 서버의 응답에 이상이 있을 경우 수행
        @Override
        public void onFailure(Call<BoxOfficeRoot> call, Throwable t) {
            Log.e(TAG, "failure");
            Log.e(TAG, t.getMessage());
        }
    };
}