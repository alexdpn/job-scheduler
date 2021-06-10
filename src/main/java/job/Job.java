package job;

import job.exception.JobException;
import job.model.StartTime;
import job.model.State;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class Job implements Comparable<Job> {
    private State state;
    private int priority;
    private String jobDescription;
    private StartTime startTime;

    protected Job(int priority, String jobDescription) {
        if(priority <= 0)
            throw new IllegalArgumentException("The priority must not be zero or negative");

        this.priority = priority;
        this.jobDescription = jobDescription;
        this.startTime = new StartTime(0, SECONDS);
    }

    protected Job(int priority, String jobDescription, StartTime startTime) {
        this(priority, jobDescription);
        this.startTime = startTime;
    }

    //it is up to the subclass to provide the details of the execution
    public abstract void execute() throws JobException;

    //every subclass must provide a working rollback strategy in case the job fails
    public abstract void rollback();

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public int getPriority() {
        return priority;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public StartTime getStartTime() {
        return startTime;
    }

    //final so it cannot be overriden so we can keep the descending order in the queue for any Job
    @Override
    public final int compareTo(Job job) {
        return Integer.compare(job.getPriority(), this.getPriority());
    }
}
