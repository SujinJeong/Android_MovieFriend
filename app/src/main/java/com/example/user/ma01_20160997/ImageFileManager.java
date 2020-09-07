package com.example.user.ma01_20160997;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UnknownFormatFlagsException;

public class ImageFileManager {

    public final static String TAG = "ImageFileManager";

    public final static String SAVE_IMAGE = "SAVE";
    public final static String CACHE_IMAGE = "TEMP";


    private Context context;
    private String imageFileSavePath;
    private String saveType;


    //    생성자 - 수행 시 이미지 파일 저장 위치 지정
    public ImageFileManager(Context context, String type) throws UnknownFormatFlagsException {
        this.context = context;
        this.saveType = type;
        if (saveType.equals(SAVE_IMAGE)) {
            imageFileSavePath = context.getExternalFilesDir(null) + context.getResources().getString(R.string.my_image_path);
        } else if (saveType.equals(CACHE_IMAGE)) {
            imageFileSavePath = context.getCacheDir().getAbsolutePath() + context.getResources().getString(R.string.tmp_image_path);
        } else {
            throw new UnknownFormatFlagsException(type);
        }
        createSaveDir();
        Log.i(TAG, "imageFileSavePath: " + imageFileSavePath);

    }


    //    이미지 저장 폴더 생성
    private void createSaveDir() {
        boolean mounted = true;
        if (saveType.equals(SAVE_IMAGE)) {
//            외부 저장소 (sd card) 의 상태 확인
            String sdState = Environment.getExternalStorageState();

//            외부 저장소가 mount 상태일 경우
            if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
                mounted = false;
            }
        }

        if (mounted) {
            File saveDir = new File(imageFileSavePath);

//          생성한 이력이 없을 경우
            if (!saveDir.exists()) {
//              디렉토리 생성 - mkdir vs. mkdirs
                if (saveDir.mkdirs()) Log.i(TAG, "directory is created");
                else Log.i(TAG, "directory is not created");
            }
        }
    }



    //    이미지 저장 폴더에 동일한 파일명을 가진 파일이 있는지 검사
    public boolean checkFileExist(String fileName) {
//        경로 설정
        File checkFile = new File(imageFileSavePath, imageFileSavePath + fileName);
//        파일 유무 검사
        if (checkFile.exists()) return true;
        return false;
    }


    //    URL에서 파일명에 해당하는 부분을 추출
//    사용하는 URL에 따라 달라질 수 있으므로 사용 시 확인 필요
    public String getImageFileNameFromUrl(String url) {
        int beginIndex = url.lastIndexOf("/") + 1;
        String extractedFileName = url.substring(beginIndex);

        Log.i(TAG, extractedFileName);

        return extractedFileName;
    }


    //    bitmap 객체의 내용을 file로 저장
    public void saveImage(Bitmap bitmap, String fileName) {
//        파일 지정
        File saveFile = new File(imageFileSavePath, fileName);
        try {
//            file용 output 스트림 생성
            FileOutputStream fos = new FileOutputStream(saveFile);

//            bitmap 생성
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //    이미지 파일을 bitmap으로 읽어옴
    public Bitmap getSavedImage(String fileName) {
        String path = imageFileSavePath + "/" + fileName;
        Log.i(TAG, path);

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }


    //    이미지 디렉토리 안에 있는 모든 파일 삭제
    public void removeAllImages() {
        String path = imageFileSavePath;
        Log.i(TAG, path);

        File files = new File (path);
//        디렉토리 내부의 모든 파일을 가져옴
        File[] images = files.listFiles();

        for (File image : images) {
            image.delete();
        }
    }

}