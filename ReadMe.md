# 360 自动多渠道加固插件
## 使用
 + 首先添加此模块到项目,然后执行
    publishJiaGuPluginPublicationsToMavenLocal Task
 项目build.gradle文件添加classpath
 ```groovy
   repositories {
        google()
        jcenter()
        mavenLocal()
        maven {
            url uri("JiaGuPlugin/repo")
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.3'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
       // classpath 'com.mml.jiagu.plugin:360:1.0.0'
        classpath 'com.mml.jiagu.plugin:360:1.1'

   }

```  
然后在app目录下build.gradle中加
apply from: "BaseWheelsByKotlin/JiaGuPlugin/jiagu.gradle"
 或者
```groovy 
apply plugin: 'com.mml.jiagu.plugin'
String os = "${project.rootDir.absolutePath}/JiaGuPlugin/tools/${getJiaguJarFilePathByOs()}"
jiagu {
    jiaGuToolsPath "${os}/jiagu.jar"
    username " "
    password " "
    keyStoreFile android.signingConfigs.release.storeFile.absolutePath
    keyStorePassword android.signingConfigs.release.storePassword
    keyAlias android.signingConfigs.release.keyAlias
    keyAliasPassword android.signingConfigs.release.keyPassword
    inputFile //"${buildDir.getAbsolutePath()}\\outputs\\apk\\release\\app-release.apk"
    outputFile //"${buildDir.getAbsolutePath()}\\jiagu" //这里指定的是输出文件夹
    config '-so', '-data', '-assets', '-string_obfus', '-so_private'
    channelFile "${os}/多渠道模板.txt"
}

String getJiaguJarFilePathByOs(){
    String osName = org.gradle.internal.os.OperatingSystem.current().getName();
    String osVersion = org.gradle.internal.os.OperatingSystem.current().getVersion();
    println "*** $osName $osVersion was detected."

    if (org.gradle.internal.os.OperatingSystem.current().isLinux()) {
        println("Linux")
        return "linux"+File.separator+"jiagu"
    } else if (org.gradle.internal.os.OperatingSystem.current().isWindows()) {
        println("Windows")
        return "windows"+File.separator+"jiagu"
    } else if (org.gradle.internal.os.OperatingSystem.current().isMacOsX()) {
        println("OSX")
        return "mac"+File.separator+"jiagu"
    } 
}

```  

最后打包以后执行jiagu task 