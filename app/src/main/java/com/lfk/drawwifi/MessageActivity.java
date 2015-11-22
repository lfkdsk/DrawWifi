package com.lfk.drawwifi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.yoojia.zxing.QRCodeEncode;
import com.lfk.drawwifi.Utils.SpUtils;

import cn.bmob.v3.listener.SaveListener;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mQRCodeImage;
    private QRCodeEncode mEncoder;
    private DecodeTask mDecodeTask;
    private String key = null;
    private boolean open = false;

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
                .setCodeColor(0XFF3F51B5)
                .setOutputBitmapPadding(2)
                .setOutputBitmapWidth(dimension)
                .setOutputBitmapHeight(dimension)
                .build();
        mDecodeTask = new DecodeTask();
        if (SpUtils.contains(this, "name")) {
            sendMessage();
        }
        findViewById(R.id.button_start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                if (open) {
                    Intent intent = new Intent(MessageActivity.this, DrawActivity.class);
                    intent.putExtra("editable", true);
                    startActivity(intent);
                }
                break;
        }
    }

    private void sendMessage() {
        Draw draw = new Draw((String) SpUtils.get(this, "name", "ha"), "", true);
        draw.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.e("message", "success");
                Toast.makeText(MessageActivity.this, "新建行成功", Toast.LENGTH_SHORT).show();
                SpUtils.put(MessageActivity.this, "key", draw.getObjectId());
                key = draw.getObjectId();
                Log.e("key", key);
                open = true;
                if (key != null)
                    mDecodeTask.execute("key:" + key);
            }

            @Override
            public void onFailure(int i, String s) {
                if (SpUtils.contains(MessageActivity.this, "key")) {
                    mDecodeTask.execute("key:" + SpUtils.get(MessageActivity.this, "key", "defaultkey"));
                }
            }
        });
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
