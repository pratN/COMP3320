/**
 * Created by Beau on 14/09/2016.
 */
public class Game{

    private boolean closeState;

    public void innit(){
        //Create window

        //Create renderHandler

        //Create world
    }

    public void loop(){
        //Render loop
        //Game logic
    }

    public void close(){}

    public void run(){
        innit();
        while(!closeState) {
            loop();
        }
        close();
    }


}
