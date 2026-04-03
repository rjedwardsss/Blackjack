package blackjack.model;

import java.util.Objects;

/**
 * A single playing card with rank, suit, and deck index. {@code dealt} tracks whether the card
 * is currently in play for this shoe (same behavior as the original {@code used} flag).
 */
public final class Card {

    private final Rank rank;
    private final Suit suit;
    private final int deckIndex;
    private boolean dealt;

    public Card(Rank rank, Suit suit, int deckIndex) {
        this.rank = Objects.requireNonNull(rank, "rank");
        this.suit = Objects.requireNonNull(suit, "suit");
        this.deckIndex = deckIndex;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getDeckIndex() {
        return deckIndex;
    }

    public boolean isDealt() {
        return dealt;
    }

    public void setDealt(boolean dealt) {
        this.dealt = dealt;
    }

    public boolean isAce() {
        return rank.isAce();
    }

    public String getSymbol() {
        return rank.getSymbol();
    }

    @Override
    public String toString() {
        return rank.getDisplayName() + " of " + suit.getDisplayName();
    }
}
