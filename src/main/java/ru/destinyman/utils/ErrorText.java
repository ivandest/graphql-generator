package ru.destinyman.utils;

public enum ErrorText {
    QUERIES_KEY_CONFLICT ("NO_QUERIES conflict with QUERIES_ONLY"),
    NO_FILEPATH ("Check that filepath was passed."),
    CONNECTION_STRING_NOT_VALID("Valid connection string look like: host:port:database:login:password");

    String message;
    ErrorText(String s) {
        this.message = s;
    }

    public String getMessage() {
        return message;
    }
}
