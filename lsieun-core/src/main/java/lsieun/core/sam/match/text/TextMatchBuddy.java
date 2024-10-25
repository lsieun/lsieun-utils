package lsieun.core.sam.match.text;

public interface TextMatchBuddy {
    static TextMatch byFileExtension(String... exts) {
        String multipleExt = String.join("|", exts);
        String regex = String.format("([^\\s]+(\\.(?i)(%s))$)", multipleExt);
        return TextMatch.matches(regex);
    }

    static TextMatch ignoreThirdParty() {
        return (text) -> !(
                text.contains("javax")
                        || text.contains("groovy")
                        || text.contains("/ktor/")
                        || text.startsWith("com/amazon/")
                        || text.startsWith("com/esotericsoftware/")
                        || text.startsWith("com/fasterxml/")
                        || text.startsWith("com/github/")
                        || text.startsWith("com/google/")
                        || text.startsWith("com/ibm/")
                        || text.startsWith("com/jcraft/jzlib/")
                        || text.startsWith("com/sun/")
                        || text.startsWith("com/thaiopensource/")
                        || text.startsWith("io/grpc/")
                        || text.startsWith("io/opentelemetry/")
                        || text.startsWith("io/netty/")
                        || text.startsWith("net/sf/cglib/")
                        || text.startsWith("org/apache/")
                        || text.startsWith("org/bouncycastle/")
                        || text.startsWith("org/codehaus/")
                        || text.startsWith("org/eclipse/")
                        || text.startsWith("org/h2/")
                        || text.startsWith("org/jaxen/")
                        || text.startsWith("org/mozilla/")
                        || text.startsWith("org/objectweb/asm/")
                        || text.startsWith("org/openjdk/")
                        || text.startsWith("org/sqlite/")
                        || text.startsWith("org/w3c/")
        );
    }
}
