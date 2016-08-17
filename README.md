# Spectrum- COMP3320 Game


[Dropbox link for documentation](https://www.dropbox.com/home/COMP3320)

---
### Resetting local repository

Because of feedback from the labs we won't be using libGDX anymore.
To reset your repository:
Delete the locally stored files in your folder
do the following commands
``` 
git reset --hard HEAD
git pull https://github.com/pratN/COMP3320/
```
---
## Configuring the project
You need JDK version 8+ to be able to use LWJGL3
+ Download and extract LWJGL3 [here](https://www.lwjgl.org/download)
+ Compile the project

If it doesn't compile add the LWJGL jars as dependencies
+ Go to file> project structure
+ In the window that pops up selec modules from the panel on the left
+ Select Spectrum from the module list on the right and go to the Dependencies tab
+ Click on the + button and select JARs or directories
+ Navigate to the lib folder and select lwjgl.jar

If it compiles correctly you should be greeted with a purple window.
---
### Resources

Tutorials for game development with LWJGL 3 with code snippets can be found [HERE](https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/)

Playlist for game development with LWJGL can be found [HERE](https://www.youtube.com/watch?v=VS8wlS9hF8E&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP)
