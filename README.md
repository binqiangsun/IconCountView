# IconCountView
该项目是社区类APP点赞，收藏等按钮操作后的数字动画效果的View

#### 效果
![](http://wx1.sinaimg.cn/mw1024/7b3eaa29gy1fkmpwbyrgcg20a006o4gc.gif)


#### 2017.10.23更新
为了增强实际的应用性， 增加了当数量为0的时候， 显示文字的功能，效果如下：
![](http://wx2.sinaimg.cn/mw690/7b3eaa29gy1fksifgls9xg20a006o1ky.gif)

#### 2017.10.25更新
增加文字颜色的设置：
![](http://wx2.sinaimg.cn/mw690/7b3eaa29gy1fkux3v9le3g20a006o7wi.gif)

#### 2017.10.30更新
分离Model，View层，应用MVP架构

### 配置
gradle
```
implementation 'com.sunbq:iconcountview:1.0.1'
```
xml
```
<com.sunbinqiang.iconcountview.IconCountView
        android:id="@+id/praise_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:count="123"
        app:state="false"
        app:zeroText="赞"
        app:textNormalColor="#8a8a8a"
        app:textSelectedColor="#d4237a"
        app:textSize="12sp"
        app:normalRes="@drawable/icon_praise_normal"
        app:selectedRes="@drawable/icon_praise_selected"/>
```
