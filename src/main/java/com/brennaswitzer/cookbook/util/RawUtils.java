package com.brennaswitzer.cookbook.util;

import com.brennaswitzer.cookbook.payload.RawIngredientDissection;

public class RawUtils {

    private static final char SPACE = ' ';

    public static int lengthOfLongestSharedSuffix(CharSequence a, CharSequence b) {
        int ai = a.length() - 1, bi = b.length() - 1;
        while (ai >= 0 && bi >= 0 && a.charAt(ai) == b.charAt(bi)) {
            ai--;
            bi--;
        }
        return a.length() - ai - 1;
    }

    public static RawIngredientDissection dissect(String raw) {
        if (raw == null) return null;
        if (raw.trim().isEmpty()) return null;
        RawIngredientDissection d = new RawIngredientDissection(raw);
        NumberUtils.NumberWithRange n = NumberUtils.parseNumberWithRange(raw);
        int pos = 0;
        if (n != null) {
            d.setQuantity(new RawIngredientDissection.Section(
                    raw.substring(n.getStart(), n.getEnd()),
                    n.getStart(),
                    n.getEnd()
            ));
            pos = n.getEnd();
        }
        RawIngredientDissection.Section s = findSection(raw, pos, '_');
        if (s != null) {
            d.setUnits(s);
            pos = s.getEnd();
        }
        s = findSection(raw, pos, '"');
        if (s != null) {
            d.setName(s);
//            pos = s.getEnd();
        }
        return d;
    }

    private static RawIngredientDissection.Section findSection(String str, int pos, char delim) {
        if (pos > 0 && str.charAt(pos - 1) == SPACE) pos -= 1;
        int start = str.indexOf(delim, pos);
        if (start < 0) return null;
        int end = str.indexOf(delim, start + 1);
        if (end < 0) return null;
        return new RawIngredientDissection.Section(
                str.substring(start + 1, end + 1 - 1),
                start,
                end + 1
        );
    }

}
