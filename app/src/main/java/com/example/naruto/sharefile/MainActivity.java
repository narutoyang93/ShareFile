package com.example.naruto.sharefile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout rlRoot;
    private View vCover;//遮罩层
    private ShareFunction shareFunction;
    private String filePath;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        vCover = findViewById(R.id.v_cover);
    }

    /**
     * 显示PopupWindow
     */
    private void showPopupWindow() {
        if (shareFunction == null) {
            shareFunction = new ShareFunction(this, rlRoot, vCover, filePath, fileName);
        }
        shareFunction.showPopupWindow();
    }

    public void showPopupWindow(View view) {
        showPopupWindow();
    }
}
