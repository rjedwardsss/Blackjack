package blackjack.model;

import java.util.Objects;

/**
 * Immutable log line shown in the game transcript (player vs dealer styling).
 */
public final class Message {

    private final String text;
    /** {@code "Player"} or {@code "Dealer"} for UI coloring, matching the original behavior. */
    private final String speaker;

    public Message(String text, String speaker) {
        this.text = Objects.requireNonNull(text, "text");
        this.speaker = Objects.requireNonNull(speaker, "speaker");
    }

    public String getText() {
        return text;
    }

    public String getSpeaker() {
        return speaker;
    }

    public boolean isDealerMessage() {
        return "Dealer".equalsIgnoreCase(speaker);
    }
}
