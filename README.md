# Spectrum- COMP3320 Game


[Dropbox link for documentation](https://www.dropbox.com/home/COMP3320)

---

##Setting up the environment

These steps are for IntelliJ IDEA, it has a community edition available [here](https://www.jetbrains.com/idea/download/), as well as a pro version that is free with a student account. The community edition has everything you need to be able to use the framework used. If you use eclipse or netbeans the tutorial on setting up the environment is [here](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29).

###Required Downloads
+ [JDK 7+](http://www.oracle.com/technetwork/java/javase/downloads/index.html), 6 or earlier won't be compatible
+ [Android SDK](https://developer.android.com/studio/index.html#resources). Only need the SDK at the bottom of the page, under command line tools. Extract and run the SDK manager to get the latest stable build

###Set environment variables
Set an environment variable called ANDROID_HOME that points to the android sdk

##Setting up the project
This requires you to have Git CMD installed, download from [here](https://git-scm.com/downloads).
Run Git CMD and paste in the line 
```
git clone https://github.com/pratN/COMP3320
```

Navigate to where you have cloned the Spectrum repository and find the file `local.properties`. Open it with any text editor and edit the line
```
sdk.dir=C:/Users/Beau/Downloads/android-sdk_r24.4.1-windows/android-sdk-windows
```
So that it points to the location where you installed your android SDK. **Note the directions of the slashes, they must all be forward slash**

Next, start up IntelliJ and go to open>Spectrum>build.gradle

In the screen that pops up make sure that your java home is in the correct location and select OK

When the project has finished importing go to Run>Edit Configurations

In the window that pops up click the + in the top left of the screen, select application and set the following fields.
+ Name: Desktop
+ JRE: Select your preferred JRE
+ Use classpath to module: desktop
+ Working Directory: /android/assets
+ Main Class: DesktopLauncher
 
Click Apply, then OK

Click Run and if everything goes well you will see a window with a red background and BadLogic's logo in it.
