import java.util.concurrent.LinkedBlockingQueue;

public class QueueLogger {
  private static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
  public static synchronized String consumeLog() {
    return queue.poll();
  }

  public static synchronized boolean hasLog() {
    return !queue.isEmpty();
  }

  public static synchronized void publishLog(String itemString) {
    queue.add(itemString);
  }


}
