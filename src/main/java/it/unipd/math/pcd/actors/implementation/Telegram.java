package it.unipd.math.pcd.actors.implementation;
import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.Message;

/**
 * Interface used for the TelegramBox.
 * Created by Davide Castello on 21/02/16.
 */

public interface Telegram<T extends Message> {

    T getMessage();
    ActorRef<T> getSender();
}
