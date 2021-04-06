package opensrcerer.requestEntities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RandomText implements BTJReturnable {
    /**
     * Random text retrieved by the BTB API.
     */
    private final String text;

    @JsonCreator
    public RandomText(@JsonProperty("message") String text) {
        this.text = text;
    }

    /**
     * @return The random text retrieved by the BTB API.
     */
    public String getText() {
        return text;
    }
}
