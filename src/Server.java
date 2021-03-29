import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * O que servidor irah fazer:
 * (1) deverah aceitar cada conexao (isto estah implementado)
 * (2) deverah possuir um pool estatico de threads para atendimento
 * (3) devarah delegar cada atendimento para uma thread
 */


public class Server {
	private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	public static void main(String args[]) throws Exception {
		ServerSocket serverSocket = new ServerSocket(1234);
		boolean cont = true;
		while (cont) {
			Socket inSoc = serverSocket.accept();
			dispatchRequest(inSoc);
		}
		serverSocket.close();
	}
	private static void dispatchRequest(Socket inSoc) throws Exception {
		executor.execute(new HttpRequestTask(inSoc));
	}
}