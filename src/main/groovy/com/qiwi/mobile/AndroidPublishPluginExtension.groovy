package com.qiwi.mobile

class AndroidPublishPluginExtension {

    private String configAppName
    private String configPackageName
    private String configEmail
    private String configApkPath
    private String keyPath
    private String settingsPath

    String getConfigAppName() {
        return configAppName
    }

    void setConfigAppName(String configAppName) {
        this.configAppName = configAppName
    }

    String getConfigPackageName() {
        return configPackageName
    }

    void setConfigPackageName(String configPackageName) {
        this.configPackageName = configPackageName
    }

    String getConfigEmail() {
        return configEmail
    }

    void setConfigEmail(String configEmail) {
        this.configEmail = configEmail
    }

    String getConfigApkPath() {
        return configApkPath
    }

    void setConfigApkPath(String configApkPath) {
        this.configApkPath = configApkPath
    }

    String getKeyPath() {
        return keyPath
    }

    void setKeyPath(String keyPath) {
        this.keyPath = keyPath
    }

    String getSettingsPath() {
        return settingsPath
    }

    void setSettingsPath(String settingsPath) {
        this.settingsPath = settingsPath
    }
}
