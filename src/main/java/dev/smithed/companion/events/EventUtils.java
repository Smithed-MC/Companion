package dev.smithed.companion.events;

public class EventUtils {
    public static void registerEvents() {
        ServerConnectionEvents.serverPlayerDisconnectEvent();
        ClientConnectionEvents.clientJoinEvent();
    }
}