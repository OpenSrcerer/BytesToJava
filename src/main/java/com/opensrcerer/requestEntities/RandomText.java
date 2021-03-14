package com.opensrcerer.requestEntities;

public class RandomText implements BTJReturnable {
    /**
     * Random text retrieved by the BTB API.
     */
    private final String text;

    public RandomText(String text) {
        this.text = text;
    }

    /**
     * @return The random text retrieved by the BTB API.
     */
    public String getText() {
        return text;
    }
}
