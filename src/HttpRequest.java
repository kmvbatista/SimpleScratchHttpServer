import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;

/*
 * Criar mensagem de log contendo:
 * (4) IP requisitante
 * (5) Arquivo solicitado
 * (6) Cod/status
 * 
 * Ex: 127.0.0.1 - GET / HTTP/1.1 - 200 OK - (Nome da thread que atendeu)
 */

public class HttpRequest {
	private Socket clientConn;
	private Logger logger = Logger.getInstance();

	public HttpRequest(Socket clientConn) throws Exception {
		this.clientConn	 = clientConn;
	}

	public void process() throws Exception {
		Reader reader = new InputStreamReader(clientConn.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(clientConn.getOutputStream());

		String request = bufferedReader.readLine();
		if(request == null){
			bufferedReader.close();
			bufferedOutputStream.close();
			clientConn.close();
			return;
		}
		
		request = request.trim();
		
		System.out.println(request);
		StringTokenizer st = new StringTokenizer(request);

		String header = st.nextToken();
		String fileName = st.nextToken();

		if (header.equals(HTTP.GET.toString())) {
			FileInputStream fin = null;
			boolean fileExist = true;
			try {
				if(fileName.equals("/")){
					fileName = HTTP.INDEX_PAGE_NAME.toString();
				}
				fin = new FileInputStream(fileName.substring(1));
			} catch (Exception ex) {
				fileExist = false;
			}

			String serverLine = "Simple HTTP Server";
			String statusLine = null;
			String contentTypeLine = null;
			String contentLengthLine = null;
			String contentBody = null;

			if (fileExist) {
				statusLine = HTTP.OK.toString();
				contentTypeLine = HTTP.TEXT_CONTENT.toString();
				contentLengthLine = HTTP.CONTENT_LENGTH.toString() + getLengthLine(fin);
			} else {
				statusLine = HTTP.NOT_FOUND.toString();
				contentTypeLine = HTTP.TEXT_CONTENT.toString();
				contentBody = HTTP.NOT_FOUND_PAGE.toString();
				contentLengthLine = HTTP.CONTENT_LENGTH.toString()+ getLengthLine(contentBody);
			}

			bufferedOutputStream.write(statusLine.getBytes());
			bufferedOutputStream.write(serverLine.getBytes());
			bufferedOutputStream.write(contentTypeLine.getBytes());
			bufferedOutputStream.write(contentLengthLine.getBytes());
			bufferedOutputStream.write(HTTP.CLOSE_CONNECTION.toString().getBytes());
			
			if (fileExist) {

				byte[] buffer = new byte[1024];
				int bytes = 0;
				while ((bytes = fin.read(buffer)) != -1) {
					try {
						bufferedOutputStream.write(buffer, 0, bytes);
					} catch (Exception e) {
						System.out.println("Connection aborted");
						fin.close();
						clientConn.close();
						return;
					}
				}
				fin.close();
			} else {
				bufferedOutputStream.write(contentBody.getBytes());
			}
			publishLog(request, statusLine);
			
			bufferedOutputStream.close();
			clientConn.close();
		}
	}

	private void publishLog(String request, String responsecode) {
		String currentThreadName = Thread.currentThread().getName();
		String messageToLog = getClientIp() + " - " + request + " - " + responsecode + " - " + currentThreadName;
		logger.putMessage(messageToLog);
	}

	private String getClientIp() {
		InetSocketAddress socketAddress = (InetSocketAddress)clientConn.getRemoteSocketAddress();
		Inet6Address iPV4address = (Inet6Address)socketAddress.getAddress();
		byte[] iPV4addressBytes = iPV4address.getAddress();
		return iPV4addressBytes.toString();
	}


	private String getLengthLine(FileInputStream fin) throws IOException {
		return new Integer(fin.available()).toString()+"\n";
	}
	
	private String getLengthLine(String contentBody) {
		return new Integer(contentBody.length()).toString()+"\n";
	}
}
