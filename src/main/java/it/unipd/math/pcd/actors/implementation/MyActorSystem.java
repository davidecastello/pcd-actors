package it.unipd.math.pcd.actors.implementation;

import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.ActorRef;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Concrete class of ActorSystem.
 * Created by Davide Castello on 21/02/16.
 */

public class MyActorSystem extends AbsActorSystem {

    private ExecutorService es;

    /**
     * Constructor initializes "es" to a newCachedThreadPool
     */
    public MyActorSystem() {
        es = Executors.newCachedThreadPool();
    }

    /**
     * @param mode type ActorMode
     * @return ActorRef
     */
    @Override
    protected ActorRef createActorReference(ActorMode mode)
    {
        if(mode == ActorMode.LOCAL)
            return new MyActorRef(this);
        else
            throw new IllegalArgumentException();
    }

    /**
     * Execute the Runnable passed on the cachedThreadPool
     * @param r type Runnable
     */
    public void systemExecute(Runnable r) {es.execute(r);}
}
