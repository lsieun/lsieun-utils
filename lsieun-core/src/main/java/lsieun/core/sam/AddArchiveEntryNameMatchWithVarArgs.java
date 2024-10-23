package lsieun.core.sam;

import lsieun.core.match.text.TextMatch;

@FunctionalInterface
public interface AddArchiveEntryNameMatchWithVarArgs<T> {
    T withEntryName(TextMatch... textMatches);
}
