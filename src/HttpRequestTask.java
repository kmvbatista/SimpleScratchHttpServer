import java.net.Socket;

public class HttpRequestTask implements Runnable {
  private Socket inSoc;
  public HttpRequestTask(Socket socket) {
    this.inSoc = socket;
  }
  @Override
	public void run() {
		try {
      HttpRequest request = new HttpRequest(inSoc);
      request.process();
    } catch (Exception e) {
      e.printStackTrace();
    }
	}
}
