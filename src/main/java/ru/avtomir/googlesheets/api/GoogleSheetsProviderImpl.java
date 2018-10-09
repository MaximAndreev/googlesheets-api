package ru.avtomir.googlesheets.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsProviderImpl implements GoogleSheetsProvider {

    private static final String APPLICATION_NAME = "Google Sheets API Java for Avtomir";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsProviderImpl.class);
    private final FileDataStoreFactory fileDataStoreFactory;
    private Credential credential;

    /**
     * @param credentials - JSON file with credentials
     * @param tokenFolder - a name of a folder (would be created) with secret key from Google
     * @see <a href="https://developers.google.com/sheets/api/quickstart/java">Guide</a>
     */
    public GoogleSheetsProviderImpl(InputStreamReader credentials, String tokenFolder) throws GeneralSecurityException, IOException {
        logger.trace("construct an HTTP transport for service");
        try {
            logger.trace("construct fileDataStoreFactory");
            this.fileDataStoreFactory = new FileDataStoreFactory(new java.io.File("tokens"));
        } catch (IOException e) {
            logger.error("can't read file", e);
            throw new RuntimeException(e);
        }
        this.credential = loadCredential(credentials);
    }

    @Override
    public Sheets getSheetsService() throws GeneralSecurityException, IOException {
        logger.debug("gave a Sheets instance");
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential loadCredential(InputStreamReader credentials) throws GeneralSecurityException, IOException {
        logger.info("load credentials");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, credentials);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(fileDataStoreFactory)
                .setAccessType("offline")
                .build();
        logger.info("authorize user");
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
