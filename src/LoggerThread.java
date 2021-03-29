import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerThread extends Thread{
  private final String pathToWrite;
  private int logCounter = 0;

  public LoggerThread(String pathToWrite) {
    this.pathToWrite = pathToWrite;
  }

  @Override
  public void run() {
    while(true) {
      if(QueueLogger.hasLog()) {
        String message = QueueLogger.consumeLog();
        log(message);
      }
    }
  }

  private String getCounter() {
    logCounter++;
    return String.format("Log #%d ", logCounter);
  }

  private void log(String message) {
    String logMessage = String.format("-%s at %s \n %s \n -----------\n", getCounter(), getDateTime(), message);
    FileWriter fileWriter;
    File file = new File(this.pathToWrite);
    boolean append = true;
    try {
      fileWriter = new FileWriter(file, append);
      fileWriter.write(logMessage);
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getDateTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
    LocalDateTime now = LocalDateTime.now(); 
    return dtf.format(now).toString();
  }
}
