package com.lfk.drawwifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.lfk.drawwifi.Views.PaintView;
import com.lfk.drawwifi.Views.PathNode;
import com.lfk.drawwifi.Views.UserInfo;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {
    private List<Draw> list = new ArrayList<>();
    private PaintView paintView;
    private PathNode pathNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        pathNode = (PathNode) getApplication();
        paintView = (PaintView) findViewById(R.id.paint);
        paintView.setIsRecordPath(true, pathNode);
        paintView.setOnPathListener((x, y, event, IsPaint) -> {
            PathNode.Node temp_node = pathNode.new Node();
            temp_node.x = paintView.px2dip(x);
            temp_node.y = paintView.px2dip(y);
            if (IsPaint) {
                temp_node.PenColor = UserInfo.PaintColor;
                temp_node.PenWidth = UserInfo.PaintWidth;
            } else {
                temp_node.EraserWidth = UserInfo.EraserWidth;
            }
            temp_node.IsPaint = IsPaint;
            temp_node.TouchEvent = event;
            temp_node.time = System.currentTimeMillis();
            pathNode.addNode(temp_node);
            if (event == MotionEvent.ACTION_UP) {
                String s = paintView.PathNodeToJsonString(pathNode, "");
                send(s);
            }
        });


    }

    private void init() {
        Bmob.initialize(this, "d76a19ffd74a3ebf8d346c6eeacc94d6");
    }

    private void send(String msg) {
        Draw draw = new Draw("lfk", "hh", msg);
        draw.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.e("message", "success");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
}
