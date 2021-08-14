## Android垃圾代码生成插件

> 该项目仅做练习用

创意来自 https://github.com/qq549631030/AndroidJunkCode

### 原理

1. 创建一个扩展androidJunkCode,让使用者可以精准地控制产生多少垃圾代码
2. 创建一个任务generate${variantName}JunkCode,将该任务添加到变体的生成资源和生成java的任务队列中
3. 随机生成普通的java类源文件,然后在该类中添加一些随机的方法
4. 随机生成Activity类源文件,然后生成该Activity对应的布局文件,然后创建清单文件,将其注册进去
5. 随机生成一些drawable
6. 随机生成一些string资源
7. 打包的时候,Gradle会将上面这些动态生成的资源整合起来,打成apk