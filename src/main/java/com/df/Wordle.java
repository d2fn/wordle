package com.df;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Wordle {

    private static final String WORDS_PATH = "words.txt";

    public Wordle(String word) {

        final Board board = new Board(word);
        board.print();

        while (!board.isComplete()) {
            Scanner inputReader = new Scanner(System.in);
            System.out.print("> ");
            final String nextGuess = inputReader.nextLine();
            if(nextGuess.length() != word.length()) {
                continue;
            }
            board.guess(nextGuess);
            board.print();
        }
    }

    public static void main(String[] args) throws Exception {

        final int wordLength = Integer.parseInt(args[0]);

        final String word = pickRandomWord(WORDS_PATH, wordLength);

        new Wordle(word);
    }

    private static String pickRandomWord(String wordsPath, int wordLength) throws FileNotFoundException {
        final Scanner s = new Scanner(new File(wordsPath));
        int n = 1;
        String selectedWord = null;
        final Random r = new Random();
        while(s.hasNextLine()) {
            final String nextWord = s.nextLine();
            if(nextWord.length() != wordLength) {
                continue;
            }
            if(n == 1) {
                selectedWord = nextWord;
            }
            else {
                if(r.nextInt(n) == 1) {
                    selectedWord = nextWord;
                }
            }
            n++;
        }
        System.out.println("\u001B[34m" + selectedWord + "\u001B[0m");

        return selectedWord;
    }
}
