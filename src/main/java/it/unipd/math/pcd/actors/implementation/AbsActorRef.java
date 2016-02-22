package it.unipd.math.pcd.actors.implementation;

import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.ActorSystem;
import it.unipd.math.pcd.actors.Message;

/**
 *
 * Created by Davide Castello on 21/02/16.
 */
public abstract class AbsActorRef <T extends Message> implements ActorRef<T> {

    protected final AbsActorSystem system;

    /**
     * Constructor of the AbsActorRef class
     * @param s type ActorSystem
     */
    public AbsActorRef(ActorSystem s) {system = (AbsActorSystem) s;}

    /**
     * Override from the class Object
     * @param ref type ActorRef
     * @return int
     */
    @Override
    public int compareTo(ActorRef ref) {
        if(this == ref)
            return 0;
        else
            return -1;
    }

    public void execute(Runnable r) {system.systemExecute(r);}

}
