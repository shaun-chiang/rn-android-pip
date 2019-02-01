
# react-native-android-pip

## Getting started

`$ npm install react-native-android-pip --save`

### Mostly automatic installation

`$ react-native link react-native-android-pip`

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
```javascript
import RNAndroidPip from 'react-native-android-pip';

// TODO: What to do with the module?
RNAndroidPip;
```
  