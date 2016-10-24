# Spectrum- COMP3320 Game

If any issues arise with the project, do the following.
This guide assumes use of intelliJ Idea as an IDE.
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
## Configuring the project##
You need JDK version 8+ to be able to use LWJGL3
+ Download and extract LWJGL3 [here](https://www.lwjgl.org/download)
+ Open Intellij and select "import project" from the welcome screen
+ Follow the prompts, you shouldn't need to change anything.
+ From the dropdown list at the top, select "Edit Configurations"
+ From here expand "Application" and select "Main"
+ In the VM options field add the following line
```
-Djava.library.path=lib/
```

If it doesn't compile add the LWJGL jars as dependencies
+ Go to file> project structure
+ In the window that pops up select modules from the panel on the left
+ Select Spectrum from the module list on the right and go to the Dependencies tab
+ Click on the + button and select JARs or directories
+ Navigate to the lib folder and select lwjgl.jar

---
This project is based off the LWJGl tutorials by ThinMatrix.
All textures and models used are free and open source.
