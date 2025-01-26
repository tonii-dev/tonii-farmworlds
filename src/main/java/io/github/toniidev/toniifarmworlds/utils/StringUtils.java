package io.github.toniidev.toniifarmworlds.utils;

public class StringUtils {
    /**
     * Edits the given string, so that the colorPrefix gets replaced with the character
     * that in Minecraft must be placed before any color code to give a color to a String
     *
     * @param colorPrefix The character that is in the String and has to be replaced with '§'
     * @param string      The string where the character has to be replaced
     * @return string#replace(% colorPrefix %, ' § ')
     */
    public static String formatColorCodes(char colorPrefix, String string) {
        return string.replace(colorPrefix, '§');
    }
}
