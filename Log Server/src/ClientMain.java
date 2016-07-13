/**
 * Created by Mario Nitchev on 22-Dec-15.
 */
public class ClientMain {
    public static void main(String[] args) {
        try (LogClient me = new LogClient(10514)) {
            for (int i = 0; i < 100 ; i++) {
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ie){
                }
                System.out.println("Sending message!" + i);
                me.log("Batman" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
