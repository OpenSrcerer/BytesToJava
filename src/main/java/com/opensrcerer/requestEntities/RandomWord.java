package com.opensrcerer.requestEntities;

public class RandomWord implements BTJReturnable {
    /**
     * Random word retrieved by the BTB API.
     */
    private final String word;

    public RandomWord(String word) {
        this.word = word;
    }

    /**
     * @return The random word retrieved by the BTB API.
     */
    public String getWord() {
        return word;
    }
}
