package app.dto.meditation;

public enum MeditationStatus {
    PENDING("pending"),
    UPLOADING("uploading"),
    DONE("done"),
    ERROR("error"),
    ABORTED("aborted"),
    PROCESSING("processing");

    public final String label;

    MeditationStatus(String label) {
        this.label = label;
    }
}
