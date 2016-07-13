/**
 * Created by Home on 8.2.2016 г..
 */
public class ClientMain3 {
    public static void main(String[] args) {
        try (LogClient me = new LogClient(10514)) {
            for (int i = 0; i < 1000 ; i++) {
                try {
                    Thread.sleep(100);
                }catch(InterruptedException ie){
                }
                System.out.println("Sending message!" + i);
                me.log("The Joker" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
