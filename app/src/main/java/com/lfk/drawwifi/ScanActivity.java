package com.lfk.drawwifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.yoojia.zxing.FinderView;
import com.github.yoojia.zxing.QRCodeScanSupport;

public class ScanActivity extends AppCompatActivity {
    private FinderView finderView;
    private QRCodeScanSupport mQRCodeScanSupport;

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
        mQRCodeScanSupport.setOnScanResultListener(new QRCodeScanSupport.OnScanResultListener() {
            @Override
            public void onScanResult(String notNullResult) {
                Toast.makeText(ScanActivity.this, "扫描结果: " + notNullResult, Toast.LENGTH_SHORT).show();
            }
        });


    }


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
}
