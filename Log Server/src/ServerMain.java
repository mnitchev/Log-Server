import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Mario Nitchev on 20-Dec-15.
 */
public class ServerMain {
    public static void main(String[] args) {
        Path filePath = Paths.get("C:\\OneDrive\\Uni\\Java\\logs\\log.txt");
        try {
            LogServerIO server = new LogServerIO(filePath, LogServerIO.SERVER_PORT);
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
