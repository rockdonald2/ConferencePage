package edu.conference.utils;

public final class Constants {

    public static final String PWD_REGEXP = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    public static final String EMAIL_REGEXP = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public static final String UPLOAD_DIRECTORY = "uploads";
    public static final String PDF_SUFFIX = ".pdf";
    public static final String PDF_CONTENT_TYPE = "application/pdf";
    
}
