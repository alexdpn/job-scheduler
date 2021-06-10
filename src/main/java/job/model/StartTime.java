package job.model;

import java.util.concurrent.TimeUnit;

public class StartTime {
    private int value;
    private TimeUnit timeUnit;

    public StartTime(int value, TimeUnit timeUnit) {
        if(value < 0)
            throw new IllegalArgumentException("Value Must not be negative");

        this.value = value;
        this.timeUnit = timeUnit;
    }

    public int getValue() {
        return this.value;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }
}
