package com.billing.io;

/**
 * Signals a format/parse error while importing external data.
 */
public class ImportException extends Exception {
    public ImportException(String message, Throwable cause) { super(message, cause); }
    public ImportException(String message) { super(message); }
}
