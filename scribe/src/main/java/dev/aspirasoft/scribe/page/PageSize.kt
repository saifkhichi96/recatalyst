package dev.aspirasoft.scribe.page

import dev.aspirasoft.scribe.utils.mmToPixels

/**
 * @author saifkhichi96
 * @since 1.0.0
 */
enum class PageSize(width: Int, height: Int) {

    // ISO Series A
    A0(841, 1189),
    A1(594, 841),
    A2(420, 594),
    A3(297, 420),
    A4(210, 297),
    A5(148, 210),
    A6(105, 148),

    // ISO Series B
    B0(1000, 1414),
    B1(707, 1000),
    B2(500, 707),
    B3(353, 500),
    B4(250, 353),
    B5(176, 250),
    B6(125, 176),

    // ISO Series C
    C0(917, 1297),
    C1(648, 917),
    C2(458, 648),
    C3(324, 458),
    C4(229, 324),
    C5(162, 229),
    C6(114, 162),

    // ANSI Paper Sizes
    ANSI_A(216, 279),
    ANSI_B(279, 432),
    ANSI_C(432, 559),
    ANSI_D(559, 864),
    ANSI_E(864, 1118),

    // Traditional North American Paper Sizes
    LEGAL(216, 356),
    LEGAL_JUNIOR(203, 127),
    LETTER(216, 279),
    TABLOID(279, 432);

    val width = mmToPixels(width.toFloat())
    val height = mmToPixels(height.toFloat())

}