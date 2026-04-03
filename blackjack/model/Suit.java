package blackjack.model;

/**
 * Playing-card suit. Order matches the original deck construction (Spades, Hearts, Diamonds, Clubs).
 */
public enum Suit {
    SPADES("Spades"),
    HEARTS("Hearts"),
    DIAMONDS("Diamonds"),
    CLUBS("Clubs");

    private final String displayName;

    Suit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRed() {
        return this == HEARTS || this == DIAMONDS;
    }
}
