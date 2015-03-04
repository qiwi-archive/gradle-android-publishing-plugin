package com.qiwi.mobile

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidPublishPlugin implements Plugin<Project> {

    @Override void apply(Project project) {
        def extensions = project.extensions.create("publishToPlay", AndroidPublishPluginExtension)
        project.task('publishToPlay') << {
            Config.configApkPath = extensions.configApkPath
            Config.configAppName = extensions.configAppName
            Config.configEmail = extensions.configEmail
            Config.configPackageName = extensions.configPackageName
            Config.keyPath = extensions.keyPath
            Config.settingsPath = extensions.settingsPath
            Config.track = extensions.track
            println("Start publish to the Google Play ...")
            def result = Worker.upload()
            if(result) println("App was successfully published to the Google Play!")
            else println("We failed to publish app to the Google Play...")
        }
    }

}

