package ru.npp.extension.client;

public final class ClientTimerState {
    private static boolean running;
    private static int remainingSeconds;

    private ClientTimerState() {
    }

    public static void setState(boolean isRunning, int secondsLeft) {
        running = isRunning;
        remainingSeconds = secondsLeft;
    }

    public static boolean isRunning() {
        return running;
    }

    public static int getRemainingSeconds() {
        return remainingSeconds;
    }
}
