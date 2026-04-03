package blackjack.ui;

import blackjack.game.GameStatistics;
import blackjack.game.HandTotals;
import blackjack.game.RoundResolver;
import blackjack.model.Card;
import blackjack.model.Deck;
import blackjack.model.Message;
import blackjack.model.Suit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Swing UI for blackjack: layout, rendering, and input. Game rules and scoring are delegated to
 * {@link HandTotals} and {@link RoundResolver}; the deck is managed by {@link Deck}.
 */
public class BlackjackFrame extends JFrame {

    private final Random random = new Random();
    private final Deck deck = new Deck();
    private final GameStatistics statistics;
    private final AtomicBoolean applicationRunning;

    private boolean dealerBusy;

    private final List<Card> playerHand = new ArrayList<>();
    private final List<Card> dealerHand = new ArrayList<>();
    private final List<Message> messageLog = new ArrayList<>();

    private HandTotals playerTotals = HandTotals.EMPTY;
    private HandTotals dealerTotals = HandTotals.EMPTY;

    private final Font fontCard = new Font("Times New Roman", Font.PLAIN, 40);
    private final Font fontQuest = new Font("Times New Roman", Font.BOLD, 40);
    private final Font fontButton = new Font("Times New Roman", Font.PLAIN, 25);
    private final Font fontLog = new Font("Times New Roman", Font.ITALIC, 30);

    private final Color colorDealerMessage = Color.red;
    private final Color colorPlayerMessage = new Color(255, 255, 255);

    private final String promptHitOrStay = "Hit or Stay?";
    private final String promptPlayAgain = "Play more?";

    private final Color colorBackground = new Color(39, 119, 20);
    private final Color colorButton = new Color(37, 203, 204);

    private final JButton buttonHit = new JButton();
    private final JButton buttonStay = new JButton();
    private final JButton buttonYes = new JButton();
    private final JButton buttonNo = new JButton();

    private final int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private final int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private static final int WINDOW_WIDTH = 1300;
    private static final int WINDOW_HEIGHT = 800;

    private static final int GRID_X = 50;
    private static final int GRID_Y = 50;
    private static final int GRID_WIDTH = 900;
    private static final int GRID_HEIGHT = 400;

    private static final int CARD_SPACING = 10;
    private static final int CARD_CORNER_ROUNDING = 10;
    private final int slotWidth = GRID_WIDTH / 6;
    private final int slotHeight = GRID_HEIGHT / 2;
    private final int cardWidth = slotWidth - CARD_SPACING * 2;
    private final int cardHeight = slotHeight - CARD_SPACING * 2;

    private boolean awaitingHitOrStay = true;
    private boolean dealerTurnActive;
    private boolean awaitingPlayAgain;

    private final int[] diamondPolygonX = new int[4];
    private final int[] diamondPolygonY = new int[4];

    public BlackjackFrame(GameStatistics statistics, AtomicBoolean applicationRunning) {
        this.statistics = statistics;
        this.applicationRunning = applicationRunning;

        setTitle("RJ's Blackjack Game");
        setBounds((screenWidth - WINDOW_WIDTH - 6) / 2, (screenHeight - WINDOW_HEIGHT - 29) / 2,
                WINDOW_WIDTH + 6, WINDOW_HEIGHT + 29);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Board board = new Board();
        setContentPane(board);
        board.setLayout(null);

        addMouseMotionListener(new Move());
        addMouseListener(new Click());

        buttonHit.addActionListener(new ActHit());
        buttonHit.setBounds(1000, 200, 100, 50);
        buttonHit.setBackground(colorButton);
        buttonHit.setFont(fontButton);
        buttonHit.setText("HIT");
        board.add(buttonHit);

        buttonStay.addActionListener(new ActStay());
        buttonStay.setBounds(1150, 200, 100, 50);
        buttonStay.setBackground(colorButton);
        buttonStay.setFont(fontButton);
        buttonStay.setText("STAY");
        board.add(buttonStay);

        buttonYes.addActionListener(new ActYes());
        buttonYes.setBounds(1000, 600, 100, 50);
        buttonYes.setBackground(colorButton);
        buttonYes.setFont(fontButton);
        buttonYes.setText("YES");
        board.add(buttonYes);

        buttonNo.addActionListener(new ActNo());
        buttonNo.setBounds(1150, 600, 100, 50);
        buttonNo.setBackground(colorButton);
        buttonNo.setFont(fontButton);
        buttonNo.setText("NO");
        board.add(buttonNo);

        playerHand.add(deck.drawRandomAvailable(random));
        dealerHand.add(deck.drawRandomAvailable(random));
        playerHand.add(deck.drawRandomAvailable(random));
        dealerHand.add(deck.drawRandomAvailable(random));

        refreshHandTotals();
        setVisible(true);
    }

    private void refreshHandTotals() {
        playerTotals = HandTotals.compute(playerHand);
        dealerTotals = HandTotals.compute(dealerHand);
    }

    private void dealerHitOrStay() {
        dealerBusy = true;

        int dealerShowing = dealerTotals.bestTotal();
        int playerShowing = playerTotals.bestTotal();

        repaint();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean mustHit = (dealerShowing < playerShowing && playerShowing <= 21) || dealerShowing < 16;

        if (mustHit) {
            int totalBeforeDraw = dealerTotals.getMaxTotal() <= 21
                    ? dealerTotals.getMaxTotal()
                    : dealerTotals.getMinTotal();
            messageLog.add(new Message("Dealer decided to hit! (total: " + totalBeforeDraw + ")", "Dealer"));
            dealerHand.add(deck.drawRandomAvailable(random));
        } else {
            int totalShown = dealerTotals.getMaxTotal() <= 21
                    ? dealerTotals.getMaxTotal()
                    : dealerTotals.getMinTotal();
            messageLog.add(new Message("Dealer decided to stay! (total: " + totalShown + ")", "Dealer"));
            RoundResolver.resolveAndRecord(playerTotals, dealerTotals, messageLog, statistics);
            dealerTurnActive = false;
            awaitingPlayAgain = true;
        }
        dealerBusy = false;
    }

    public void tick() {
        if (awaitingHitOrStay) {
            buttonHit.setVisible(true);
            buttonStay.setVisible(true);
        } else {
            buttonHit.setVisible(false);
            buttonStay.setVisible(false);
        }

        if (dealerTurnActive && !dealerBusy) {
            dealerHitOrStay();
        }

        if (awaitingPlayAgain) {
            buttonYes.setVisible(true);
            buttonNo.setVisible(true);
        } else {
            buttonYes.setVisible(false);
            buttonNo.setVisible(false);
        }

        refreshHandTotals();

        if (awaitingHitOrStay
                && (playerTotals.getMaxTotal() == 21 || playerTotals.getMinTotal() >= 21)) {
            int shown = playerTotals.getMaxTotal() <= 21
                    ? playerTotals.getMaxTotal()
                    : playerTotals.getMinTotal();
            messageLog.add(new Message("Auto pass! (total: " + shown + ")", "Player"));
            awaitingHitOrStay = false;
            dealerTurnActive = true;
        }

        if (dealerTurnActive
                && (dealerTotals.getMaxTotal() == 21 || dealerTotals.getMinTotal() >= 21)) {
            int shown = dealerTotals.getMaxTotal() <= 21
                    ? dealerTotals.getMaxTotal()
                    : dealerTotals.getMinTotal();
            messageLog.add(new Message("Dealer auto pass! (total: " + shown + ")", "Dealer"));
            RoundResolver.resolveAndRecord(playerTotals, dealerTotals, messageLog, statistics);
            dealerTurnActive = false;
            awaitingPlayAgain = true;
        }

        repaint();
    }

    private class Board extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(colorBackground);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            if (awaitingHitOrStay) {
                g.setColor(Color.black);
                g.setFont(fontQuest);
                g.drawString(promptHitOrStay, GRID_X + GRID_WIDTH + 60, GRID_Y + 90);
                g.drawString("Total:", GRID_X + GRID_WIDTH + 60, GRID_Y + 290);
                if (playerTotals.getMinTotal() == playerTotals.getMaxTotal()) {
                    g.drawString(Integer.toString(playerTotals.getMaxTotal()), GRID_X + GRID_WIDTH + 60, GRID_Y + 350);
                } else if (playerTotals.getMaxTotal() <= 21) {
                    g.drawString(playerTotals.getMinTotal() + " or " + playerTotals.getMaxTotal(),
                            GRID_X + GRID_WIDTH + 60, GRID_Y + 350);
                } else {
                    g.drawString(Integer.toString(playerTotals.getMinTotal()), GRID_X + GRID_WIDTH + 60, GRID_Y + 350);
                }
            } else if (awaitingPlayAgain) {
                g.setColor(Color.black);
                g.setFont(fontQuest);
                g.drawString(promptPlayAgain, GRID_X + GRID_WIDTH + 70, GRID_Y + 490);
            }

            g.setColor(Color.black);
            g.fillRect(GRID_X, GRID_Y + GRID_HEIGHT + 50, GRID_WIDTH, 500);

            g.setFont(fontLog);
            int logIndex = 0;
            for (Message line : messageLog) {
                g.setColor(line.isDealerMessage() ? colorDealerMessage : colorPlayerMessage);
                g.drawString(line.getText(), GRID_X + 20, GRID_Y + 480 + logIndex * 35);
                logIndex++;
            }

            g.setColor(Color.BLACK);
            g.setFont(fontQuest);
            String score = "Score: (P) " + statistics.getPlayerWins() + " - (C) " + statistics.getDealerWins();
            g.drawString(score, GRID_X + GRID_WIDTH + 10, GRID_Y + GRID_HEIGHT + 300);

            int index = 0;
            for (Card c : playerHand) {
                drawCardFace(g, c, index, 0);
                index++;
            }

            if (dealerTurnActive || awaitingPlayAgain) {
                index = 0;
                for (Card c : dealerHand) {
                    drawCardFace(g, c, index, 200);
                    index++;
                }

                g.setColor(Color.black);
                g.setFont(fontQuest);
                g.drawString("Your total: ", GRID_X + GRID_WIDTH + 60, GRID_Y + 40);
                if (playerTotals.getMaxTotal() <= 21) {
                    g.drawString(Integer.toString(playerTotals.getMaxTotal()), GRID_X + GRID_WIDTH + 60, GRID_Y + 120);
                } else {
                    g.drawString(Integer.toString(playerTotals.getMinTotal()), GRID_X + GRID_WIDTH + 60, GRID_Y + 120);
                }
                g.drawString("Dealer's total: ", GRID_X + GRID_WIDTH + 60, GRID_Y + 240);
                if (dealerTotals.getMaxTotal() <= 21) {
                    g.drawString(Integer.toString(dealerTotals.getMaxTotal()), GRID_X + GRID_WIDTH + 60, GRID_Y + 320);
                } else {
                    g.drawString(Integer.toString(dealerTotals.getMinTotal()), GRID_X + GRID_WIDTH + 60, GRID_Y + 320);
                }
            }
        }

        private void drawCardFace(Graphics g, Card c, int index, int rowOffset) {
            g.setColor(Color.white);
            g.fillRect(GRID_X + CARD_SPACING + slotWidth * index + CARD_CORNER_ROUNDING, GRID_Y + CARD_SPACING + rowOffset,
                    cardWidth - CARD_CORNER_ROUNDING * 2, cardHeight);
            g.fillRect(GRID_X + CARD_SPACING + slotWidth * index, GRID_Y + CARD_SPACING + CARD_CORNER_ROUNDING + rowOffset,
                    cardWidth, cardHeight - CARD_CORNER_ROUNDING * 2);
            g.fillOval(GRID_X + CARD_SPACING + slotWidth * index, GRID_Y + CARD_SPACING + rowOffset,
                    CARD_CORNER_ROUNDING * 2, CARD_CORNER_ROUNDING * 2);
            g.fillOval(GRID_X + CARD_SPACING + slotWidth * index, GRID_Y + CARD_SPACING + cardHeight - CARD_CORNER_ROUNDING * 2 + rowOffset,
                    CARD_CORNER_ROUNDING * 2, CARD_CORNER_ROUNDING * 2);
            g.fillOval(GRID_X + CARD_SPACING + slotWidth * index + cardWidth - CARD_CORNER_ROUNDING * 2, GRID_Y + CARD_SPACING + rowOffset,
                    CARD_CORNER_ROUNDING * 2, CARD_CORNER_ROUNDING * 2);
            g.fillOval(GRID_X + CARD_SPACING + slotWidth * index + cardWidth - CARD_CORNER_ROUNDING * 2,
                    GRID_Y + CARD_SPACING + cardHeight - CARD_CORNER_ROUNDING * 2 + rowOffset,
                    CARD_CORNER_ROUNDING * 2, CARD_CORNER_ROUNDING * 2);

            g.setFont(fontCard);
            g.setColor(c.getSuit().isRed() ? Color.red : Color.black);
            g.drawString(c.getSymbol(), GRID_X + CARD_SPACING + slotWidth * index + CARD_CORNER_ROUNDING,
                    GRID_Y + CARD_SPACING + cardHeight - CARD_CORNER_ROUNDING + rowOffset);

            Suit suit = c.getSuit();
            int baseX = GRID_X + slotWidth * index;
            int baseY = GRID_Y + rowOffset;

            if (suit == Suit.HEARTS) {
                g.fillOval(baseX + 42, baseY + 70, 35, 35);
                g.fillOval(baseX + 73, baseY + 70, 35, 35);
                g.fillArc(baseX + 30, baseY + 90, 90, 90, 51, 78);
            } else if (suit == Suit.DIAMONDS) {
                diamondPolygonX[0] = baseX + 75;
                diamondPolygonX[1] = baseX + 50;
                diamondPolygonX[2] = baseX + 75;
                diamondPolygonX[3] = baseX + 100;
                diamondPolygonY[0] = baseY + 60;
                diamondPolygonY[1] = baseY + 100;
                diamondPolygonY[2] = baseY + 140;
                diamondPolygonY[3] = baseY + 100;
                g.fillPolygon(diamondPolygonX, diamondPolygonY, 4);
            } else if (suit == Suit.SPADES) {
                g.fillOval(baseX + 42, baseY + 90, 35, 35);
                g.fillOval(baseX + 73, baseY + 90, 35, 35);
                g.fillArc(baseX + 30, baseY + 15, 90, 90, 51 + 180, 78);
                g.fillRect(baseX + 70, baseY + 100, 10, 40);
            } else {
                g.fillOval(baseX + 40, baseY + 90, 35, 35);
                g.fillOval(baseX + 75, baseY + 90, 35, 35);
                g.fillOval(baseX + 58, baseY + 62, 35, 35);
                g.fillRect(baseX + 70, baseY + 75, 10, 70);
            }
        }
    }

    private static class Move implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
            // unused
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // unused
        }
    }

    private static class Click implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            // unused
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // unused
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // unused
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // unused
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // unused
        }
    }

    private class ActHit implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!awaitingHitOrStay) {
                return;
            }
            int shown = playerTotals.bestTotal();
            messageLog.add(new Message("You decided to hit! (total: " + shown + ")", "Player"));
            playerHand.add(deck.drawRandomAvailable(random));
        }
    }

    private class ActStay implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!awaitingHitOrStay) {
                return;
            }
            int shown = playerTotals.bestTotal();
            messageLog.add(new Message("You decided to stay! (total: " + shown + ")", "Player"));
            awaitingHitOrStay = false;
            dealerTurnActive = true;
        }
    }

    private class ActYes implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            deck.reset();
            playerHand.clear();
            dealerHand.clear();
            messageLog.clear();

            awaitingPlayAgain = false;
            awaitingHitOrStay = true;

            playerHand.add(deck.drawRandomAvailable(random));
            dealerHand.add(deck.drawRandomAvailable(random));
            playerHand.add(deck.drawRandomAvailable(random));
            dealerHand.add(deck.drawRandomAvailable(random));
        }
    }

    private class ActNo implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            applicationRunning.set(false);
            dispose();
        }
    }
}
