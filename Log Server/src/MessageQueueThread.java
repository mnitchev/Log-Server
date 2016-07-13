import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Mario Nitchev on 27-Dec-15.
 */

/**
 * A thread that handles sending the messages to the log server.
 * The thread uses a ConcurrentLinkedQueue of String to get the
 * messages that need to be sent.
 */
public class MessageQueueThread extends Thread {
    private InetAddress address;
    private int portNumber;
    private ConcurrentLinkedQueue<String> messages;
    private String identifier;
    private boolean done = false;
    private int numberOfReconnects = 0;

    /**
     * @param messages - the queue of messages that needs to be sent.
     * @param portNumber - the port number at to which the thread will send messages.
     * @param address - the address of the server to which the thread will send messages.
     */
    public MessageQueueThread(ConcurrentLinkedQueue<String> messages, int portNumber, InetAddress address) {
        this.address = address;
        this.portNumber = portNumber;
        identifier = ManagementFactory.getRuntimeMXBean().getObjectName().toString();
        this.messages = messages;
    }

    /**
     * @return - true if the thread is still running, false if the string has finished or
     * is finishing sending messages.
     */
    public boolean isDone(){ return done;}

    /**
     * The main cycle of the thread.
     */
    @Override
    public void run() {
        try (Socket clientSocket = new Socket(address, portNumber);
                PrintWriter socketWriter = new PrintWriter(clientSocket.getOutputStream());
              BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            // Set the number of reconnects that have been made to 0.
            // If the connection hasn't been established it wont reach this line.
            numberOfReconnects = 0;

            // Prints the identifier as the first message.
            socketWriter.println(identifier);
            while (!done) {
                // If the messages queue isn't empty sends the first message and removes it.
                if (!messages.isEmpty()) {
                    // Sends the message using a PrintWriter.
                    socketWriter.println(messages.poll());
                    socketWriter.flush();

                    /* Console messages for TESTING purposes. */
                    System.out.println("Message sent!" + messages.size());

                    // If no message is returned then the connection has been lost.
                    if(reader.readLine() == null){
                        throw new IOException("Connection lost!");
                    }
                }
            }

            /*
             * If the main thread has called stopThread, there still might be messages
             * that need to be sent.
             */
            while(!messages.isEmpty()){
                socketWriter.println(messages.remove());
                socketWriter.flush();

                /* Console messages for TESTING purposes. */
                System.out.println("Message sent!" + messages.size());

                if(reader.readLine() == null){
                    throw new IOException("Connection lost!");
                }
            }

        }catch (IOException e) {
            // Counts this attempt at reconnecting and waits a while before attempting a new one.
            numberOfReconnects++;
            System.err.print("Failed to connect!");
            try{
                Thread.sleep(2000);
            }catch (InterruptedException ie){
            }

            // If reconnecting has failed 10 times stops the thread.
            if(numberOfReconnects == 10){
                System.err.println("Disconnecting!");
                done = true;
                return;
            }

            // Recursive call to this method.
            System.err.println("Reconnecting! " + numberOfReconnects);
            this.run();
        }
    }

    public void stopThread(){
        done = true;
    }
}
