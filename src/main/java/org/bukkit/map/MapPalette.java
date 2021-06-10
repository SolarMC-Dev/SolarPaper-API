package org.bukkit.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import static org.bukkit.map.internal.MapPaletteColors.colors;

/**
 * Represents the palette that map items use.
 * <p>
 * These fields are hee base color ranges. Each entry corresponds to four
 * colors of varying shades with values entry to entry + 3.
 */
public final class MapPalette {
    // Internal mechanisms
    private MapPalette() {}

    private static double getDistance(Color c1, Color c2) {
        double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        double weightR = 2 + rmean / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256.0;
        return weightR * r * r + weightG * g * g + weightB * b * b;
    }

    // static final Color[] colors = ; // Solar - moved to MapPaletteColors

    // Interface
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte TRANSPARENT = 0;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte LIGHT_GREEN = 4;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte LIGHT_BROWN = 8;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte GRAY_1 = 12;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte RED = 16;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte PALE_BLUE = 20;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte GRAY_2 = 24;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte DARK_GREEN = 28;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte WHITE = 32;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte LIGHT_GRAY = 36;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte BROWN = 40;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte DARK_GRAY = 44;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte BLUE = 48;
    /**
     * @deprecated Magic value
     */
    @Deprecated
    public static final byte DARK_BROWN = 52;

    /**
     * Resize an image to 128x128.
     *
     * @param image The image to resize.
     * @return The resized image.
     */
    public static BufferedImage resizeImage(Image image) {
        BufferedImage result = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(image, 0, 0, 128, 128, null);
        graphics.dispose();
        return result;
    }

    /**
     * Convert an Image to a byte[] using the palette.
     *
     * @param image The image to convert.
     * @return A byte[] containing the pixels of the image.
     * @deprecated Magic value
     */
    @Deprecated
    public static byte[] imageToBytes(Image image) {
        BufferedImage temp = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = temp.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        int[] pixels = new int[temp.getWidth() * temp.getHeight()];
        temp.getRGB(0, 0, temp.getWidth(), temp.getHeight(), pixels, 0, temp.getWidth());

        byte[] result = new byte[temp.getWidth() * temp.getHeight()];
        for (int i = 0; i < pixels.length; i++) {
            result[i] = matchColor(new Color(pixels[i], true));
        }
        return result;
    }

    /**
     * Get the index of the closest matching color in the palette to the given
     * color.
     *
     * @param r The red component of the color.
     * @param b The blue component of the color.
     * @param g The green component of the color.
     * @return The index in the palette.
     * @deprecated Magic value
     */
    @Deprecated
    public static byte matchColor(int r, int g, int b) {
        return matchColor(new Color(r, g, b));
    }

    /**
     * Get the index of the closest matching color in the palette to the given
     * color.
     *
     * @param color The Color to match.
     * @return The index in the palette.
     * @deprecated Magic value
     */
    @Deprecated
    public static byte matchColor(Color color) {
        if (color.getAlpha() < 128) return 0;

        int index = 0;
        double best = -1;

        for (int i = 4; i < colors.length; i++) {
            double distance = getDistance(color, colors[i]);
            if (distance < best || best == -1) {
                best = distance;
                index = i;
            }
        }

        // Minecraft has 143 colors, some of which have negative byte representations
        return (byte) (index < 128 ? index : -129 + (index - 127));
    }

    /**
     * Get the value of the given color in the palette.
     *
     * @param index The index in the palette.
     * @return The Color of the palette entry.
     * @deprecated Magic value
     */
    @Deprecated
    public static Color getColor(byte index) {
        if ((index > -49 && index < 0) || index > 127) {
            throw new IndexOutOfBoundsException();
        } else {
            // Minecraft has 143 colors, some of which have negative byte representations
            return colors[index >= 0 ? index : index + 256];
        }
    }
}
