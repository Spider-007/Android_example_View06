# Android_example_View06
android 相机拍照 和 图库 （涉及到数据安全的小练习）
## 其一:相机拍照
 </br>step
</br>1.创建文件 使用sd卡 的文件存储路径作为存储地址，自定义 输出文件名字;
</br>2.拍照时首先->判断文件是否存在 如果已经存在则delete->接着重新创建，保证照片是最新的;
</br>3.对android7.0以上的设备进行判断，如果大于7.0需要使用 FileProvider ，它会默认提供一个uri，类似于ContentProvider保证数据安全性;
</br>4.如果没有大于7.0，这是用Uri的 fromFile( )转为uri;
</br>5.根据返回的uri,使用intent跳转 把 uri put 出去;
</br>6.使用startActivityForResult()去请求回调-> onActivityResult()接着判断接收到的值是否正确，拿到把uri使用BitMapFactory对资源流进行解析;
</br>Q：如何对SDK进行判断？
</br>A：Build->VERSION.SDK_INT() 可以判断！
</br>
</br>