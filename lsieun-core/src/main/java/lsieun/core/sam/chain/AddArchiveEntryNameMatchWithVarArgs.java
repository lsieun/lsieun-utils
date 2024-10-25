package lsieun.core.sam.chain;

import lsieun.core.sam.match.text.TextMatch;

@FunctionalInterface
public interface AddArchiveEntryNameMatchWithVarArgs<T> {
    T withEntryName(TextMatch... textMatches);
}
