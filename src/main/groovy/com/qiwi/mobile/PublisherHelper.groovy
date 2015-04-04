package com.qiwi.mobile

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes

class PublisherHelper {

    static final String MIME_TYPE_APK = "application/vnd.android.package-archive"
    static final String SRC_RESOURCES_KEY_P12 = AndroidPublishPluginExtension.keyPath
    static final String RESOURCES_CLIENT_SECRETS_JSON = AndroidPublishPluginExtension.settingsPath
    static final String DATA_STORE_SYSTEM_PROPERTY = "user.home";
    static final String DATA_STORE_FILE = ".store/android_publisher_api";
    static final File DATA_STORE_DIR =
            new File(System.getProperty(DATA_STORE_SYSTEM_PROPERTY), DATA_STORE_FILE)
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()
    static HttpTransport HTTP_TRANSPORT
    static final String INST_APP_USER_ID = "user"

    static FileDataStoreFactory dataStoreFactory

    static Credential authorizeWithServiceAccount(String serviceAccountEmail) {
        new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountEmail)
                .setServiceAccountScopes(
                Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
                .setServiceAccountPrivateKeyFromP12File(new File(SRC_RESOURCES_KEY_P12))
                .build()
    }

    static Credential authorizeWithInstalledApplication() {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(
                        PublisherHelper.class.getResourceAsStream(RESOURCES_CLIENT_SECRETS_JSON)))

        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
                .setDataStoreFactory(dataStoreFactory).build();

        new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize(INST_APP_USER_ID)
    }

    static AndroidPublisher init(String applicationName) {
        return init(applicationName, null);
    }

    static AndroidPublisher init(String applicationName, String serviceAccountEmail) {
        newTrustedTransport();
        Credential credential;

        if (serviceAccountEmail == null || serviceAccountEmail.isEmpty()) {
            credential = authorizeWithInstalledApplication();
        } else {
            credential = authorizeWithServiceAccount(serviceAccountEmail);
        }

        new AndroidPublisher.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    static void newTrustedTransport() {
        if (null == HTTP_TRANSPORT) {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        }
    }

}
