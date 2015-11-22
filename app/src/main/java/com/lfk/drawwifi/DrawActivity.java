package com.lfk.drawwifi;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.lfk.drawwifi.Utils.SpUtils;
import com.lfk.drawwifi.Views.PaintView;
import com.lfk.drawwifi.Views.PathNode;
import com.lfk.drawwifi.Views.UserInfo;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

public class DrawActivity extends AppCompatActivity {
    private PaintView paintView;
    private PathNode pathNode;
    private BmobRealTimeData rtd;
    private FloatingActionButton rightLowerButton;
    private FloatingActionMenu rightLowerMenu;
    private SubActionButton.Builder rLSubBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        if(getIntent().getBooleanExtra("editable", false)){
            initView();
        }
        setVisibility();
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

    private void initView() {
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();
        rLSubBuilder = new SubActionButton.Builder(this);
        ImageView pen = new ImageView(this);
        ImageView eraser = new ImageView(this);
        ImageView clean = new ImageView(this);
        ImageView back = new ImageView(this);
        back.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_back));
        pen.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_bi));
        eraser.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_xpc));
        clean.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_clear));
        rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(pen).build())
                .addSubActionView(rLSubBuilder.setContentView(eraser).build())
                .addSubActionView(rLSubBuilder.setContentView(clean).build())
                .addSubActionView(rLSubBuilder.setContentView(back).build())
                .attachTo(rightLowerButton)
                .build();
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
//                OPEN = true;
                setVisibility();
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
//                OPEN = false;
                setVisibility();
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });
        back.setOnClickListener(v -> finish());
        eraser.setOnClickListener(view -> paintView.Eraser());
        pen.setOnClickListener(view -> paintView.Paint());
        clean.setOnClickListener(v -> {
            if (!paintView.isShowing()) {
                paintView.clean();
                pathNode.clearList();
                paintView.clearReUnList();
                send("clear");
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setVisibility() {
        getWindow().getDecorView().setSystemUiVisibility
                (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
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
                JSONObject data = jsonObject.optJSONObject("data");
                try {
                    Log.e("get:", data.toString());
                    String message = data.getString("message");
                    if (message.equals("clear")) {
                        paintView.clean();
                        paintView.clearReUnList();
                        pathNode.clearList();
                    } else {
                        paintView.ContentToPathNodeToHandle(message);
                    }
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
