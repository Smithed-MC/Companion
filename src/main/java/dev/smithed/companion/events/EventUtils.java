package dev.smithed.companion.events;

public class EventUtils {

    public static void RegisterEvents() {
        ServerConnectionEvents.serverPlayerDisconnectEvent();
        ClientConnectionEvents.ClientJoinEvent();
    }

}
