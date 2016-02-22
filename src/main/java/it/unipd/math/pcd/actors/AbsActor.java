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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
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
import it.unipd.math.pcd.actors.implementation.AbsActorRef;
import it.unipd.math.pcd.actors.implementation.Telegram;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message> implements Actor<T> {

    // Self-reference of the actor
    protected ActorRef<T> self;

    //Sender of the current message
    protected ActorRef<T> sender;

    // True if stopped by the actor system
    private volatile boolean stopped;

    /**
     * Queue where all the received telegrams go.
     * In this project specifics, this data structure is called "mail box":
     * I decided to call it "TelegramBox" since I called "telegrams" the ones
     * who have the message, in order to be original.
     */
    private BlockingQueue<Telegram<T>> telegrambox;

    // true if the manager has already been created
    private volatile boolean createdManager;

    /**
     * The constructor of the AbsActor class
     */
    public AbsActor() {
        self = null;
        sender = null;
        stopped = false;
        telegrambox = new LinkedBlockingQueue<>();
        createdManager = false;
    }

    /**
     * Method that checks if there are any telegrams left to read.
     *
     * Method used for testing purpose only.
     */
    public boolean nothingToRead() {return telegrambox.isEmpty();}

    /**
     * Set data field "sender" to the last ActorRef that sent a message to this actor.
     *
     * @param sender
     */
    protected final void setSender(ActorRef<T> sender) {
        this.sender = sender;
    }

    /**
     * Method that returns the last ActorRef that sent a message to this actor.
     *
     * Method used for testing purpose only.
     */
    public ActorRef<T> getSender() {return sender;}

    /**
     * Sets the self-reference.
     *
     * @param self The reference to itself
     * @return The actor.
     */
    protected final Actor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    }

    /**
     * Sets the data field "stopped" to true: if it already is, it throws a NoSuchActorException.
     * @throws NoSuchActorException
     */
    public void stop() {
        synchronized (this){
            if (!stopped)
                stopped = true;
            else
                throw new NoSuchActorException();
        }
    }


    /**
     * Adds a telegram in the TelegramBox.
     * @param telegram type Telegram
     * @throws NoSuchActorException
     */
    public void newTelegram(Telegram<T> telegram){
        try {
            if (stopped)
                throw new NoSuchActorException();
            telegrambox.put(telegram);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!createdManager) {
            createTheTelegramBoxManager();
        }
    }

    /**
     * Create the TelegramBox manager.
     */
    private synchronized void createTheTelegramBoxManager() {
        ((AbsActorRef<T>)self).execute(new TelegramBoxManager());
        createdManager = true;
    }

    /**
     * Class that manages the telegrams in the TelegramBox.
     */
    private class TelegramBoxManager implements Runnable {

        /**
         * The TelegramBox manager works until the actor is stopped:
         * then he must read all the telegrams remaining before finishing his job.
         */
        @Override
        public void run() {
            while(!stopped)
                telegramManagement();
            if (stopped)
                while(!(telegrambox.isEmpty()))
                    telegramManagement();
        }


        /**
         * The method that actually do the job:
         * it takes a telegram to read, set the field "sender"
         * to the actual sender of the telegram and then
         * it processes the message with the receive method.
         */
        private void telegramManagement(){
            try {
                Telegram m = telegrambox.take();
                setSender(m.getSender());
                receive((T) m.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
