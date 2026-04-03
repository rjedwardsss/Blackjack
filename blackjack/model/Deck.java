package blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Standard 52-card deck with the same ordering as the original project (13 ranks × 4 suits).
 */
public final class Deck {

    private final List<Card> cards;

    public Deck() {
        List<Card> built = new ArrayList<>(52);
        for (int i = 0; i < 52; i++) {
            Rank rank = Rank.values()[i / 4];
            Suit suit = Suit.values()[i % 4];
            built.add(new Card(rank, suit, i));
        }
        this.cards = Collections.unmodifiableList(built);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void reset() {
        for (Card card : cards) {
            card.setDealt(false);
        }
    }

    /**
     * Draws a random card that is not currently {@link Card#isDealt()} and marks it dealt.
     */
    public Card drawRandomAvailable(Random random) {
        int index;
        do {
            index = random.nextInt(cards.size());
        } while (cards.get(index).isDealt());
        Card card = cards.get(index);
        card.setDealt(true);
        return card;
    }
}
