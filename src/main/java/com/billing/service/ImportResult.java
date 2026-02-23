package com.billing.service;

public class ImportResult {
    public final int imported;
    public final int skipped;
    public final int failed;

    public ImportResult(int imported, int skipped, int failed) {
        this.imported = imported;
        this.skipped = skipped;
        this.failed = failed;
    }
}
