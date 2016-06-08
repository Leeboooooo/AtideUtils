package com.atide.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @Package: com.atide.app
 * @Description: ${todo}
 * @author: HJC
 * @date: 2016/3/22.
 */
public class NotificationActivity extends Activity{
    private Button showBtn, stopBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_notification);

        init();
    }

    private void init(){
        showBtn = (Button) findViewById(R.id.btn_show);
        showBtn.setOnClickListener(onViewClick);

        stopBtn = (Button) findViewById(R.id.btn_stop);
        stopBtn.setOnClickListener(onViewClick);
    }

    View.OnClickListener onViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_show:
                    startService(new Intent(NotificationActivity.this, LocalService.class));
                    break;
                case R.id.btn_stop:
                    stopService(new Intent(NotificationActivity.this, LocalService.class));
                    break;
                default:
                    break;

            }
        }
    };
}
