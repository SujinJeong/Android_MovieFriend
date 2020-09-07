package com.example.user.ma01_20160997;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ma01_20160997.model.MyPlace;

public class DetailedInfoActivity extends AppCompatActivity {

    final static String TAG = "DetailedInfoActivity";

    MyPlace place;

    TextView etName;
    TextView etAddress;
    TextView etPhoneNumber;
    TextView etWebsiteUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_info);

        etName = (TextView) findViewById(R.id.etName);
        etAddress = (TextView) findViewById(R.id.etAddress);
        etPhoneNumber = (TextView) findViewById(R.id.etPhoneNumber);
        etWebsiteUri = (TextView) findViewById(R.id.etWebsiteUri);

//        intent가 저장하고 있는 place 객체 확인
        place = (MyPlace) getIntent().getSerializableExtra("place");

        Log.i(TAG, place.getName());
    }


    @Override
    protected void onResume() {
        super.onResume();

        etName.setText(place.getName());
        etAddress.setText(place.getAddress());
        etPhoneNumber.setText((place.getPhone().equals("") ? "정보 없음" : place.getPhone()));
        etWebsiteUri.setText(place.getWebsiteUri());
    }


    public void dOnClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnCall:
                String num = etPhoneNumber.getText().toString();

                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + num));

                    startActivity(callIntent);

                } catch (ActivityNotFoundException e) {
                    Log.e("전화걸기", "전화걸기에 실패했습니다", e);
                }
                break;
        }
    }
}