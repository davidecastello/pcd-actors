package it.unipd.math.pcd.actors.implementation;

import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.Message;

/**
 * Implementation of the Telegram interface.
 * Created by Davide Castello on 21/02/16.
 */

public final class MyTelegram<T extends Message> implements Telegram<T> {

    private final T message;
    private final ActorRef<T> sender;

    public MyTelegram(T msg, ActorRef<T> sndr) {
        message = msg;
        sender = sndr;
    }

    /**
     * Return the message in the telegram.
     * @return T subtype of Message
     */
    @Override
    public T getMessage() {
        return message;
    }

    /**
     * Return the sender of the message.
     * @return ActorRef
     */
    @Override
    public ActorRef<T> getSender() {
        return sender;
    }
}
