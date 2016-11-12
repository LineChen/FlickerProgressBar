package com.beiing.flicker;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beiing.flikerprogressbar.FlikerProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , Runnable{

    FlikerProgressBar flikerProgressBar;
    FlikerProgressBar roundProgressbar;

    Thread downLoadThread;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            flikerProgressBar.setProgress(msg.arg1);
            roundProgressbar.setProgress(msg.arg1);
            if(msg.arg1 == 100){
                flikerProgressBar.finishLoad();
                roundProgressbar.finishLoad();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flikerProgressBar = (FlikerProgressBar) findViewById(R.id.flikerbar);
        roundProgressbar = (FlikerProgressBar) findViewById(R.id.round_flikerbar);

        flikerProgressBar.setOnClickListener(this);
        roundProgressbar.setOnClickListener(this);

        downLoad();
    }


    public void reLoad(View view) {

        downLoadThread.interrupt();
            // 重新加载
        flikerProgressBar.reset();
        roundProgressbar.reset();

        downLoad();
    }

    private void downLoad() {
        downLoadThread = new Thread(this);
        downLoadThread.start();
    }

    @Override
    public void onClick(View v) {
        if(!flikerProgressBar.isFinish()){
            flikerProgressBar.toggle();
            roundProgressbar.toggle();

            if(flikerProgressBar.isStop()){
                downLoadThread.interrupt();
            } else {
                downLoad();
            }

        }
    }

    @Override
    public void run() {
        try {
            while( ! downLoadThread.isInterrupted()){
                float progress = flikerProgressBar.getProgress();
                progress  += 2;
                Thread.sleep(200);
                Message message = handler.obtainMessage();
                message.arg1 = (int) progress;
                handler.sendMessage(message);
                if(progress == 100){
                    break;
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
