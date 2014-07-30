#This plugin uses Google API to publish app to Google Play.

Add plugin as .jar
1. Build project via gradlew clean fatJar
2. Get .jar from /build/libs/
3. Put .jar in your project
4. Create file client_secrets.json with following format
{
  "installed": {
    "client_id": "[[INSERT CLIENT ID HERE]]",
    "client_secret": "[[INSERT CLIENT SECRET HERE]]",
    "redirect_uris": [],
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://accounts.google.com/o/oauth2/token"
  }
}
5. Setup build.gradle. Add something like this
buildscript {
    repositories {
        flatDir dirs: "/libs"
    }
    dependencies {
        classpath "com.qiwi:gradle-android-publishing-plugin:0.0.1"
    }
}

apply plugin: 'android-publish'

publishToPlay {
    configAppName = 'MyApp'
    configPackageName = 'ru.mw'
    configEmail = 'ru.mw@ru.mw'
    configApkPath = '/path/to/apk'
    keyPath = 'key.p12'
    settingsPath = 'client_secrets.json'
}