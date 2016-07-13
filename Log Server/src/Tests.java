import static org.junit.Assert.*;

import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


/**
 * Created by Mario Nitchev on 9.2.2016 г..
 */

public class Tests {
    //[Client] -> msg -> [Server] -> [Logger] -> [File]

    public interface TestThread extends Runnable {
        @Override
        void run();
        Socket getAndStop();
    }

    @Test
    public void testIfLoggerWritesToFileCorrectly() {
        Path filePath = Paths.get("C:\\OneDrive\\Uni\\Java\\logs\\test.txt");
        File file = filePath.toFile();
        try (Logger logger = new Logger(filePath);
             Scanner reader = new Scanner(file)) {
            String testMessage = "This is a test message!";
            String testName = "This is a test name";
            logger.log(testName, testMessage);
            String result = reader.nextLine();
            if (!result.contains(testName + " : " + testMessage)) {
                System.out.println(result);
                fail();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testIfClientSendsMessageProperly() {
        LogClient testClient = new LogClient(10514);
        try (ServerSocket ss = new ServerSocket(10514)) {

            TestThread test = new TestThread() {
                private Socket sock = null;
                private boolean notGot = true;

                public void run() {
                    try {
                        while (notGot) {
                            sock = ss.accept();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public Socket getAndStop() {
                    if (sock != null)
                        notGot = false;
                    return sock;
                }
            };
            Thread testThread = new Thread(test);
            testThread.setDaemon(true);
            testThread.start();
            Socket testSocket = null;
            while (testSocket != null) {
                testSocket = test.getAndStop();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(testSocket.getInputStream()))) {
                testClient.log("Test Message");
                reader.readLine(); // Client name.
                String message = reader.readLine();
                testClient.close();
                if (!message.equals("Test Message")) {
                    fail();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Test
    public void testIfServerLogsMessageProperly(){
        Path filePath = Paths.get("C:\\OneDrive\\Uni\\Java\\logs\\test1.txt");
        LogServerIO testServer = new LogServerIO(filePath,10514);
        testServer.start();
        try(Socket testSocket = new Socket(InetAddress.getLocalHost(), 10514);
        PrintWriter writer = new PrintWriter(testSocket.getOutputStream());
        Scanner sc = new Scanner(filePath.toFile())){
            String testName = "Test Name";
            String testMessage = "Test Message";
            writer.println(testName);
            writer.println(testMessage);
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){}
            testServer.close();
            if(!sc.nextLine().contains(testName + " : " + testMessage)){
                fail();
            }

        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
