package com.appprojects.arindam.synesthesia.util;

/**
 * Utility class for joining arrays or collection of strings
 * with specified delimiters and prefixes.
 * @author Arindam Das
 * @version 29-12-2017.
 */
public final class StringJoiner {

    /*Instance vars of class StringJoiner*/
    private StringBuilder buffer;
    private String delimiter, prefix, suffix;
    private int size;

    /**
     * Constructs a new instance of String joiner
     * @param delimiter the delimiter to be used for delimiting the tokens
     * @param prefix prefix string for the compiled string
     * @param suffix suffix string for the compiled string
     */
    public StringJoiner(String delimiter, String prefix, String suffix) {
        this.delimiter = delimiter;
        this.prefix = prefix;
        this.suffix = suffix; reset();
    }

    /**
     * Cascading constructor. Implemented as : <br>
     * <code>
     *     this(delimiter, "", "")
     * </code>
     * @param delimiter the delimiter to be used for delimiting the tokens
     */
    public StringJoiner(String delimiter){
        this(delimiter, "", "");
    }

    /**
     * Resets the contents of the StringJoiner.
     */
    public void reset() {
        this.buffer = new StringBuilder()
                .append(suffix)
                .insert(0, prefix);
        size = 0;
    }


    /**
     * Returns the string contained with this instance.
     * @return the string compilation of all the objects
     */
    @Override
    public String toString() {
        return buffer.toString();
    }


    /**
     * Adds a new token to the invoking StringJoiner instance
     * @param string The object whose string representation is to be joined to this StringJoiner
     * @return the modified instance after joining this object
     */
    public StringJoiner add(String string) {
        int offset = buffer.lastIndexOf(suffix);
        offset = offset > -1 ? offset : buffer.length();
        this.buffer.insert(offset, string);
        if(size() > 0)
            this.buffer.insert(offset, delimiter);
        size++;
        return this;
    }

    /**
     * Returns the number of tokens contained within this StringJoiner instance.
     * @return the number of tokens as an int
     */
    public int size() { return size; }

    /**
     * Joins all the elements of this iterable into a compiled string.
     * @param iterable source of tokens
     * @return the string result
     */
    public <T> String join(Iterable<T> iterable) {
        for(T t : iterable)
            this.add(t.toString());
        return toString();
    }

    /**
     * Joins all the elements of this iterable into a compiled string.
     * @param iterable source of tokens
     * @return the string result
     */
    public <T> String join(T[] iterable) {
        for(T t : iterable)
            this.add(t.toString());
        return toString();
    }
}
