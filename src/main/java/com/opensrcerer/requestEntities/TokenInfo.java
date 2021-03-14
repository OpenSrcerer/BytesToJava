package com.opensrcerer.requestEntities;

public class TokenInfo implements BTJReturnable {

    /**
     * The token that is used in a BTB instance to access the API.
     */
    private final String token;

    /**
     * Represents the uses of this token in the allowed timeslot.
     */
    private int uses;

    /**
     * Represents the usage of the token in the past minute.
     * (According to BTB API server time).
     * If limit is -1, the token should not be ratelimited.
     */
    private int limit;

    public TokenInfo(final String token) {
        this.token = token;
    }

    /**
     * @return The token for the BTB API.
     */
    public String getToken() {
        return token;
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
     * @return True if token has no ratelimit, false otherwise.
     */
    public boolean isUnlimited() {
        return limit == -1;
    }

    /**
     * @param uses Uses of token to set.
     * @param limit Limit of token to set.
     */
    public void setData(int uses, int limit) {
        this.uses = uses;
        this.limit = limit;
    }
}
