package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.ActorSystem;
import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;
import it.unipd.math.pcd.actors.utils.ActorSystemFactory;
import it.unipd.math.pcd.actors.utils.actors.StoreActor;
import it.unipd.math.pcd.actors.utils.actors.TrivialActor;
import it.unipd.math.pcd.actors.utils.messages.StoreMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class used for testing purpose only.
 * It runs 5 different tests.
 * The purpose of this class is to test the functionalities of the actors and the ActorSystem,
 * in order to check if they fulfill their purposes.
 *
 * Created by Davide Castello on 22/02/16.
 */

public class MyTest {

    // the ActorSystem actually used for this testing session
    private ActorSystem system;

    /**
     * Initializes the {@code system} with a concrete implementation before each test.
     */
    @Before
    public void init() {
        system = ActorSystemFactory.buildActorSystem();
    }


    /**
     * Simple test to check the actorOf method.
     */
    @Test
    public void shouldCreateAnActorRefWithActorOf() {
        ActorRef ref = system.actorOf(TrivialActor.class);
        Assert.assertNotNull("A reference was created and it is not null", ref);
    }


    /**
     * When you stop the ActorSystem, it stops all the actors which are inside of him.
     * It should not be possible to stop an actor again, once it's stopped.
     * It should throw a NoSuchActorException.
     */
    @Test(expected = NoSuchActorException.class)
    public void shouldStopTheEntireActorSystemAndThenAnActorCouldNotBeStoppedASecondTime() {
        ActorRef ref1 = system.actorOf(TrivialActor.class);
        system.stop();
        system.stop(ref1);
    }


    /**
     * Given two actors in the system, actor1 should be able to send a telegram
     * to actor2 and this one should be able to receive it.
     */
    @Test
    public void shouldBeAbleToSendATelegramAndTheOtherOneShouldBeAbleToReceiveIt() throws InterruptedException {
        TestActorRef ref1 = new TestActorRef(system.actorOf(StoreActor.class));
        TestActorRef ref2 = new TestActorRef(system.actorOf(StoreActor.class));
        StoreActor actor1 = (StoreActor) ref1.getUnderlyingActor(system);
        StoreActor actor2 = (StoreActor) ref2.getUnderlyingActor(system);

        // actor1 sends a telegram to the actor2
        ref1.send(new StoreMessage("Hello World"), ref2);

        // Wait that the message is processed
        Thread.sleep(2000);

        // Verify that the message has been received
        Assert.assertEquals("The message has to be received by the actor", "Hello World", actor2.getData());
    }


    /**
     * Given two actors in the system, actor1 should be able
     * to send a telegram to actor2.
     * actor2 should now be able to process that message
     * and reply the sender of the message.
     */
    @Test
    public void shouldBeAbleToSendATelegramAndTheOtherOneShouldBeAbleToReply() throws InterruptedException {
        TestActorRef ref1 = new TestActorRef(system.actorOf(StoreActor.class));
        TestActorRef ref2 = new TestActorRef(system.actorOf(StoreActor.class));

        // the two actors in the conversation
        StoreActor actor1 = (StoreActor) ref1.getUnderlyingActor(system);
        StoreActor actor2 = (StoreActor) ref2.getUnderlyingActor(system);

        // actor1 sends a telegram to the actor2
        ref1.send(new StoreMessage("Hello, actor2!"), ref2);

        // Wait that the message is processed
        Thread.sleep(2000);

        // Verify that the message has been received
        Assert.assertEquals("The message has to be received by the actor", "Hello, actor2!", actor2.getData());

        // Verify that the message received was sent by actor1
        Assert.assertEquals("The sender of the message has to be actor1", ref1, actor2.getSender());

        // actor2 can now reply to the sender of the message (actor1)
        ref2.send(new StoreMessage("Hello, actor1!"), actor2.getSender());

        // Wait that the message is processed
        Thread.sleep(2000);

        // Verify that the message has been received
        Assert.assertEquals("The message has to be received by the actor", "Hello, actor1!", actor1.getData());
    }


    /**
     * Once an actor is stopped it should process all the messages
     * in its TelegramBox.
     * To test this, an actor sends to itself 30 telegrams and
     * now there should be some telegrams to read; then the actor
     * is being stopped and after some time (which is needed to process
     * all the messages) there should be no telegrams to read.
     */
    @Test
    public void shouldProcessAllTheTelegramsRemainingAfterBeingStopped() throws InterruptedException {
        // create an actor and get the real actor through its ActorRef
        // with the method getUnderlyingActor
        TestActorRef ref = new TestActorRef(system.actorOf(StoreActor.class));
        StoreActor actor = (StoreActor) ref.getUnderlyingActor(system);

        // The actor sends to itself 30 telegrams
        for(int i=0; i<10; ++i)
            ref.send(new StoreMessage("Hello, it's me!"), ref);

        // Verify that the TelegramBox is not empty
        Assert.assertEquals("There are some telegrams for you.", false, actor.nothingToRead());

        // Stop the actor
        actor.stop();

        // Wait for the actor to read all of its telegrams
        Thread.sleep(2000);

        // Verify that the TelegramBox is empty
        Assert.assertEquals("There's no telegram to read. Have a nice day!", true, actor.nothingToRead());
    }
}