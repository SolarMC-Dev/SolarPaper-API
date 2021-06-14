package org.bukkit.block;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * Represents a captured state of either a SignPost or a WallSign.
 */
// Solar start - add adventure to whole class
public interface Sign extends BlockState {

    /**
     * Gets all the lines of text currently on this sign.
     *
     * @return a list of components for each line of text
     */
    @NonNull
    List<Component> lines();

    /**
     * Gets the line of text at the specified index. <br>
     * <br>
     * For example, getLine(0) will return the first line of text.
     *
     * @param index line index to get the text from
     * @return text on the given line
     * @throws IndexOutOfBoundsException if the line index does not exist
     */
    @NonNull
    Component line(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the line of text at the specified index. <br>
     * <br>
     * For example, setLine(0, Component.text("Line One")) will set the first line
     * of text to "Line One".
     *
     * @param index line index to set the text at
     * @param line the new text to place at the given line
     * @throws IndexOutOfBoundsException if the line index does not exist
     */
    void line(int index, @NonNull Component line) throws IndexOutOfBoundsException;

    /**
     * Gets all the lines of text currently on this sign.
     *
     * @return Array of Strings containing each line of text
     * @deprecated Use the adventure {@link #lines()}
     */
    @Deprecated
    String[] getLines();

    /**
     * Gets the line of text at the specified index.
     * <p>
     * For example, getLine(0) will return the first line of text.
     *
     * @param index Line number to get the text from, starting at 0
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @return Text on the given line
     * @deprecated Use the adventure {@link #line(int)}
     */
    @Deprecated
    String getLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the line of text at the specified index.
     * <p>
     * For example, setLine(0, "Line One") will set the first line of text to
     * "Line One".
     *
     * @param index Line number to set the text at, starting from 0
     * @param line New text to set at the specified index
     * @throws IndexOutOfBoundsException If the index is out of the range 0..3
     * @deprecated Use the adventure {@link #line(int, Component)}
     */
    @Deprecated
    void setLine(int index, String line) throws IndexOutOfBoundsException;
}
// Solar end
