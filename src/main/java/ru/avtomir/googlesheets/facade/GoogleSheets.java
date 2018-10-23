package ru.avtomir.googlesheets.facade;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;

import java.io.IOException;
import java.util.List;

public interface GoogleSheets {

    /**
     * Get all sheets in spreadsheet.
     */
    List<Sheet> getAll(String spreadSheetId) throws IOException;

    /**
     * Get sheet by id {@link SheetProperties#sheetId}.
     */
    Sheet getById(String spreadSheetId, Integer id) throws IOException;

    /**
     * Get sheet by name {@link SheetProperties#title}.
     */
    Sheet getByName(String spreadSheetId, String name) throws IOException;

    /**
     * Rename sheet by id {@link SheetProperties#sheetId}.
     */
    void renameById(String spreadSheetId, Integer id, String newName) throws IOException;

    /**
     * Rename sheet by name {@link SheetProperties#title}.
     */
    Integer createSheet(String spreadSheetId, String name) throws IOException;

    /**
     * Check if Sheet exists.
     *
     * @return {@link Sheet} or @{@code null} if not found.
     */
    default Sheet isExist(String spreadSheetId, String name) throws IOException {
        for (Sheet sheet : getAll(spreadSheetId)) {
            if (sheet.getProperties().getTitle().equals(name)) {
                return sheet;
            }
        }
        return null;
    }
}
