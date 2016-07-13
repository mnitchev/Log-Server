/**
 * Created by Mario Nitchev on 28-Dec-15.
 */
public class ClientMain2 {
    public static void main(String[] args) {
        try (LogClient me = new LogClient(10514)) {
            for (int i = 0; i < 1000 ; i++) {
                try {
                    Thread.sleep(100);
                }catch(InterruptedException ie){
                }
                System.out.println("Sending message!" + i);
                me.log("Robin" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}