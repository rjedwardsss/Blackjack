package blackjack.game;

import blackjack.model.Card;

import java.util.List;

/**
 * Min/max hand totals with Ace handling (one soft +10 bump when any Ace is present), matching the original rules.
 */
public final class HandTotals {

    public static final HandTotals EMPTY = new HandTotals(0, 0);

    private final int minTotal;
    private final int maxTotal;

    private HandTotals(int minTotal, int maxTotal) {
        this.minTotal = minTotal;
        this.maxTotal = maxTotal;
    }

    public static HandTotals compute(List<Card> hand) {
        if (hand == null || hand.isEmpty()) {
            return EMPTY;
        }
        int min = 0;
        int max = 0;
        int aces = 0;
        for (Card card : hand) {
            int v = card.getRank().minBlackjackValue();
            min += v;
            max += v;
            if (card.isAce()) {
                aces++;
            }
        }
        if (aces > 0) {
            max += 10;
        }
        return new HandTotals(min, max);
    }

    public int getMinTotal() {
        return minTotal;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    /**
     * Best standing total: prefer {@code maxTotal} if it does not bust, else {@code minTotal}.
     */
    public int bestTotal() {
        return maxTotal > 21 ? minTotal : maxTotal;
    }
}
