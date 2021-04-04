package personal.opensrcerer.requestEntities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RandomWord implements BTJReturnable {
    /**
     * Random word retrieved by the BTB API.
     */
    private final String word;

    @JsonCreator
    public RandomWord(@JsonProperty("message") String word) {
        this.word = word;
    }

    /**
     * @return The random word retrieved by the BTB API.
     */
    public String getWord() {
        return word;
    }
}
