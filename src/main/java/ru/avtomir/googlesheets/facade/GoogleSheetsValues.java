package ru.avtomir.googlesheets.facade;

import java.util.List;
import java.util.Map;

public interface GoogleSheetsValues extends GoogleSheets {

    /**
     * Write String values to Google Sheets.
     *
     * @param data - must not contain {@code null} values.
     */
    void writeValues(String spreadSheetId, String sheetName, String range, List<List<String>> data);

    /**
     * Read cells from Google Sheets.
     *
     * @return all empty cells returned as an empty {@link String}.
     */
    List<Map<String, String>> readValues(String spreadSheetId, String sheetName, String range);
}
