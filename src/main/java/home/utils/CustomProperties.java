/*******************************************************************************
 * Copyright 2021-2026 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Properties without date comment for
 * {@link java.util.Properties#store(OutputStream, String) store} method and
 * with sorting of keys
 */
public final class CustomProperties extends Properties {

    private static final long serialVersionUID = 693357491710031108L;

    //// code for deleting a comment

    @Override
    public void store(OutputStream out, String comments)
            throws IOException {
        throw new UnsupportedOperationException("This method will not be implemented "
                + "for this class. Please use 'CustomProperties.store(OutputStream out)' instead.");
    }

    public void store(OutputStream out) throws IOException {
        store0(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8.toString())),
                null, false, true);
    }

    /**
     * Modification of parent method:
     * {@link java.util.Properties#store0(BufferedWriter, String, boolean) store0}
     */
    private void store0(BufferedWriter bw, String comments, boolean addDate, boolean escUnicode)
            throws IOException {
        if (comments != null) {
            writeComments(bw, comments);
        }
        if (addDate) {
            bw.write("#" + new Date().toString());
            bw.newLine();
        }
        synchronized (this) {
            for (Entry<Object, Object> e : entrySet()) {
                String key = (String) e.getKey();
                String val = (String) e.getValue();
                key = saveConvert(key, true, escUnicode);
                /*
                 * No need to escape embedded and trailing spaces for value, hence pass false to
                 * flag.
                 */
                val = saveConvert(val, false, escUnicode);
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
    }

    /**
     * Copy of parent method:
     * {@link java.util.Properties#writeComments(BufferedWriter, String)
     * writeComments}
     */
    private static void writeComments(BufferedWriter bw, String comments) throws IOException {
        HexFormat hex = HexFormat.of().withUpperCase();
        bw.write("#");
        int len = comments.length();
        int current = 0;
        int last = 0;
        while (current < len) {
            char c = comments.charAt(current);
            if (c > '\u00ff' || c == '\n' || c == '\r') {
                if (last != current) {
                    bw.write(comments.substring(last, current));
                }
                if (c > '\u00ff') {
                    bw.write("\\u");
                    bw.write(hex.toHexDigits(c));
                } else {
                    bw.newLine();
                    if (c == '\r' &&
                            current != len - 1 &&
                            comments.charAt(current + 1) == '\n') {
                        current++;
                    }
                    if (current == len - 1 ||
                            (comments.charAt(current + 1) != '#' &&
                            comments.charAt(current + 1) != '!')) {
                        bw.write("#");
                    }
                }
                last = current + 1;
            }
            current++;
        }
        if (last != current) {
            bw.write(comments.substring(last, current));
        }
        bw.newLine();
    }

    /**
     * Converts unicodes to encoded &#92;uxxxx and escapes special characters with a
     * preceding slash
     *
     * Copy of parent method:
     * {@link java.util.Properties#saveConvert(String, boolean, boolean)
     * saveConvert}
     */
    private String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder outBuffer = new StringBuilder(bufLen);
        HexFormat hex = HexFormat.of().withUpperCase();
        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ':
                    if (x == 0 || escapeSpace) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    break;
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                case '=': // $FALL-THROUGH$
                case ':': // $FALL-THROUGH$
                case '#': // $FALL-THROUGH$
                case '!':
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
                        outBuffer.append("\\u");
                        outBuffer.append(hex.toHexDigits(aChar));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    //// key sort code

    /**
     * Modification of parent method: {@link java.util.Properties#keys() keys}
     */
    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(getKeys());
    }

    /**
     * Modification of parent method: {@link java.util.Properties#keySet() keySet}
     */
    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(getKeys());
    }

    private Set<Object> getKeys() {
        return new TreeSet<Object>(super.keySet());
    }

    /**
     * Modification of parent method: {@link java.util.Properties#entrySet()
     * entrySet}
     */
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        Set<Entry<Object, Object>> set1 = super.entrySet();
        Set<Entry<Object, Object>> set2 = new LinkedHashSet<Entry<Object, Object>>(set1.size());

        Iterator<Entry<Object, Object>> iterator = set1.stream()
                .sorted((o1, o2) -> o1.getKey().toString().compareTo(o2.getKey().toString()))
                .iterator();

        while (iterator.hasNext()) {
            set2.add(iterator.next());
        }

        return set2;
    }
}