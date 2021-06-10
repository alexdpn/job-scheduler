package job.model;

public enum State {
    QUEUED("The job is the queue"),
    RUNNING("The job is running"),
    SUCCESS("The job completed successfully"),
    FAILED("The job failed");

    private String message;

    State(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
