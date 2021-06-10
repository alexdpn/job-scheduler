package job;

import job.exception.JobException;
import job.model.StartTime;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterJob extends Job {
    private String fileName;
    private boolean shouldthrowJobException = false;
    private List<String> data;

    public FileWriterJob(int priority, String jobDescription, String fileName, List<String> data) {
        super(priority, jobDescription);
        this.fileName = fileName;
        this.data = data;
    }

    public FileWriterJob(int priority, String jobDescription, StartTime startTime, String fileName, List<String> data) {
        super(priority, jobDescription, startTime);
        this.fileName = fileName;
        this.data = data;
    }

    @Override
    public void execute() throws JobException{
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))){

            //for testing purposes
            shouldthrowJobException = Boolean.parseBoolean(System.getProperty("shouldThrowJobException"));
            if(shouldthrowJobException)
                throw new IOException();

            //write the data
            for(String line: data) {
                bufferedWriter.write(line + System.lineSeparator());
            }
        }catch (IOException e) {
            throw new JobException("Something bad happened with the job: " + this.getJobDescription());
        }
    }

    @Override
    public void rollback() {
        //try to delete the file
        File file = new File(fileName);
        file.delete();
    }
}
