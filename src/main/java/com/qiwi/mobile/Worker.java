package com.qiwi.mobile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Apks.Upload;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Commit;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Insert;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Tracks.Update;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Track;

public class Worker {

    private static final Log log = LogFactory.getLog(Worker.class);

    public static boolean upload() {
        try {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(Config.configPackageName),
                    "ApplicationConfig.PACKAGE_NAME cannot be null or empty!");

            // Create the API service.
            AndroidPublisher service = AndroidPublisherHelper.init(Config.configAppName, Config.configEmail);
            final Edits edits = service.edits();

            // Create a new edit to make changes to your listing.
            Insert editRequest = edits
                    .insert(Config.configPackageName, null /** no content */);
            AppEdit edit = editRequest.execute();
            final String editId = edit.getId();
            log.info(String.format("Created edit with id: %s", editId));

            // Upload new apk to developer console
//            final String apkPath = Worker.class
//                    .getResource(ApplicationConfig.APK_FILE_PATH)
//                    .toURI().getPath();
            final AbstractInputStreamContent apkFile =
                    new FileContent(AndroidPublisherHelper.MIME_TYPE_APK, new File(Config.configApkPath));
            Upload uploadRequest = edits
                    .apks()
                    .upload(Config.configPackageName, editId, apkFile);
            Apk apk = uploadRequest.execute();
            log.info(String.format("Version code %d has been uploaded",
                    apk.getVersionCode()));

            // Assign apk to track.
            List<Integer> apkVersionCodes = new ArrayList<>();
            apkVersionCodes.add(apk.getVersionCode());
            Update updateTrackRequest = edits
                    .tracks()
                    .update(Config.configPackageName,
                            editId,
                            Config.track,
                            new Track().setVersionCodes(apkVersionCodes));
            Track updatedTrack = updateTrackRequest.execute();
            log.info(String.format("Track %s has been updated.", updatedTrack.getTrack()));

            // Commit changes for edit.
            Commit commitRequest = edits.commit(Config.configPackageName, editId);
            AppEdit appEdit = commitRequest.execute();
            log.info(String.format("App edit with id %s has been comitted", appEdit.getId()));
            return true;
        } catch (Exception ex) {
            log.error("Exception was thrown while uploading apk ", ex);
            return false;
        }
    }

}
