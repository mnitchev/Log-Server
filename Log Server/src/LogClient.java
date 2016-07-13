import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Mario Nitchev on 21-Dec-15.
 */
public class LogClient implements AutoCloseable {
    private ConcurrentLinkedQueue<String> messages;
    private MessageQueueThread connectionThread;

    /**
     * Initialises the queue of messages and starts a new ConnectionThread with the specified port.
     * @param portNumber - the port to which the client will connect.
     */
   public LogClient(int portNumber){
       try {
           messages = new ConcurrentLinkedQueue<>();
           connectionThread = new MessageQueueThread(messages, portNumber,  InetAddress.getLocalHost());
           connectionThread.start();
       } catch (UnknownHostException e) {
           e.printStackTrace();
       }
   }

    /**
     * Initialises the queue of messages and starts a new ConnectionThread with the specified port.
     * @param portNumber - the port to which the client will connect.
     * @param address - the address of the server.
     */
    public LogClient(int portNumber, InetAddress address){
        messages = new ConcurrentLinkedQueue<>();
        connectionThread = new MessageQueueThread(messages, portNumber, address);
        connectionThread.start();
    }

    /**
     * Checks weather the connection thread is still running.
     * @return - true if the thread is still running,
     * false if it has stopped or is finishing sending queued messages.
     */
    public boolean isRunning(){
        return !connectionThread.isDone();
    }

    /**
     * Adds a message to the messages queue.
     * @param message - the message that needs to be added.
     */
    public void log(String message) {
            messages.add(message);
    }

    /**
     * Stops the connection thread and joins it to the calling thread.
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        connectionThread.stopThread();
        connectionThread.join();
    }
}
