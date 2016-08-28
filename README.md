# FlikerProgressBar

Android 仿应用宝下载进度条 

### 自定义属性

- loadingColor 下载中颜色
- stopColor 暂停时颜色
- textSize 进度文本字体大小

### 效果图

![pic](https://github.com/LineChen/FlikerProgressBar/blob/master/screenshot/screenshot.gif)

### 使用

#### 布局

```java

<com.beiing.flikerprogressbar.FlikerProgressBar
        android:id="@+id/flikerbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:textSize="12sp"
        app:loadingColor="#40c4ff"
        app:stopColor="#ff9800"/>

```


#### 测试下载

```java

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
    
     private void downLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(200);
                        Message message = handler.obtainMessage();
                        message.arg1 = i + 1
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
}

```







