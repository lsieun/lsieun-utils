package lsieun.base.text.escape.xml;

import lsieun.base.text.escape.Escaper;
import lsieun.base.text.escape.Escapers;

public class XmlEscapers {
    private static final char MIN_ASCII_CONTROL_CHAR = '\u0000';
    private static final char MAX_ASCII_CONTROL_CHAR = '\u001f';
    private static final Escaper XML_ESCAPER;
    private static final Escaper XML_CONTENT_ESCAPER;
    private static final Escaper XML_ATTRIBUTE_ESCAPER;

    private XmlEscapers() {
    }

    public static Escaper xmlContentEscaper() {
        return XML_CONTENT_ESCAPER;
    }

    public static Escaper xmlAttributeEscaper() {
        return XML_ATTRIBUTE_ESCAPER;
    }

    static {
        Escapers.Builder builder = Escapers.builder();
        builder.setSafeRange('\u0000', '�');
        builder.setUnsafeReplacement("�");

        for(char c = 0; c <= 31; ++c) {
            if (c != '\t' && c != '\n' && c != '\r') {
                builder.addEscape(c, "�");
            }
        }

        builder.addEscape('&', "&amp;");
        builder.addEscape('<', "&lt;");
        builder.addEscape('>', "&gt;");
        XML_CONTENT_ESCAPER = builder.build();
        builder.addEscape('\'', "&apos;");
        builder.addEscape('"', "&quot;");
        XML_ESCAPER = builder.build();
        builder.addEscape('\t', "&#x9;");
        builder.addEscape('\n', "&#xA;");
        builder.addEscape('\r', "&#xD;");
        XML_ATTRIBUTE_ESCAPER = builder.build();
    }
}