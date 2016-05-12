package com.framgia.fel1.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by PhongTran on 04/25/2016.
 */
public class ShowImage extends AsyncTask<String, Void, Bitmap> {
    ImageView mImageView;
    boolean hasError = false;

    public ShowImage(ImageView imageView) {
        this.mImageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        if (url == null) {
            return bitmap;
        }
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            hasError = true;
            bitmap = BitmapUtil.decodeSampledBitmapFromFile(url, 100, 100);
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
//        if (!hasError) {
        if(result != null){
            mImageView.setImageBitmap(result);
        }
    }
}
