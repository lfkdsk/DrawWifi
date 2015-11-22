package com.lfk.drawwifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.lfk.drawwifi.Utils.SpUtils;
import com.lfk.drawwifi.Views.PaintView;
import com.lfk.drawwifi.Views.PathNode;
import com.lfk.drawwifi.Views.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

public class DrawActivity extends AppCompatActivity {
    private PaintView paintView;
    private PathNode pathNode;
    private BmobRealTimeData rtd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        pathNode = (PathNode) getApplication();
        paintView = (PaintView) findViewById(R.id.paint);
        paintView.setIsRecordPath(true, pathNode);
        paintView.setEditable(getIntent().getBooleanExtra("editable", false));
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
                send(paintView.PathNodeToJsonString(pathNode, ""));
                pathNode.clearList();
            }
        });

        if (!paintView.isEditable()) {
            init(getIntent().getStringExtra("key"));
        }
    }

    private void init(String ObjectId) {
        rtd = new BmobRealTimeData();
        rtd.start(this, new ValueEventListener() {
            @Override
            public void onConnectCompleted() {
                Log.e("bmob", "连接成功" + rtd.isConnected());
                if (rtd.isConnected()) {
                    rtd.subRowUpdate("Draw", ObjectId);
                }
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {
//                Log.e("get:", jsonObject.toString());
                JSONObject data = jsonObject.optJSONObject("data");
                try {
                    Log.e("get:", data.toString());
                    String message = data.getString("message");
                    paintView.ContentToPathNodeToHandle(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void send(String msg) {
        Draw draw = new Draw((String) SpUtils.get(this, "name", "lfk"), msg, true);
        draw.update(this, (String) SpUtils.get(this, "key", "000"), new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.i("update", "success");
                pathNode.clearList();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("update", "fail");
            }
        });
    }
}
