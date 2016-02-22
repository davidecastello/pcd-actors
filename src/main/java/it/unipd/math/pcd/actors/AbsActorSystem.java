/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

import java.util.HashMap;
import java.util.Map;

/**
 * A map-based implementation of the actor system.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActorSystem implements ActorSystem {

    /**
     * Associates every Actor created with an identifier.
     */
    private Map<ActorRef<?>, Actor<?>> actors;

    /**
     * The constructor creates its data field 'actor' as an HashMap where ActorRef are the keys.
     */
    public AbsActorSystem() {
        actors = new HashMap<>();
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode) {

        // ActorRef instance
        ActorRef<?> reference;
        try {
            // Create the reference to the actor
            reference = this.createActorReference(mode);
            // Create the new instance of the actor
            Actor actorInstance = ((AbsActor) actor.newInstance()).setSelf(reference);
            // Associate the reference to the actor
            actors.put(reference, actorInstance);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoSuchActorException(e);
        }
        return reference;
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }

    protected abstract ActorRef createActorReference(ActorMode mode);

    /**
     *
     * @param actor The actor to be stopped
     * Invoke this method on an actor to stop him.
     */
    @Override
    public void stop(ActorRef<?> actor) { ((AbsActor)actors.get(actor)).stop();}

    /**
     * Invoke this method to stop all actors.
     * The stop() method above will be invoked on all the actors in the ActorSystem.
     */
    @Override
    public void stop() {
        for (Map.Entry<ActorRef<?>, Actor<?>> entry : actors.entrySet())
            ((AbsActor) entry.getValue()).stop();
    }

    /**
     * Return the Actor associated to a given ActorRef, otherwise it throws a NoSuchActorException.
     * @param ref type ActorRef
     * @return act type Actor
     * @throws NoSuchActorException
     */
    public Actor<?> find(ActorRef<?> ref) throws NoSuchActorException {
        Actor act = actors.get(ref);
        if(act != null)
            return act;
        else
            throw new NoSuchActorException();
    }

    /**
     * This method will take a Runnable and it will execute it
     * @param r type Runnable
     */
    public abstract void systemExecute(Runnable r);
}