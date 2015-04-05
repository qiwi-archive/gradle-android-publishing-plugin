package com.qiwi.mobile
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.Apk
import com.google.api.services.androidpublisher.model.ApkListing
import com.google.api.services.androidpublisher.model.AppEdit
import com.google.api.services.androidpublisher.model.Track

class Publisher {

    static boolean upload(AndroidPublishPluginExtension params) {
        try {
            AndroidPublisher service = PublisherHelper.init(params)
            final AndroidPublisher.Edits edits = service.edits()

            AndroidPublisher.Edits.Insert editRequest = edits
                    .insert(params.configPackageName, null)
            AppEdit edit = editRequest.execute()
            final String editId = edit.getId()

            AndroidPublisher.Edits.Apks.Upload uploadRequest = edits
                    .apks()
                    .upload(params.configPackageName, editId, PublisherHelper.getApk(params.configApkPath))
            Apk apk = uploadRequest.execute()

            List<Integer> apkVersionCodes = new ArrayList<>()
            apkVersionCodes.add(apk.getVersionCode())
            AndroidPublisher.Edits.Tracks.Update updateTrackRequest = edits
                    .tracks()
                    .update(params.configPackageName, editId, params.track, new Track().setVersionCodes(apkVersionCodes))
            updateTrackRequest.execute()

            if (params.updateMessage != "" && params.updateMessage != null) {
                def json = PublisherHelper.parseJson(params.updateMessage)
                json.messages.each { entry ->
                    AndroidPublisher.Edits.Apklistings.Update updateRecentChangesRequest = edits
                            .apklistings()
                            .update(params.configPackageName, editId, apk.getVersionCode(),
                            entry.locale, new ApkListing().setRecentChanges(entry.text))
                    updateRecentChangesRequest.execute()
                }
            }

            AndroidPublisher.Edits.Commit commitRequest = edits.commit(params.configPackageName, editId)
            commitRequest.execute()
            true
        } catch (Exception e) {
            println("Exception was thrown while uploading apk: " + e)
            false
        }
    }

}
