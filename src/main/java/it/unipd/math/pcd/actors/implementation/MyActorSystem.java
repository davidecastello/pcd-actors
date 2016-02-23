package it.unipd.math.pcd.actors.implementation;

import it.unipd.math.pcd.actors.AbsActor;
import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.ActorRef;

import java.util.concurrent.*;

/**
 * Concrete implementation of AbsActorSystem.
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
     * Method that creates a local ActorRef
     * @param mode type ActorMode
     * @return ActorRef
     */
    @Override
    protected ActorRef createActorReference(ActorMode mode)
    {
        if(mode == ActorMode.LOCAL)
            // MyActorRef is a class that represents the implementation of local ActorRef
            return new MyActorRef(this);
        else
            throw new IllegalArgumentException();
    }

    /**
     * Execute the Runnable passed on the cachedThreadPool
     * @param r type Runnable
     */
    public void systemExecute(Runnable r) {es.execute(r);}


    /**
     * Inner class that implements a Callable, used to stop a single actor.
     * Using a Callable is possible to know if an actor has been stopped or not (Boolean).
     */
    private static class Stoppable implements Callable<Boolean> {

        private AbsActor<?> actor;

        Stoppable(AbsActor<?> a){
            actor = a;
        }

        @Override
        public Boolean call() throws Exception {
            return actor.stop();
        }
    }


    /**
     * Invoke this method on an actor to stop him.
     * @param actor type ActorRef - The actor to be stopped
     */
    @Override
    public void stop(ActorRef<?> actor) {
        final AbsActor<?> actorToStop = ((AbsActor<?>) find(actor));
        FutureTask<Boolean> future = new FutureTask<>(new Stoppable(actorToStop));
        es.execute(future);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException except) {
            except.printStackTrace();
        }
        remove(actor);
    }


    /**
     * Invoke this method to stop all actors that are active in the ActorSystem.
     * The stop() method above will be invoked on all the actors in the ActorSystem.
     */
    @Override
    public void stop() {
        for (ActorRef actor : getMapKeys()) {
            stop(actor);
        }
    }
}
