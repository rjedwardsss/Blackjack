# Blackjack (Java)

A desktop blackjack game built with Java and Swing. You play against a simple dealer AI, with hit/stay choices, automatic stands on 21 or bust, and a running session scoreboard.

## Features

- Full 52-card deck with suits rendered on the table (Spades, Hearts, Diamonds, Clubs)
- Player hit/stay flow with on-screen prompts and action log
- Dealer AI: hits below 16 or when behind a non-busting player total, otherwise stays
- Ace handling with soft totals (1 or 11) consistent with classic blackjack scoring
- Play-again loop without restarting the app; quit ends the background render loop cleanly

## Technologies

- Java SE
- Swing (`JFrame`, `JPanel`, custom `paintComponent` drawing)
- `java.util` collections and `java.util.concurrent.atomic` for shutdown signaling

## What I Learned

- Separating **domain models** (card, deck, hand totals) from **UI code** makes rules easier to test and reason about
- Using **enums** for rank and suit avoids string bugs (for example, comparing Aces with `==` on strings)
- **Immutable value objects** (like log messages) simplify sharing data between the model and the view
- Coordinating **game state** with a fixed-step loop and Swing repaints taught me to be careful about *when* totals are recalculated relative to AI decisions

## Future Improvements

- Move long-running dealer delays off the render path (e.g. `javax.swing.Timer`) to avoid blocking threads
- Add explicit **push** (tie) handling and configurable **house rules** (dealer soft 17, number of decks)
- Unit tests for `HandTotals`, `RoundResolver`, and `Deck`
- Package as a runnable JAR and/or migrate the same rules to a TypeScript version for the web
