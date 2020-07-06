
# react-native-android-pip


## Getting Started

`$ npm install react-native-android-pip --save`


Include in your AndroidManifest.xml in MainActivity activity:

`android:resizeableActivity="true"
android:supportsPictureInPicture="true"
android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale|layoutDirection|fontScale|screenLayout|density|smallestScreenSize|orientation"`


```
# RN >= 0.60
No action needed

# RN < 0.60
react-native link react-native-android-pip
```


### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNAndroidPipPackage;` to the imports at the top of the file
  - Add `new RNAndroidPipPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-android-pip'
  	project(':react-native-android-pip').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-android-pip/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-android-pip')
  	```


## Usage
For android sdk version 23 and below, this method call will be ignored as PIP support is not available

```javascript
import AndroidPip from 'react-native-android-pip';

AndroidPip.enterPictureInPictureMode()
```
  
