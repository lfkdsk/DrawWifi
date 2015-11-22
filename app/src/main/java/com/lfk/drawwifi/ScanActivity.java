package com.lfk.drawwifi;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.yoojia.zxing.FinderView;
import com.github.yoojia.zxing.QRCodeDecode;
import com.github.yoojia.zxing.QRCodeScanSupport;

import java.io.FileNotFoundException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {
    private FinderView finderView;
    private QRCodeScanSupport mQRCodeScanSupport;
    private boolean open = true;
    private static int SELECT_PICTURE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan);
        finderView = (FinderView) findViewById(R.id.capture_viewfinder_view);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view);
        mQRCodeScanSupport = new QRCodeScanSupport(surfaceView, finderView);
        // 如何处理扫描结果
        mQRCodeScanSupport.setOnScanResultListener(notNullResult -> {
            Toast.makeText(ScanActivity.this, "扫描结果: " + notNullResult, Toast.LENGTH_SHORT).show();
            dotheResult(notNullResult);
        });
        findViewById(R.id.scan_pic).setOnClickListener(this);
    }

    private void dotheResult(String notNullResult) {
        if (notNullResult.substring(0, 4).equals("key:") && open) {
            BmobQuery<Draw> bmobQuery = new BmobQuery<>();
            bmobQuery.getObject(this, notNullResult.substring(4), new GetListener<Draw>() {
                @Override
                public void onSuccess(Draw draw) {
                    Message message = handler.obtainMessage();
                    message.obj = notNullResult.substring(4);
                    handler.sendMessage(message);
                    open = false;
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }


    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(ScanActivity.this, DrawActivity.class);
            intent.putExtra("key", msg.obj.toString());
            intent.putExtra("editable", false);
            startActivity(intent);
        }
    };


    @Override
    protected void onResume() {
        mQRCodeScanSupport.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mQRCodeScanSupport.onPause(this);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_pic:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                //选择图片
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    QRCodeDecode decoder = new QRCodeDecode.Builder().build();
                    if (decoder.decode(bitmap) != null) {
                        Toast.makeText(ScanActivity.this, "扫描结果：" + decoder.decode(bitmap), Toast.LENGTH_SHORT).show();
                        dotheResult(decoder.decode(bitmap));
                    } else
                        Toast.makeText(ScanActivity.this, "该图片无法解析", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("the bmp toString: " + bitmap);
            } else {
                Toast.makeText(ScanActivity.this, "请重新选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
