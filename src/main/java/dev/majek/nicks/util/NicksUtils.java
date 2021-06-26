/*
 * This file is part of PaperNicks, licensed under the MIT License.
 *
 * Copyright (c) 2021 Majekdor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.majek.nicks.util;

/**
 * Handles general utility methods.
 */
public class NicksUtils {

  /**
   * Convert a string with legacy codes into a string with MiniMessage tags.
   *
   * @param text Text to search
   * @return String with MiniMessage tags.
   */
  public String legacyToMini(String text) {
    text = text.replace("&0", "<black>");
    text = text.replace("&1", "<dark_blue>");
    text = text.replace("&2", "<dark_green>");
    text = text.replace("&3", "<dark_aqua>");
    text = text.replace("&4", "<dark_red>");
    text = text.replace("&5", "<dark_purple>");
    text = text.replace("&6", "<gold>");
    text = text.replace("&7", "<gray>");
    text = text.replace("&8", "<dark_gray>");
    text = text.replace("&9", "<blue>");
    text = text.replace("&a", "<green>");
    text = text.replace("&b", "<aqua>");
    text = text.replace("&c", "<red>");
    text = text.replace("&d", "<light_purple>");
    text = text.replace("&e", "<yellow>");
    text = text.replace("&f", "<white>");
    text = text.replace("&m", "<underlined>");
    text = text.replace("&m", "<strikethrough>");
    text = text.replace("&k", "<obfuscated>");
    text = text.replace("&o", "<italic>");
    text = text.replace("&l", "<bold>");
    text = text.replace("&r", "<reset>");
    return text;
  }
}
