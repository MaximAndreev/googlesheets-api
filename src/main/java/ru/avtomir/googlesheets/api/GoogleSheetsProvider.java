package ru.avtomir.googlesheets.api;

import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleSheetsProvider {

    Sheets getSheetsService() throws GeneralSecurityException, IOException;
}
