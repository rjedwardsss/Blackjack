package blackjack.model;

/**
 * Card rank from Ace through King. {@link #minBlackjackValue()} is the contribution to the hand
 * before treating an Ace as 11.
 */
public enum Rank {
    ACE(1, "Ace", "A"),
    TWO(2, "2", "2"),
    THREE(3, "3", "3"),
    FOUR(4, "4", "4"),
    FIVE(5, "5", "5"),
    SIX(6, "6", "6"),
    SEVEN(7, "7", "7"),
    EIGHT(8, "8", "8"),
    NINE(9, "9", "9"),
    TEN(10, "10", "10"),
    JACK(11, "Jack", "J"),
    QUEEN(12, "Queen", "Q"),
    KING(13, "King", "K");

    private final int deckOrder;
    private final String displayName;
    private final String symbol;

    Rank(int deckOrder, String displayName, String symbol) {
        this.deckOrder = deckOrder;
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public int getDeckOrder() {
        return deckOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isAce() {
        return this == ACE;
    }

    /**
     * Blackjack point value when counting Aces as 1 (soft-11 handled separately in {@link blackjack.game.HandTotals}).
     */
    public int minBlackjackValue() {
        switch (this) {
            case ACE:
                return 1;
            case JACK:
            case QUEEN:
            case KING:
            case TEN:
                return 10;
            default:
                return deckOrder;
        }
    }
}
