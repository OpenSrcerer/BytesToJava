package com.opensrcerer.requestEntities;

import java.util.List;
import java.util.Objects;

/**
 * Class that encapsulates the data received from JSON requests on the
 * MadLibs endpoint.
 */
public class MadLib implements BTJReturnable {
    /**
     * The total number of variables in this MadLib.
     */
    private final int questions;

    /**
     * The title of this MadLib.
     */
    private final String title;

    /**
     * The title of this MadLib, marked with variables to replace in the form:
     * {1} {2} ... {n}
     */
    private String text;

    /**
     * Contains this MadLib's variable description.
     */
    private final List<String> variables;

    /**
     * Constructs a new MadLib object with the required data.
     */
    public MadLib(int questions, String title,
                  String text, List<String> variables)
    {
        this.questions = questions;
        this.title = title;
        this.text = text;
        this.variables = variables;
    }

    /**
     * @return The number of questions for this MadLib.
     */
    public int getQuestions() {
        return questions;
    }

    /**
     * @return The title of this MadLib.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The text of this MadLib.
     *         Notice that you should use replace or bulkReplace to replace the questions
     *         before calling this method to avoid getting unparsed text.
     * @see    MadLib#bulkReplace(List)
     * @see    MadLib#replace(String, String)
     */
    public String getText() {
        return text;
    }

    /**
     * Replaces a word for a MadLib at a given index. These indexes start at 1.
     * @param questionIndex Index of MadLib word (question).
     * @param replacement Word to replace at this index.
     */
    public void replace(String questionIndex, String replacement) {
        Objects.requireNonNull(questionIndex);
        Objects.requireNonNull(replacement);
        text = text.replace("{" + questionIndex + "}", replacement);
    }

    /**
     * @param toReplace A List of Strings, whose content will be used to replace
     *                  indexes (questions) on the raw MadLib string. The Strings must be in
     *                  the order you want them replaced.
     * @return          A parsed MadLib String. If the List is smaller than the number
     *                  of questions, only the Strings given will be used for replacement
     *                  replaced in. If it is larger, the other words will be ignored.
     * @see             MadLib#bulkReplace(List)
     */
    public String bulkReplace(List<String> toReplace) {
        Objects.requireNonNull(toReplace);
        for (int i = 0; i < toReplace.size(); ++i) {
            text = text.replace("{" + (i + 1) + "}", toReplace.get(i));
        }
        return text;
    }

    /**
     * @return The descriptions of the variables for this MadLib.
     */
    public List<String> getVariables() {
        return variables;
    }
}
