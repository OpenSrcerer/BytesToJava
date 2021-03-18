package github.opensrcerer.requestEntities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class TokenInfo implements BTJReturnable {

    /**
     * Represents the uses of this token in the allowed timeslot.
     */
    private final int uses;

    /**
     * Represents the usage of the token in the past minute.
     * (According to BTB API server time).
     * If limit is -1, the token should not be ratelimited.
     */
    private final int limit;

    /**
     * Time until the next use reset of the provided token.
     */
    private final int nextReset;

    @JsonCreator
    public TokenInfo(@JsonProperty("uses") int uses, @JsonProperty("limit") int limit, @JsonProperty("next_reset") int nextReset) {
        this.uses = uses;
        this.limit = limit;
        this.nextReset = nextReset;
    }

    /**
     * @return The uses of this token in the allowed timeslot.
     */
    public int getUses() {
        return uses;
    }

    /**
     * @return The usage of the token in the past minute.
     * (According to BTB API server time).
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @return The time until the next use reset of the provided token (in seconds).
     */
    public int getNextReset() {
        return nextReset;
    }

    /**
     * @return True if token has no ratelimit, false otherwise.
     */
    public boolean isUnlimited() {
        return limit == -1;
    }
}
