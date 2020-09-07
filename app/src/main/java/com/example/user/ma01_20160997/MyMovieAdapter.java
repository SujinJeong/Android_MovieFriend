package com.example.user.ma01_20160997;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyMovieAdapter extends BaseAdapter {

    public static final String TAG = "MyMovieAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<MovieDto> list;
    private ViewHolder viewHodler = null;
    private ImageFileManager imgManager = null;

    private String imageSavedPath;

    public MyMovieAdapter(Context context, int resource, ArrayList<MovieDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        this.imgManager = new ImageFileManager(context, ImageFileManager.CACHE_IMAGE);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageSavedPath = context.getResources().getString(R.string.tmp_image_path);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MovieDto getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    // ViewHolder 는 매번 findViewById 를 수행하지 않도록 도와주는 클래스이므로 생략해도 무방

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView with position : " + position);
        View view = convertView;

        if (view == null) {
            viewHodler = new ViewHolder();
            view = inflater.inflate(layout, parent, false);
            viewHodler.tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            viewHodler.tvActor = (TextView)view.findViewById(R.id.tvActor);
            viewHodler.ivImage = (ImageView)view.findViewById(R.id.ivImage);
            view.setTag(viewHodler);
        } else {
            viewHodler = (ViewHolder)view.getTag();
        }

        MovieDto dto = list.get(position);

        viewHodler.tvTitle.setText(dto.getTitle());
        viewHodler.tvActor.setText(dto.getActor());

        String imageFileName = imgManager.getImageFileNameFromUrl(dto.getImage());

        if (imgManager.checkFileExist(imageFileName)) {
            viewHodler.ivImage.setImageBitmap(imgManager.getSavedImage(imageFileName));
        } else {
            GetImageAsyncTask task = new GetImageAsyncTask();
            try {
//                A. 비트맵을 네트워크에서 다 받을 때까지 대기한 후 진행하고자 할 경우 get() 메소드 사용
                Bitmap bitmap = task.execute(dto.getImage(), imageFileName).get();
                viewHodler.ivImage.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return view;
    }


    public void setList(ArrayList<MovieDto> list) {
        this.list = list;
    }

    public void clear() {
        this.list = new ArrayList<MovieDto>();
    }



    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvActor = null;
        public ImageView ivImage = null;
    }



    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        String imageFileName;

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = null;
            String imageAddress = params[0];
            imageFileName = params[1];

            Log.i(TAG, imageAddress);

            try {
                URL Url = new URL(imageAddress);
                URLConnection imageConn = Url.openConnection();
                imageConn.connect();

//                이미지 크기 확인
                int imageLength = imageConn.getContentLength();
//                InputStream 을 가져와 BufferedInputStream 으로 변환
                BufferedInputStream bis = new BufferedInputStream(imageConn.getInputStream(), imageLength);
//                Stream 을 Bitmap 으로 변환
                bitmap = BitmapFactory.decodeStream(bis);

                bis.close();
            } catch (FileNotFoundException e) {
//                서버에 이미지 파일이 없을 경우 리소스에 있는 기본 이미지 사용
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
//            imgManager.saveImage(bitmap, imageFileName);
//            B. 비트맵을 네트워크에서 다운받을 때를 기다리지 않고 받는 시점에 처리할 경우
            viewHodler.ivImage.setImageBitmap(bitmap);
        }
    }

}