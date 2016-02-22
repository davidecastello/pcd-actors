package it.unipd.math.pcd.actors.implementation;
import it.unipd.math.pcd.actors.*;
import java.util.concurrent.ThreadFactory;

/**
 *
 * Created by Davide Castello on 21/02/16.
 */
public class MyActorRef<T extends Message> extends AbsActorRef<T> {

    public MyActorRef(ActorSystem system){ super((AbsActorSystem)system); }

    @Override
    public void send(T message, ActorRef to) {
        ((AbsActor<T>)system.find(to)).newTelegram(new MyTelegram<T>(message, this));
    }


}
