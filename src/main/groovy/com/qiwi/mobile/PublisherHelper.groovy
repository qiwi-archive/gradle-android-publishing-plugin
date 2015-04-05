package com.qiwi.mobile

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import groovy.json.JsonSlurper

class PublisherHelper {

    private static final String MIME_TYPE_APK = "application/vnd.android.package-archive"
    private static final String DATA_STORE_SYSTEM_PROPERTY = "user.home"
    private static final String DATA_STORE_FILE = ".store/android_publisher_api"
    private static final File DATA_STORE_DIR =
            new File(System.getProperty(DATA_STORE_SYSTEM_PROPERTY), DATA_STORE_FILE)
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private static HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    private static final String INST_APP_USER_ID = "user"
    private static FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR)

    static AndroidPublisher init(AndroidPublishPluginExtension params) {
        Credential credential
        if (params.configEmail == null || params.configEmail.isEmpty()) {
            credential = authorizeWithInstalledApplication(params)
        } else {
            credential = authorizeWithServiceAccount(params)
        }

        new AndroidPublisher.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(params.configAppName)
                .build()
    }

    static Credential authorizeWithServiceAccount(AndroidPublishPluginExtension params) {
        new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(params.configEmail)
                .setServiceAccountScopes(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
                .setServiceAccountPrivateKeyFromP12File(new File(params.keyPath))
                .build()
    }

    static Credential authorizeWithInstalledApplication(AndroidPublishPluginExtension params) {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(PublisherHelper.class.getResourceAsStream(params.settingsPath)))

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
                .setDataStoreFactory(dataStoreFactory)
                .build()

        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
                .authorize(INST_APP_USER_ID)
    }

    static AbstractInputStreamContent getApk(String path) {
        new FileContent(MIME_TYPE_APK, new File(path))
    }

    static Object parseJson(String path) {
        File file = new File(path)
        JsonSlurper parser = new JsonSlurper()
        parser.parse(new FileReader(file))
    }

}
