package typingapp.util;

import java.awt.Dimension;

public final class WindowSettings {

    private static int width = 1024;
    private static int height = 576;

    private static int fontSize = 16;

    private static boolean fullScreen = false;

    private WindowSettings() {
        // インスタンス化禁止
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static Dimension getSize() {
        return new Dimension(width, height);
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static boolean isFullScreen() {
        return fullScreen;
    }

    public static void setSize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
        fullScreen = false;
    }

    public static void setFontSize(int newFontSize) {
        fontSize = newFontSize;
    }

    public static void setFullScreen(boolean value) {
        fullScreen = value;
    }
}