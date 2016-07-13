import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mario Nitchev on 18-Dec-15.
 */

/**
 * A simple logger. Implements AutoCloseable.
 * Writes messages to a specified file in the format:
 * yyyy.MM.dd HH:mm:ss.SSS UserName : Message
 * on one line each.
 */
public class Logger implements AutoCloseable{
    private Path logFilePath;
    protected PrintWriter logMessageWriter;

    protected static final SimpleDateFormat timeStampFormat =
            new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    /**
     * Creates a log at a user specified location.
     * @param logFilePath
     */
    public Logger(Path logFilePath) throws IOException {
        this.logFilePath = logFilePath;
        this.open();

    }

    /**
     * Opens the PrintWriter.
     * @throws IOException
     */
    private void open() throws IOException {
        logMessageWriter = new PrintWriter(new FileOutputStream(logFilePath.toFile(), true));
    }

    /**
     * Close the PrintWriter.
     * @throws IOException
     */
    public void close() throws IOException {
        logMessageWriter.close();
    }

    /**
     *  Logs a message to the log file.
     * @param userName - name of the user sending a message.
     * @param message - the message that needs to be logged.
     */
    public void log(String userName, String message){
        // Get the date and time and add it to the message with the user name.
        Date now = new Date();
        String formattedTime = timeStampFormat.format(now);
        String formattedMessage = formattedTime + " " + userName + " : " + message;

        // Print the message to the file.
        logMessageWriter.println(formattedMessage);

        /* Console messages for TESTING purposes. */
        System.out.println(formattedMessage);
        System.out.println("Message printed!");
    }
}
