package app.dto.meditation;

public enum UploadStatus {
    PARSING,
    PARSED,
    LOADING_TO_STORAGE,
    ERROR,
    READY,
    SYSTEM_FILE_COPYING
}
