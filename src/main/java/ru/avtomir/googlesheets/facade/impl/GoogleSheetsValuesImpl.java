package ru.avtomir.googlesheets.facade.impl;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.avtomir.googlesheets.api.GoogleSheetsProvider;
import ru.avtomir.googlesheets.facade.GoogleSheetsValues;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GoogleSheetsValuesImpl extends AbstractGoogleSheets implements GoogleSheetsValues {
    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsValuesImpl.class);

    public GoogleSheetsValuesImpl(GoogleSheetsProvider googleSheets) throws GeneralSecurityException, IOException {
        super(googleSheets);
    }

    @Override
    public void writeValues(String spreadSheetId, String sheetName, String range, List<List<String>> data) {
        Objects.requireNonNull(spreadSheetId, "spreadSheetId must not be null");
        Objects.requireNonNull(sheetName, "sheetName must not be null");
        Objects.requireNonNull(range, "range must not be null");
        Objects.requireNonNull(data, "data must not be null");
        logger.info("write to spreadSheetId: {}, sheetName: {}, range: {}", spreadSheetId, sheetName, range);
        try {
            service.spreadsheets()
                    .values()
                    .update(spreadSheetId,
                            sheetName + "!" + range,
                            toValueRange(data))
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Map<String, String>> readValues(String spreadSheetId, String sheetName, String range) {
        Objects.requireNonNull(spreadSheetId, "spreadSheetId must not be null");
        Objects.requireNonNull(sheetName, "sheetName must not be null");
        Objects.requireNonNull(range, "range must not be null");
        logger.info("read from spreadSheetId: {}, sheetName: {}, range: {}", spreadSheetId, sheetName, range);
        try {
            ValueRange response = service
                    .spreadsheets()
                    .values()
                    .get(spreadSheetId, sheetName + "!" + range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return Collections.singletonList(Collections.emptyMap());
            }
            return fromValueRange(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.singletonList(Collections.emptyMap());
        }
    }

    private static List<Map<String, String>> fromValueRange(ValueRange response) {
        logger.trace("convert ValueRange to List of Maps");
        List<List<Object>> result = response.getValues();
        List<String> headers = result.get(0).stream().map(Object::toString).collect(Collectors.toList());
        return IntStream.range(1, result.size())
                .boxed()
                .map(i -> {
                    Map<String, String> map = new HashMap<>();
                    Iterator<String> headersIter = headers.iterator();
                    Iterator<String> rowIter = result.get(i).stream().map(Object::toString).collect(Collectors.toList()).iterator();
                    while (headersIter.hasNext()) {
                        if (rowIter.hasNext()) {
                            map.put(headersIter.next(), rowIter.next());
                        } else {
                            map.put(headersIter.next(), "");
                        }
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static ValueRange toValueRange(List<List<String>> data) {
        logger.trace("convert List of Lists to ValueRange");
        ValueRange valueRange = new ValueRange();
        valueRange.setValues((List<List<Object>>) (Object) data);
        return valueRange;
    }
}
