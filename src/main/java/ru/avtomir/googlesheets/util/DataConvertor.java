package ru.avtomir.googlesheets.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataConvertor {
    private static final Logger logger = LoggerFactory.getLogger(DataConvertor.class);

    public static List<List<String>> mapToList(List<Map<String, String>> listOfMapData, List<String> orderOfColumns) {
        logger.debug("convert List of Maps to List of Lists");
        List<List<String>> listOfListData = new ArrayList<>();
        listOfListData.add(orderOfColumns);
        for (Map<String, String> map : listOfMapData) {
            List<String> rowOfdata = new ArrayList<>();
            for (String columnName : orderOfColumns) {
                rowOfdata.add(map.getOrDefault(columnName, ""));
            }
            listOfListData.add(rowOfdata);
        }
        return listOfListData;
    }
}
