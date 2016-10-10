/**
 * Created by Beau on 14/09/2016.
 */
public class Game{

    private boolean closeState;

    public void init(){
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
        init();
        while(!closeState) {
            loop();
        }
        close();
    }


}
