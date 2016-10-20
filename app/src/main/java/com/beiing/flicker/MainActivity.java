package com.beiing.flicker;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.beiing.flikerprogressbar.FlikerProgressBar;

public class MainActivity extends AppCompatActivity {

    FlikerProgressBar flikerProgressBar;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            flikerProgressBar.setProgress(msg.arg1);
            if(msg.arg1 == 100){
                flikerProgressBar.finishLoad();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flikerProgressBar = (FlikerProgressBar) findViewById(R.id.flikerbar);

        flikerProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flikerProgressBar.isFinish()){
                    flikerProgressBar.toggle();
                }
            }
        });

        downLoad();
    }


    public void reLoad(View view) {
        if(flikerProgressBar.isFinish()){
            // 重新加载
            flikerProgressBar.reset();
            downLoad();
        }
    }

    private void downLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        float progress = flikerProgressBar.getProgress();
                        progress  += 2;
                        Thread.sleep(200);
                        Message message = handler.obtainMessage();
                        message.arg1 = (int) progress;
                        handler.sendMessage(message);
                        if(progress == 100){
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
