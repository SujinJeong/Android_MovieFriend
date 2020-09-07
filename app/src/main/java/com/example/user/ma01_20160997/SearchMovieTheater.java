package com.example.user.ma01_20160997;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.ma01_20160997.model.MyPlace;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class SearchMovieTheater extends AppCompatActivity {

    final static String TAG = "SearchMovieTheater";

    private final static int REQ_PERMISSIONS = 100;     // permission 확인용 코드

    private GoogleMap mGoogleMap;
    private LocationManager mLocManager;
    private Location mLastLoc;          // 위치제공자로부터 현재 수신한 위치 저장
    MarkerOptions markerOptions;
    Marker centerMarker;

    private GeoDataClient mGeoDataClient;

    ProgressDialog progressDialog;
    EditText etType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        etType = findViewById(R.id.etType);

//        permission 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    REQ_PERMISSIONS);
            return;
        }

        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //        Provider로부터 최종 수신 위치를 받음
        mLastLoc = mLocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        //        최종 수신 위치가 없을 경우 기본 위치 설정
        if (mLastLoc == null) {
            mLastLoc = new Location(LocationManager.PASSIVE_PROVIDER);
            mLastLoc.setLatitude( Double.valueOf(getResources().getString(R.string.init_latitude)) );
            mLastLoc.setLongitude( Double.valueOf(getResources().getString(R.string.init_longitude)) );
        }


//        Google map 준비
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

//        마커를 생성하기 위한 옵션 지정
        markerOptions = new MarkerOptions();
        markerOptions.title("최종 위치");
        markerOptions.snippet("정지상태");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon1));

//        위치 상세정보를 얻기 위한 Google place의 GedDataClient 준비
        mGeoDataClient = Places.getGeoDataClient(this);



//                위치 정보 수신 시작 - 3초 간격, 5m 이상 이동 시 수신
        mLocManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 5, locationListener);

    }


    public void tOnClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearch:
//                지도에 있는 기존의 Marker 삭제
                mGoogleMap.clear();

//                Google Place 정보 요청
                new NRPlaces.Builder().listener(placesListener)
                        .key(getResources().getString(R.string.google_api_key))
                        .latlng(mLastLoc.getLatitude(), mLastLoc.getLongitude())
                        .radius(500)
                        .type(convertType(etType.getText().toString()))     // 한글로 종류를 입력받았을 경우 변환
                        .build()
                        .execute();

                break;
        }
    }

    //    입력한 한글 문자열을 PlaceType 의 상수로 변환하기 위해 직접 구현할 것
    private String convertType (String type) {
        Log.i(TAG, type);
        if (type.equals(getResources().getString(R.string.type_movie))) return PlaceType.MOVIE_THEATER;
        return type;
    }


    //    Google Place API 의 결과를 처리하는 Listener
    PlacesListener placesListener = new PlacesListener() {

        @Override
        public void onPlacesFailure(PlacesException e) {
        }

        //        요청 시작 시 상태창 출력
        @Override
        public void onPlacesStart() {
            progressDialog = ProgressDialog.show(SearchMovieTheater.this, "Wait", "Searching " + etType.getText() + "...");
        }

        @Override
        public void onPlacesSuccess(final List<Place> places) {
            Log.i(TAG, "onPlaceSuccess");   // 수행상황을 확인하기 위해 log 를 찍어볼 것
            for (Place place : places) {
//                응답 결과의 place에서 확인한 place id 로 Google GeoDataClient에게 상세정보 요청
                Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(place.getPlaceId());

                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        Log.i(TAG, "onComplete");       // 수행상황을 확인하기 위해 log 를 찍어볼 것
                        if (task.isSuccessful()) {
                            // 확인이 필요할 경우 각 변수들을 log로 찍어볼 것
                            PlaceBufferResponse response = task.getResult();
                            com.google.android.gms.location.places.Place detailedPlace = response.get(0);   // 결과로 받은 Google Place의 place 객체

                            double latitude = Double.valueOf(detailedPlace.getLatLng().latitude);
                            double longitude = Double.valueOf(detailedPlace.getLatLng().longitude);

//                            개발자가 직접 정의한 MyPlace 객체에 정보 저장
                            MyPlace myPlace = new MyPlace(detailedPlace.getName().toString(), detailedPlace.getId(), latitude , longitude);
                            myPlace.setAddress(detailedPlace.getAddress().toString());
                            myPlace.setPhone(detailedPlace.getPhoneNumber().toString());
                            myPlace.setWebsiteUri(detailedPlace.getWebsiteUri().toString());


//                            Marker 생성 - 코드 가독성을 위해 메소드로 분리
                            addMarker(myPlace);

                            response.release();     // 결과 response 는 반드시 해제 수행
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
            }

        }

        //        요청 종료 시 상태창 종료
        @Override
        public void onPlacesFinished() {
            progressDialog.dismiss();
        }
    };


    private void addMarker(MyPlace place) {
        markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
        markerOptions.title(place.getName());
        markerOptions.snippet(place.getPhone());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon1));

        centerMarker = mGoogleMap.addMarker(markerOptions);
        centerMarker.setTag(place);     // Marker 에 표시하는 대상 MyPlace 를 저장 -> Marker 를 클릭할 경우 getTag() 로 place 확인
        centerMarker.showInfoWindow();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        위치 정보 수신 종료 - 위치 정보 수신 종료를 누르지 않았을 경우를 대비
        mLocManager.removeUpdates(locationListener);
    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude()), 17));

            // 지정한 위치로 마커 위치 설정
            markerOptions.position(new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude()));

            // 지도에 마커 추가 및 마커 윈도우 표시 - 윈도우 표시를 안 할 경우 마커를 터치할 때 표시됨
            centerMarker = mGoogleMap.addMarker(markerOptions);
            centerMarker.setTitle("현재위치");
            centerMarker.setSnippet("여기입니다.");
            centerMarker.showInfoWindow();

//            Marker의 window를 클릭할 경우
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    MyPlace place = (MyPlace) marker.getTag();  // Marker가 저장하고 있는 MyPlace 확인
                    Log.i(TAG, place.getName() + ": " + place.getAddress());

//                    intent 에 MyPlace 객체를 put 하여 DetailedInfoActivity 에 전달
                    Intent intent = new Intent(SearchMovieTheater.this, DetailedInfoActivity.class);
                    intent.putExtra("place", place);

                    startActivity(intent);
                }
            });
        }
    };

    /*위치 정보 수신 LocationListener*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "Current Location : " + location.getLatitude() + ", " + location.getLongitude());

//            현재 수신한 위치 정보 Location을 LatLng 형태로 변환
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

//            새로운 위치로 지도 이동
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

            // 새로운 위치로 마커의 위치 지정
            centerMarker.setPosition(currentLoc);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
        @Override
        public void onProviderEnabled(String s) {

        }
        @Override
        public void onProviderDisabled(String s) {

        }
    };

    //    permission 결과 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SearchMovieTheater.this, R.string.permission_ok_toast, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchMovieTheater.this, R.string.permission_failed_toast, Toast.LENGTH_SHORT).show();
                }
        }
    }

}
