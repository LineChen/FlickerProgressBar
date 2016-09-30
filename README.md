# FlickerProgressBar

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


#License

- - -

```java
Copyright (C) 2016 LineChen <15764230067@163.com>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```


