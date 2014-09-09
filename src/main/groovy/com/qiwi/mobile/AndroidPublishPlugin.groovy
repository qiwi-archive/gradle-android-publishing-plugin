package com.qiwi.mobile

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidPublishPlugin implements Plugin<Project>{

    @Override void apply(Project project) {
        def extensions = project.extensions.create("publishToPlay", AndroidPublishPluginExtension)
        project.task('publish') << {
            Variables.configApkPath = extensions.configApkPath
            Variables.configAppName = extensions.configAppName
            Variables.configEmail = extensions.configEmail
            Variables.configPackageName = extensions.configPackageName
            Variables.keyPath = extensions.keyPath
            Variables.settingsPath = extensions.settingsPath
            println("Start publish to Google Play ...")
            Worker.work()
        }
    }



}

