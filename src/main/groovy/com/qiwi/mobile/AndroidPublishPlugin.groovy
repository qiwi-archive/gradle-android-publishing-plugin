package com.qiwi.mobile

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidPublishPlugin implements Plugin<Project> {

    @Override void apply(Project project) {
        def extensions = project.extensions.create("publishToPlay", AndroidPublishPluginExtension)
        project.task('publishToPlay') << {
            println("Start publish to Google Play ...")
            boolean result = Publisher.upload(extensions)
            if(result) println("App was successfully published on Google Play!")
            else throw new GradleException("Upload on Google Play failed!")
        }
    }
}

