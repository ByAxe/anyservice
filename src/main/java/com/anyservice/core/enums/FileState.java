package com.anyservice.core.enums;

/**
 * Added for future extensions of functionality.
 * <p>
 * Not used for now, because there can be a situation,
 * when file is already loaded on file storage,
 * and asynchronously some logic tries to update its state in Database
 * (from LOADING -> to SAVED)
 * but in Database it's not exist at the time of this async update
 * because Transaction is not committed yet.
 * <p>
 * So that we get an error -> file with such identifier does not exist
 * <p>
 * Described situation may occur in case of:
 * EITHER very small file
 * OR very fast storage
 * OR very slow database
 */
public enum FileState {
    LOADING,
    SAVED,
}
