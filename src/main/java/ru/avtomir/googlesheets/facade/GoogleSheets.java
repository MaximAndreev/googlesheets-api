package ru.avtomir.googlesheets.facade;

import com.google.api.services.sheets.v4.model.Sheet;

import java.io.IOException;
import java.util.List;

public interface GoogleSheets {

    List<Sheet> getAll(String spreadSheetId) throws IOException;

    Sheet getById(String spreadSheetId, Integer id) throws IOException;

    Sheet getByName(String spreadSheetId, String name) throws IOException;

    void renameById(String spreadSheetId, Integer id, String newName) throws IOException;
}
