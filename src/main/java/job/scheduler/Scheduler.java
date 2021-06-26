package job.scheduler;

import job.Job;
import job.exception.JobException;

import java.util.Queue;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static job.model.State.FAILED;
import static job.model.State.QUEUED;
import static job.model.State.RUNNING;
import static job.model.State.SUCCESS;

public final class Scheduler {
    private AtomicLong atomicCounter;
    private Queue<Job> queue;
    private List<String> list;
    private ScheduledExecutorService executorService;

    private Scheduler(int size) {
        this.queue = new PriorityQueue<>(size);
        this.list = new CopyOnWriteArrayList<>();
    }

    public static Scheduler newScheduler(int initialSize) {
        return new Scheduler(initialSize);
    }

    //we can add any type of job that extends JobType
    public <T extends Job> void submit(T job) {
        queue.add(job);
        job.setState(QUEUED);
    }

    public void startScheduler() {
        executorService = Executors.newScheduledThreadPool(queue.size());

        while (!queue.isEmpty()) {
            atomicCounter = new AtomicLong(getNumberOfJobsWithSamePriority());

            //if there are not multiple jobs with the same highest priority we use a callable and wait for the result
            if(atomicCounter.get() == 1) {
                Job job = queue.poll();
                Future<String> future = executorService.schedule(() -> performJob(job), job.getStartTime().getValue(), job.getStartTime().getTimeUnit());
                try {
                    //the get() method blocks the other threads which is the behaviour we want
                    System.out.println("Result from Callable is : " + future.get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                //we use a CountDownLatch to execute only the jobs with the same priority and to make sure the other jobs with lower priorities do not start executing
                CountDownLatch latch = new CountDownLatch((int)atomicCounter.get());
                    while (atomicCounter.get() > 0) {
                        atomicCounter.decrementAndGet();
                        Job job = queue.poll();
                        executorService.schedule(() -> {
                            performJob(job);
                            latch.countDown();}, job.getStartTime().getValue(), job.getStartTime().getTimeUnit());
                    }
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    public void stopScheduler() {
        //we wait for all the threads to finish
        executorService.shutdown();
    }

    private long getNumberOfJobsWithSamePriority() {
        Job job = queue.peek();

        return queue.stream().filter(element -> element.getPriority() == job.getPriority()).count();
    }

    private String performJob(Job job) {
        String jobDescription = job.getJobDescription();
        System.out.println(jobDescription + " -> " + job.getState().getMessage());

        try {
            //set the state to RUNNING and execute the job
            job.setState(RUNNING);
            System.out.println(jobDescription + " -> " + job.getState().getMessage());

            job.execute();

            //if the execute() method does not throw a JobException then the job is considered complete
            job.setState(SUCCESS);
            System.out.println(jobDescription + " -> " + job.getState().getMessage());
            list.add(jobDescription + " -> " + job.getState().getMessage());

            return jobDescription + " -> " + job.getState().getMessage();
        } catch (JobException e) {
            //if we get here then the job failed and we do a rollback
            job.setState(FAILED);
            System.out.println(jobDescription + " -> " + job.getState().getMessage());
            System.out.println("Rolling back job: " + jobDescription);
            job.rollback();
            list.add(jobDescription + " -> " + job.getState().getMessage());

            return jobDescription + " -> " + job.getState().getMessage();
        }
    }

    public List<String> getList() {
        //we return a defensive copy
        //the list is used only for testing purposes and the returned list is sorted lexicographically which means that the jobs may not have been executed in that specific order
        return new CopyOnWriteArrayList<>(list).stream().sorted().collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }
}
