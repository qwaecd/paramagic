package com.qwaecd.paramagic.tools;

public final class BitmaskUtils {
    private BitmaskUtils() {}

    /**
     * 检查给定的整数中是否设置了特定的标志。
     *
     * @param currentFlags 当前的整数值，代表所有标志的状态。
     * @param flagToCheck  要检查的单个标志（掩码）。
     * @return 如果标志已设置，则返回 true；否则返回 false。
     */
    public static boolean hasFlag(int currentFlags, int flagToCheck) {
        return (currentFlags & flagToCheck) == flagToCheck;
    }

    /**
     * 在给定的整数中设置一个或多个标志。
     *
     * @param currentFlags 当前的整数值。
     * @param flagToSet    要设置的一个或多个标志（掩码）。
     * @return 更新后的整数值。
     */
    public static int setFlag(int currentFlags, int flagToSet) {
        return currentFlags | flagToSet;
    }

    /**
     * 在给定的整数中清除一个或多个标志。
     *
     * @param currentFlags  当前整数值。
     * @param flagToClear   要清除的一个或多个标志（掩码）。
     * @return 更新后的整数值。
     */
    public static int clearFlag(int currentFlags, int flagToClear) {
        return currentFlags & ~flagToClear;
    }

    /**
     * 根据布尔值来设置或清除一个标志。
     * 这是一个方便的方法，用于将布尔变量直接映射到位标志。
     *
     * @param currentFlags 当前整数值。
     * @param flag         要操作的标志。
     * @param shouldSet    如果为 true，则设置标志；如果为 false，则清除标志。
     * @return 更新后的整数值。
     */
    public static int setFlag(int currentFlags, int flag, boolean shouldSet) {
        return shouldSet ? setFlag(currentFlags, flag) : clearFlag(currentFlags, flag);
    }

    /**
     * 在给定的整数中翻转一个或多个标志的状态。
     *
     * @param currentFlags   当前整数值。
     * @param flagToToggle   要翻转的标志（掩码）。
     * @return 更新后的整数值。
     */
    public static int toggleFlag(int currentFlags, int flagToToggle) {
        return currentFlags ^ flagToToggle;
    }
}
