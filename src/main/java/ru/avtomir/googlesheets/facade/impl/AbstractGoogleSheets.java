package ru.avtomir.googlesheets.facade.impl;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.avtomir.googlesheets.api.GoogleSheetsProvider;
import ru.avtomir.googlesheets.facade.GoogleSheets;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class AbstractGoogleSheets implements GoogleSheets {
    private static final Logger logger = LoggerFactory.getLogger(AbstractGoogleSheets.class);
    protected Sheets service;

    public AbstractGoogleSheets(GoogleSheetsProvider googleSheets) throws GeneralSecurityException, IOException {
        Objects.requireNonNull(googleSheets, "googleSheets must not be null");
        this.service = googleSheets.getSheetsService();
    }

    @Override
    public List<Sheet> getAll(String spreadSheetId) throws IOException {
        Objects.requireNonNull(spreadSheetId, "spreadSheetId must not be null");
        logger.debug("load list of all sheets for spreadsheet: {}", spreadSheetId);
        return service.spreadsheets().get(spreadSheetId).execute().getSheets();
    }

    @Override
    public Sheet getById(String spreadSheetId, Integer id) throws IOException {
        Objects.requireNonNull(spreadSheetId, "spreadSheetId must not be null");
        Objects.requireNonNull(id, "id must not be null");
        logger.debug("get sheet by id: {} from {}", id, spreadSheetId);
        for (Sheet sheet : getAll(spreadSheetId)) {
            SheetProperties properties = sheet.getProperties();
            if (properties.getSheetId().equals(id)) {
                return sheet;
            }
        }
        return null;
    }

    @Override
    public Sheet getByName(String spreadSheetId, String name) throws IOException {
        Objects.requireNonNull(spreadSheetId, "spreadSheetId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        logger.debug("get sheet by name: {} from {}", name, spreadSheetId);
        for (Sheet sheet : getAll(spreadSheetId)) {
            SheetProperties properties = sheet.getProperties();
            if (properties.getTitle().equals(name)) {
                return sheet;
            }
        }
        return null;
    }

    @Override
    public void renameById(String spreadSheetId, Integer id, String newName) throws IOException {
        Objects.requireNonNull(spreadSheetId, "spreadSheetId must not be null");
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(newName, "newName must not be null");
        logger.debug("rename sheet with id {} to {} in {}", id, newName, spreadSheetId);
        List<Request> requestList = Collections.singletonList(
                new Request()
                        .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                                .setProperties(new SheetProperties()
                                        .setSheetId(id)
                                        .setTitle(newName))
                                .setFields("title")));
        service.spreadsheets()
                .batchUpdate(spreadSheetId, new BatchUpdateSpreadsheetRequest().setRequests(requestList))
                .execute();
    }

    /**
     * Attention: if sheet exists than Exception would be thrown {@link GoogleJsonResponseException}
     * for `400 Bad Request`.
     */
    @Override
    public Integer createSheet(String spreadSheetId, String name) throws IOException {
        Objects.requireNonNull(spreadSheetId, "spreadSheetId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        List<Request> requestList = Collections.singletonList(
                new Request()
                        .setAddSheet(new AddSheetRequest()
                                .setProperties(new SheetProperties()
                                        .setTitle(name))));
        BatchUpdateSpreadsheetResponse response = service.spreadsheets()
                .batchUpdate(spreadSheetId, new BatchUpdateSpreadsheetRequest().setRequests(requestList))
                .execute();
        return response.getReplies().get(0).getAddSheet().getProperties().getSheetId();
    }
}
