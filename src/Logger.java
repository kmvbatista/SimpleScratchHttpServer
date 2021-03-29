import java.util.concurrent.LinkedBlockingQueue;

/*
 * O que o logger deve fazer:
 * (7) deverah possuir um buffer compartilhado (sugestao: LinkedBlockingQueue)
 * (8) deverah possuir uma thread para gravacao em arquivo (consumidor)
 * (9) para inserir uma mensagem no log, deve-se utilizar o metodo putMessage (produtor)
 * (10) cada mensagem, ao ser gravada em arquivo, deverah conter o numero (contador) e a hora do evento
 * (11) inclua o que for necessario (metodos e atributos)
 */

public class Logger {
	
	private static Logger instance = null;
	private final static String logFileName = "serverlog.txt";
	// incluir campos necessarios
	
	// singleton
	public static synchronized Logger getInstance(){
		
		if(instance == null){
			instance = new Logger();
		}
		return instance;
	}
	
	private Logger(){
		new LoggerThread(logFileName).start();
	}
	
	public synchronized void putMessage(String message){
		QueueLogger.publishLog(message);
	}	
}
