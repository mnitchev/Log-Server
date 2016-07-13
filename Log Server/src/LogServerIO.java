import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Mario Nitchev on 21-Dec-15.
 */

/**
 * Implementation of a log server using the blocking java.net.* API.
 * The server accepts new connections and hands them over to a new
 * ClientConnection thread that awaits input from the client's side of
 * the connection.
 */
public class LogServerIO extends Thread{
    public static final int SERVER_PORT = 10514;

    private List<ConnectionThread> connections;
    private ServerSocket serverSocket = null;
    private Path loggerPath;
    private boolean done = false;

    /**
     * Creates a new ServerSocket, Logger and an empty list of ConnectionThreads.
     * @param loggerPath - the path to the file to which the logger will log incoming messages.
     * @param port - the port at which the server will listen for new connections.
     */
    public LogServerIO(Path loggerPath, int port){
        try {
            this.serverSocket = new ServerSocket(port);
            connections = new LinkedList<>();
            this.loggerPath = loggerPath;
        }catch (IOException e){
            System.err.println("Could not start server!");
            e.printStackTrace();
        }
    }

    /**
     * The main cycle of the server thread.
     * When a new connection is accepted it is passed on to a ConnectionThread and
     * added to the thread list.
     * All new ConnectionThreads are Daemon threads.
     */
    @Override
    public void run() {
        System.out.println("Server started!");
        try(Logger logger = new Logger(loggerPath)) {
            while (!done) {
                    // Wait for a new connection and starts a new thread to handle the incoming messages,
                    // then adds it to the list of running threads.
                    Socket nextConnection = serverSocket.accept();
                    ConnectionThread nextConnectionThread = new ConnectionThread(nextConnection, logger);
                    connections.add(nextConnectionThread);

                    nextConnectionThread.setDaemon(true);
                    nextConnectionThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server closing!");
    }

    /**
     * Closes the resources used by the server.
     * @throws Exception
     */
    public void close() throws Exception {
        System.out.println("Closing!");
        done = true;

        // Closes the ServerSocket.
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close the server socket. " + e.getMessage());
            }
        }

        // Stop all the running threads.
        for (ConnectionThread client : connections) {
            client.stopThread();
        }
        connections.clear();
    }


    public static void main(String[] args) {
        Path filePath = Paths.get("C:\\OneDrive\\Uni\\Java\\logs\\log.txt");
        try {
            LogServerIO server = new LogServerIO(filePath, SERVER_PORT);
            server.start();
            Scanner sc = new Scanner(System.in);
            while (!sc.nextLine().equals("q")) {
            }
            server.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
