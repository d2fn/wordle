package com.df;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private final String word;
    private final List<Guess> guesses;

    private final Guess emptyGuess;

    public Board(String word) {
        this.word = word;
        this.guesses = new ArrayList<>(word.length());

        final List<Position> emptyPositions = new ArrayList<>(word.length());
        for(int i = 0; i < word.length(); i++) {
//            emptyPositions.add(new Position('a', PositionState.values()[i%PositionState.values().length]));
            emptyPositions.add(new Position(' ', PositionState.EMPTY));
        }
        emptyGuess = new Guess(" ", emptyPositions);
    }

    private Map<Character, Set<Integer>> analyze(String s) {
        final Map<Character, Set<Integer>> charPositionMap = new HashMap<>();
        for(int i = 0; i < s.length(); i++) {
            final Character c = s.charAt(i);
            charPositionMap.computeIfAbsent(c, cc -> new HashSet<>()).add(i);
        }
        return charPositionMap;
    }

    public void guess(String guess) {

        final Map<Character, Set<Integer>> wordMap = analyze(word);
//        final Map<Character, Set<Integer>> guessMap = analyze(guess);

        final Position[] positions = new Position[word.length()];

        // remove matches
        for(int i = 0; i < word.length(); i++) {
            final char guessChar = guess.charAt(i);
            if (word.charAt(i) == guessChar) {
                positions[i] = new Position(guessChar, PositionState.CORRECT);
                Set<Integer> remaining = wordMap.get(guessChar);
                remaining.remove(i);
                if (remaining.isEmpty()) {
                    wordMap.remove(guessChar);
                }
            }
        }

        for(int i = 0; i < word.length(); i++) {
            final char guessChar = guess.charAt(i);
            if(positions[i] != null) {
                continue;
            }
            if(wordMap.containsKey(guessChar)) {
                positions[i] = new Position(guessChar, PositionState.OUT_OF_PLACE);
                Set<Integer> remaining = wordMap.get(guessChar);
                if(remaining.size() == 1) {
                    wordMap.remove(guessChar);
                }
                else {
                    final List<Integer> removeOne = new ArrayList<>(remaining);
                    removeOne.remove(0);
                    wordMap.put(guessChar, new HashSet<>(removeOne));
                }
            }
            else {
                positions[i] = new Position(guessChar, PositionState.NOT_FOUND);
            }
        }

        guesses.add(new Guess(guess, Arrays.asList(positions)));
    }

    public void print() {

        guesses.forEach(Guess::print);
        for(int i = 0; i < (word.length() - guesses.size()); i++) {
            emptyGuess.print();
        }

        if (lost()) {
            System.out.printf("Out of guesses: word was \"%s\"\n\n", word);
            return;
        }
    }

    private String lastGuess() {
        if(guesses.isEmpty()) {
            return null;
        }
        return guesses.get(guesses.size()-1).guess;
    }

    public boolean isComplete() {
        if(won()) {
            return true;
        }
        if (guesses.size() == word.length()) {
            return true;
        }
        return false;
    }

    public boolean won() {
        final String last = lastGuess();
        return last != null && last.equals(word);
    }

    public boolean lost() {
        return isComplete() && !won();
    }

    private class Guess {

        private final String guess;

        private final List<Position> positions;

        public Guess(String guess, List<Position> positions) {
            this.guess = guess;
            this.positions = positions;
        }

        public void print() {
            positions.forEach(Position::print);
            System.out.print("\n");
        }
    }

    private class Position {

        private final char character;
        private final PositionState state;

        public Position(char character, PositionState state) {
            this.character = character;
            this.state = state;
        }

        public void print() {
            System.out.printf("%s %c %s", state.getColorCode(), character, "\u001B[0m");
        }
    }

    private enum PositionState {
        NOT_FOUND {
            @Override
            public String getColorCode() {
                return ANSIColors.FG_WHITE + ANSIColors.BG_BLACK;
            }
        },
        OUT_OF_PLACE {
            @Override
            public String getColorCode() {
                return ANSIColors.FG_BLACK + ANSIColors.BG_YELLOW;
            }
        },
        CORRECT {
            @Override
            public String getColorCode() {
                return ANSIColors.FG_BLACK + ANSIColors.BG_GREEN;
            }
        },

        EMPTY {
            @Override
            public String getColorCode() {
                return ANSIColors.FG_WHITE + ANSIColors.BG_BLACK;
            }
        };

        public abstract String getColorCode();
    }

}
