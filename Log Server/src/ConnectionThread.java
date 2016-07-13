import java.io.*;
import java.net.Socket;

/**
 * Created by Home on 7.2.2016 г..
 */

/**
 * A thread that handles the incoming messages sent by clients.
 * The thread can be stopped from the server at any time.
 */
public class ConnectionThread extends Thread{
    private String clientName;
    private Logger logger;
    private Socket socket;
    private volatile boolean run = true;
    private static final String response = "Received!";

    /**
     * Takes a already instantiated socket and logger and saves their
     * addresses in their respective fields.
     * @param socket - the socket at which the thread will listen for new messages.
     * @param logger - the logger which will log incoming messages.
     */
    public ConnectionThread(Socket socket, Logger logger) {
        this.socket = socket;
        this.logger = logger;
    }

    /**
     * The main cycle of the thread.
     */
    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
            // First reads the client's name.
            clientName = reader.readLine();
            System.out.println("Client " + clientName + " connected!");

            // The cycle can be stopped if the method stopThread is called, thus setting run to false.
            while(run) {
                // Waits for a new message to be sent.
                String message = reader.readLine();

                // If the message isn't null it prints it using the logger
                if(message != null) {
                    synchronized (logger) {
                        logger.log(clientName, message);
                    }

                    // Sends a response back to the client to let him know the connections is still active.
                    writer.println(response);
                    writer.flush();
                    System.out.println("Received message from " + clientName);
                }else{
                    // If the message is null it means the connection is lost and breaks from the cycle.
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to print message!");
            e.printStackTrace();
        }
        this.stopThread();
        System.out.println("Client " + socket + " disconnected");
    }
    public void stopThread(){
        // Set the run variable to false.
        run = false;

        // Checks if the socket is null and closes it if not.
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
