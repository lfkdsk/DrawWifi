package com.lfk.drawwifi;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.yoojia.zxing.QRCodeEncode;
import com.lfk.drawwifi.Utils.SpUtils;

public class MessageActivity extends AppCompatActivity {
    private ImageView mQRCodeImage;
    private QRCodeEncode mEncoder;
    private DecodeTask mDecodeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mQRCodeImage = (ImageView) findViewById(R.id.qrcode_image);
        final WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final Display display = manager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        final int width = displaySize.x;
        final int height = displaySize.y;
        final int dimension = width < height ? width : height;
        mEncoder = new QRCodeEncode.Builder()
                .setBackgroundColor(0xFFFFFF)
                .setCodeColor(0XFFFA5050)
                .setOutputBitmapPadding(2)
                .setOutputBitmapWidth(dimension)
                .setOutputBitmapHeight(dimension)
                .build();
        mDecodeTask = new DecodeTask();
        mDecodeTask.execute((String) SpUtils.get(this, "name", "name"));
    }

    private class DecodeTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return mEncoder.encode(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mQRCodeImage.setImageBitmap(bitmap);
        }
    }

}
