package lsieun.base.text.escape.html;

import lsieun.base.text.escape.Escaper;
import lsieun.base.text.escape.Escapers;

public final class HtmlEscapers {
    private static final Escaper HTML_ESCAPER = Escapers.builder()
            .addEscape('"', "&quot;")
            .addEscape('\'', "&#39;")
            .addEscape('&', "&amp;")
            .addEscape('<', "&lt;")
            .addEscape('>', "&gt;").build();

    public static Escaper htmlEscaper() {
        return HTML_ESCAPER;
    }

    private HtmlEscapers() {
    }
}