# WKIRO_OpenALPR
WKIRO - System rozpoznawania tablic rejestracyjnych oparty o OpenALPR

## Used libs
* OpenALPR - for plates recognition (native C++ library) ([about](https://github.com/openalpr/openalpr/wiki/OpenALPR-Design))
* Xuggler - for video processing ([tutorial1](https://www.javacodegeeks.com/2011/02/xuggler-tutorial-frames-capture-video.html)) ([tutorial2](https://www.javacodegeeks.com/2011/02/xuggler-tutorial-transcoding-media.html))
* Google GSON - for JSON processing ([about](https://github.com/google/gson))

## Before installation
* [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/)

## Import project to IntelliJ in Windows 
1. File -> New -> Project from existing sources 
2. Select directory with sources
3. Edit configuration
![alt tag](http://i.imgur.com/ORtiQDQ.png)
4. Add directory with OpenALPR native dll library to system PATH variable
![alt tag](http://i.imgur.com/faXgLwI.png)
5. Type: "Control + Alt + Shift + S", 'Project structure' window should be displayed
6. In dependency tab, add required libs:
![alt tag](http://i.imgur.com/DlykxMl.png)
7. Build/Run application

## User interface
![alt tag](http://i.imgur.com/3JF52Dd.png)
