package blackjack.game;

/**
 * Running win counts for the session (replaces static fields on {@code Main}).
 */
public final class GameStatistics {

    private int playerWins;
    private int dealerWins;

    public int getPlayerWins() {
        return playerWins;
    }

    public int getDealerWins() {
        return dealerWins;
    }

    public void recordPlayerWin() {
        playerWins++;
    }

    public void recordDealerWin() {
        dealerWins++;
    }
}
