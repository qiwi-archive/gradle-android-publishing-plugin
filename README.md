#This plugin uses Google API to publish app to Google Play.

Add plugin as .jar

- Build project via **./gradlew clean fatJar**
- Get .jar from **/build/libs/**
- Put .jar in your project
- Create file **client_secrets.json** with following format
```
{
  "installed": {
    "client_id": "[[INSERT CLIENT ID HERE]]",
    "client_secret": "[[INSERT CLIENT SECRET HERE]]",
    "redirect_uris": [],
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://accounts.google.com/o/oauth2/token"
  }
}
```
- Setup **build.gradle**. Add something like this:

```
buildscript {
    repositories {
        flatDir dirs: "/libs"
    }
    dependencies {
        classpath 'com.qiwi:gradle-android-publishing-plugin:0.2.0'
    }
}

apply plugin: 'android-publish'

publishToPlay {
    configAppName = 'MyApp'
    configPackageName = 'com.example'
    configEmail = 'hi@example.com'
    configApkPath = '/path/to/apk'
    keyPath = '/path/to/key.p12'
    settingsPath = '/path/to/client_secrets.json'
    track = 'alpha'
    updateMessage = 'path/to/message.json' // optional parameter
}
```
If you want to add update message, then put it into JSON file and add `updateMessage` optional parameter to `publishToPlay` section. JSON must have format like this:
```
{
  "messages": [
    {
      "locale": "en-US",
      "text": "This is a message"
    },
    {
      "locale": "ru-RU",
      "text": "Текст"
    }
  ]
}
```
Locale must be specified according to IETF BCP 47.

To publish your apk to Google Play use command ```./gradlew publishToPlay ```

