package com.qiwi.mobile
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.Apk
import com.google.api.services.androidpublisher.model.AppEdit
import com.google.api.services.androidpublisher.model.Track

class Publisher {

    static boolean upload(AndroidPublishPluginExtension params) {
        try {
            AndroidPublisher service = PublisherHelper.init(params.configAppName, params.configEmail)
            final AndroidPublisher.Edits edits = service.edits()

            AndroidPublisher.Edits.Insert editRequest = edits
                    .insert(params.configPackageName, null)
            AppEdit edit = editRequest.execute()
            final String editId = edit.getId()

            final AbstractInputStreamContent apkFile =
                    new FileContent(PublisherHelper.MIME_TYPE_APK, new File(params.configApkPath))
            AndroidPublisher.Edits.Apks.Upload uploadRequest = edits
                    .apks()
                    .upload(params.configPackageName, editId, apkFile)
            Apk apk = uploadRequest.execute()

            List<Integer> apkVersionCodes = new ArrayList<>()
            apkVersionCodes.add(apk.getVersionCode())
            AndroidPublisher.Edits.Tracks.Update updateTrackRequest = edits
                    .tracks()
                    .update(params.configPackageName, editId, params.track,
                    new Track().setVersionCodes(apkVersionCodes))
            updateTrackRequest.execute()

            AndroidPublisher.Edits.Commit commitRequest = edits.commit(params.configPackageName, editId)
            commitRequest.execute()
            true
        } catch (Exception e) {
            println("Exception was thrown while uploading apk: " + e)
            false
        }
    }

}
