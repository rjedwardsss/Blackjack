package blackjack.game;

import blackjack.model.Message;

import java.util.List;

/**
 * Resolves round outcome and appends the appropriate log message and win counters.
 * Ties are awarded to the dealer, preserving original behavior.
 */
public final class RoundResolver {

    private RoundResolver() {
    }

    public static void resolveAndRecord(
            HandTotals player,
            HandTotals dealer,
            List<Message> log,
            GameStatistics statistics) {

        int pPoints = player.bestTotal();
        int dPoints = dealer.bestTotal();

        if (pPoints > 21 && dPoints > 21) {
            log.add(new Message("Nobody wins!", "Dealer"));
        } else if (dPoints > 21) {
            log.add(new Message("You win!", "Player"));
            statistics.recordPlayerWin();
        } else if (pPoints > 21) {
            log.add(new Message("Dealer wins!", "Dealer"));
            statistics.recordDealerWin();
        } else if (pPoints > dPoints) {
            log.add(new Message("You win!", "Player"));
            statistics.recordPlayerWin();
        } else {
            log.add(new Message("Dealer wins!", "Dealer"));
            statistics.recordDealerWin();
        }
    }
}
