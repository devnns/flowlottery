package com.example.devnn.flowlottery;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

/**
 * create by devnn
 * email:devnn@devnn.com
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnControl;
    private LukyDrawView lukyDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        lukyDrawView = (LukyDrawView) findViewById(R.id.lukydraw);
        btnControl = (Button) findViewById(R.id.control);
        btnControl.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (btnControl.getText().equals("开始")) {
            btnControl.setText("停止");
            lukyDrawView.start();
        } else if (btnControl.getText().equals("停止")) {
            btnControl.setText("开始");
            lukyDrawView.stop();
        }
    }
}
