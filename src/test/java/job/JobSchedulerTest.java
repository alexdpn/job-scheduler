package job;

import job.model.StartTime;
import job.scheduler.Scheduler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobSchedulerTest {
    private static List<String> listForJob1;
    private static List<String> listForJob2;
    private static List<String> listForJob3;

    @BeforeAll
    public static void setUp() {
        listForJob1 = List.of("Spring Boot", "Jersey");
        listForJob2 = List.of("ActiveMQ", "Kafka");
        listForJob3 = List.of("Thymeleaf", "Freemarker");
    }

    @Test
    public void testJobsWithDifferentPrioritiesAndNoJobExceptionAndNoScheduling () throws BrokenBarrierException,
            InterruptedException, ExecutionException {
        System.setProperty("shouldThrowJobException", String.valueOf(false));

        Job job1 = new FileWriterJob(1, "Write frameworks to file", "frameworks", listForJob1);
        Job job2 = new FileWriterJob(4, "Write brokers to file", "brokers", listForJob2);
        Job job3 = new FileWriterJob(3, "Write template engines to file", "template_engines", listForJob3);

        Scheduler scheduler = Scheduler.newScheduler(3);
        scheduler.submit(job1);
        scheduler.submit(job2);
        scheduler.submit(job3);

        scheduler.startScheduler();
        scheduler.stopScheduler();

        List<String> expectedResult = List.of(
                "Write brokers to file -> The job completed successfully",
                "Write frameworks to file -> The job completed successfully",
                "Write template engines to file -> The job completed successfully"
        );

        assertEquals(expectedResult, scheduler.getList());
    }

    @Test
    public void testJobsWithSamePrioritiesAndNoJobExceptionAndNoScheduling () throws BrokenBarrierException,
            InterruptedException, ExecutionException{
        System.setProperty("shouldThrowJobException", String.valueOf(false));

        Job job1 = new FileWriterJob(3, "Write frameworks to file", "frameworks", listForJob1);
        Job job2 = new FileWriterJob(3, "Write brokers to file", "brokers", listForJob2);
        Job job3 = new FileWriterJob(3, "Write template engines to file", "template_engines", listForJob3);

        Scheduler scheduler = Scheduler.newScheduler(3);
        scheduler.submit(job1);
        scheduler.submit(job2);
        scheduler.submit(job3);

        scheduler.startScheduler();
        scheduler.stopScheduler();

        List<String> expectedResult = List.of(
                "Write brokers to file -> The job completed successfully",
                "Write frameworks to file -> The job completed successfully",
                "Write template engines to file -> The job completed successfully"
        );

        assertEquals(expectedResult, scheduler.getList());
    }

    @Test
    public void testCountDownLatchAndNoJobExceptionAndNoScheduling () throws BrokenBarrierException,
            InterruptedException, ExecutionException{
        System.setProperty("shouldThrowJobException", String.valueOf(false));

        Job job1 = new FileWriterJob(4, "Write frameworks to file", "frameworks", listForJob1);
        Job job2 = new FileWriterJob(4, "Write brokers to file", "brokers", listForJob2);
        Job job3 = new FileWriterJob(2, "Write template engines to file", "template_engines", listForJob3);

        Scheduler scheduler = Scheduler.newScheduler(3);
        scheduler.submit(job1);
        scheduler.submit(job2);
        scheduler.submit(job3);

        scheduler.startScheduler();
        scheduler.stopScheduler();

        List<String> expectedResult = List.of(
                "Write brokers to file -> The job completed successfully",
                "Write frameworks to file -> The job completed successfully",
                "Write template engines to file -> The job completed successfully"
        );

        assertEquals(expectedResult, scheduler.getList());
    }

    @Test
    public void testJobsWithDifferentPrioritiesAndJobExceptionAndNoScheduling () throws BrokenBarrierException,
            InterruptedException, ExecutionException{
        System.setProperty("shouldThrowJobException", String.valueOf(true));

        Job job1 = new FileWriterJob(2, "Write frameworks to file", "frameworks", listForJob1);
        Job job2 = new FileWriterJob(4, "Write brokers to file", "brokers", listForJob2);
        Job job3 = new FileWriterJob(5, "Write template engines to file", "template_engines", listForJob3);

        Scheduler scheduler = Scheduler.newScheduler(3);
        scheduler.submit(job1);
        scheduler.submit(job2);
        scheduler.submit(job3);

        scheduler.startScheduler();
        scheduler.stopScheduler();

        List<String> expectedResult = List.of(
                "Write brokers to file -> The job failed",
                "Write frameworks to file -> The job failed",
                "Write template engines to file -> The job failed"
        );

        assertEquals(expectedResult, scheduler.getList());
    }

    @Test
    public void testJobsWithSamePrioritiesAndJobExceptionAndNoScheduling () throws BrokenBarrierException,
            InterruptedException, ExecutionException{
        System.setProperty("shouldThrowJobException", String.valueOf(true));

        Job job1 = new FileWriterJob(3, "Write frameworks to file", "frameworks", listForJob1);
        Job job2 = new FileWriterJob(3, "Write brokers to file", "brokers", listForJob2);
        Job job3 = new FileWriterJob(3, "Write template engines to file", "template_engines", listForJob3);

        Scheduler scheduler = Scheduler.newScheduler(3);
        scheduler.submit(job1);
        scheduler.submit(job2);
        scheduler.submit(job3);

        scheduler.startScheduler();
        scheduler.stopScheduler();

        List<String> expectedResult = List.of(
                "Write brokers to file -> The job failed",
                "Write frameworks to file -> The job failed",
                "Write template engines to file -> The job failed"
        );

        assertEquals(expectedResult, scheduler.getList());
    }

    @Test
    public void testJobsWithSamePrioritiesAndNoJobExceptionAndScheduling () throws BrokenBarrierException,
            InterruptedException, ExecutionException{
        System.setProperty("shouldThrowJobException", String.valueOf(false));

        Job job1 = new FileWriterJob(3, "Write frameworks to file", new StartTime(5, SECONDS), "frameworks", listForJob1);
        Job job2 = new FileWriterJob(3, "Write brokers to file",  new StartTime(5, SECONDS),"brokers", listForJob2);
        Job job3 = new FileWriterJob(3, "Write template engines to file", new StartTime(5, SECONDS), "template_engines", listForJob3);

        Scheduler scheduler = Scheduler.newScheduler(3);
        scheduler.submit(job1);
        scheduler.submit(job2);
        scheduler.submit(job3);

        scheduler.startScheduler();
        scheduler.stopScheduler();

        List<String> expectedResult = List.of(
                "Write brokers to file -> The job completed successfully",
                "Write frameworks to file -> The job completed successfully",
                "Write template engines to file -> The job completed successfully"
        );

        assertEquals(expectedResult, scheduler.getList());
    }
}
