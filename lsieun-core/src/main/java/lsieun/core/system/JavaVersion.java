package lsieun.core.system;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.*;

public final class JavaVersion implements Comparable<JavaVersion> {
    public final int feature;
    public final int minor;
    public final int update;
    public final int build;
    public final boolean ea;
    private static JavaVersion current;
    private static final int MAX_ACCEPTED_VERSION = 25;

    private JavaVersion(int feature, int minor, int update, int build, boolean ea) {
        this.feature = feature;
        this.minor = minor;
        this.update = update;
        this.build = build;
        this.ea = ea;
    }

    public int compareTo(@Nonnull JavaVersion o) {

        int diff = this.feature - o.feature;
        if (diff != 0) {
            return diff;
        } else {
            diff = this.minor - o.minor;
            if (diff != 0) {
                return diff;
            } else {
                diff = this.update - o.update;
                if (diff != 0) {
                    return diff;
                } else {
                    diff = this.build - o.build;
                    return diff != 0 ? diff : (this.ea ? 0 : 1) - (o.ea ? 0 : 1);
                }
            }
        }
    }

    public boolean isAtLeast(int feature) {
        return this.feature >= feature;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof JavaVersion)) {
            return false;
        } else {
            JavaVersion other = (JavaVersion)o;
            return this.feature == other.feature && this.minor == other.minor && this.update == other.update && this.build == other.build && this.ea == other.ea;
        }
    }

    public int hashCode() {
        int hash = this.feature;
        hash = 31 * hash + this.minor;
        hash = 31 * hash + this.update;
        hash = 31 * hash + this.build;
        hash = 31 * hash + (this.ea ? 1231 : 1237);
        return hash;
    }

    public @Nonnull String toFeatureString() {
        return this.formatVersionTo(true, true);
    }

    public @Nonnull String toFeatureMinorUpdateString() {
        return this.formatVersionTo(false, true);
    }

    public String toString() {
        return this.formatVersionTo(false, false);
    }

    private String formatVersionTo(boolean upToFeature, boolean upToUpdate) {
        StringBuilder sb = new StringBuilder();
        if (this.feature > 8) {
            sb.append(this.feature);
            if (!upToFeature) {
                if (this.minor > 0 || this.update > 0) {
                    sb.append('.').append(this.minor);
                }

                if (this.update > 0) {
                    sb.append('.').append(this.update);
                }

                if (!upToUpdate) {
                    if (this.ea) {
                        sb.append("-ea");
                    }

                    if (this.build > 0) {
                        sb.append('+').append(this.build);
                    }
                }
            }
        } else {
            sb.append("1.").append(this.feature);
            if (!upToFeature) {
                if (this.minor > 0 || this.update > 0 || this.ea || this.build > 0) {
                    sb.append('.').append(this.minor);
                }

                if (this.update > 0) {
                    sb.append('_').append(this.update);
                }

                if (!upToUpdate) {
                    if (this.ea) {
                        sb.append("-ea");
                    }

                    if (this.build > 0) {
                        sb.append("-b").append(this.build);
                    }
                }
            }
        }

        return sb.toString();
    }

    public static @Nonnull JavaVersion compose(int feature, int minor, int update, int build, boolean ea) throws IllegalArgumentException {
        if (feature < 0) {
            throw new IllegalArgumentException();
        } else if (minor < 0) {
            throw new IllegalArgumentException();
        } else if (update < 0) {
            throw new IllegalArgumentException();
        } else if (build < 0) {
            throw new IllegalArgumentException();
        } else {
            return new JavaVersion(feature, minor, update, build, ea);
        }
    }

    public static @Nonnull JavaVersion compose(int feature) {
        return compose(feature, 0, 0, 0, false);
    }

    public static @Nonnull JavaVersion current() {
        if (current == null) {
            JavaVersion fallback = parse(System.getProperty("java.version"));
            JavaVersion rt = rtVersion();
            if (rt == null) {
                try {
                    rt = parse(System.getProperty("java.runtime.version"));
                } catch (Throwable ignored) {
                }
            }

            current = rt != null && rt.feature == fallback.feature && rt.minor == fallback.minor ? rt : fallback;
        }

        return current;
    }

    private static @Nullable JavaVersion rtVersion() {
        try {
            Object version = Runtime.class.getMethod("version").invoke((Object)null);
            int major = (Integer)version.getClass().getMethod("major").invoke(version);
            int minor = (Integer)version.getClass().getMethod("minor").invoke(version);
            int security = (Integer)version.getClass().getMethod("security").invoke(version);
            Object buildOpt = version.getClass().getMethod("build").invoke(version);
            int build = (Integer)buildOpt.getClass().getMethod("orElse", Object.class).invoke(buildOpt, 0);
            Object preOpt = version.getClass().getMethod("pre").invoke(version);
            boolean ea = (Boolean)preOpt.getClass().getMethod("isPresent").invoke(preOpt);
            return new JavaVersion(major, minor, security, build, ea);
        } catch (Throwable var8) {
            return null;
        }
    }

    public static @Nonnull JavaVersion parse(@Nonnull String versionString) throws IllegalArgumentException {

        String str = versionString.trim();
        Map<String, String> trimmingMap = new HashMap<>();
        trimmingMap.put("Runtime Environment", "(build ");
        trimmingMap.put("OpenJ9", "version ");
        trimmingMap.put("GraalVM", "Java ");
        Iterator<String> it = trimmingMap.keySet().iterator();

        int length;
        while(it.hasNext()) {
            String keyToDetect = (String)it.next();
            if (str.contains(keyToDetect)) {
                length = str.indexOf((String)trimmingMap.get(keyToDetect));
                if (length > 0) {
                    str = str.substring(length);
                }
            }
        }

        List<String> numbers = new ArrayList<>();
        List<String> separators = new ArrayList<>();
        length = str.length();
        int p = 0;

        int feature;
        for(boolean number = false; p < length; number = !number) {
            for(feature = p; p < length && Character.isDigit(str.charAt(p)) == number; ++p) {
            }

            String part = str.substring(feature, p);
            (number ? numbers : separators).add(part);
        }

        if (!numbers.isEmpty() && !separators.isEmpty()) {
            try {
                feature = Integer.parseInt((String)numbers.get(0));
                int minor = 0;
                int update = 0;
                int build = 0;
                boolean ea = false;
                String s;
                if (feature >= 5 && feature < 25) {
                    for(p = 1; p < separators.size() && ".".equals(separators.get(p)); ++p) {
                    }

                    if (p > 1 && numbers.size() > 2) {
                        minor = Integer.parseInt((String)numbers.get(1));
                        update = Integer.parseInt((String)numbers.get(2));
                    }

                    if (p < separators.size()) {
                        s = (String)separators.get(p);
                        if (s != null && !s.isEmpty() && s.charAt(0) == '-') {
                            ea = startsWithWord(s, "-ea") || startsWithWord(s, "-internal");
                            if (p < numbers.size() && s.charAt(s.length() - 1) == '+') {
                                build = Integer.parseInt((String)numbers.get(p));
                            }

                            ++p;
                        }

                        if (build == 0 && p < separators.size() && p < numbers.size() && "+".equals(separators.get(p))) {
                            build = Integer.parseInt((String)numbers.get(p));
                        }
                    }

                    return new JavaVersion(feature, minor, update, build, ea);
                }

                if (feature == 1 && numbers.size() > 1 && separators.size() > 1 && ".".equals(separators.get(1))) {
                    feature = Integer.parseInt((String)numbers.get(1));
                    if (feature <= 25) {
                        if (numbers.size() > 2 && separators.size() > 2 && ".".equals(separators.get(2))) {
                            minor = Integer.parseInt((String)numbers.get(2));
                            if (numbers.size() > 3 && separators.size() > 3 && "_".equals(separators.get(3))) {
                                update = Integer.parseInt((String)numbers.get(3));
                                if (separators.size() > 4) {
                                    s = (String)separators.get(4);
                                    if (s != null && !s.isEmpty() && s.charAt(0) == '-') {
                                        ea = startsWithWord(s, "-ea") || startsWithWord(s, "-internal");
                                    }

                                    for(p = 4; p < separators.size() && !((String)separators.get(p)).endsWith("-b"); ++p) {
                                    }

                                    if (p < numbers.size()) {
                                        build = Integer.parseInt((String)numbers.get(p));
                                    }
                                }
                            }
                        }

                        return new JavaVersion(feature, minor, update, build, ea);
                    }
                }
            } catch (NumberFormatException var14) {
            }
        }

        throw new IllegalArgumentException(versionString);
    }

    private static boolean startsWithWord(String s, String word) {
        return s.startsWith(word) && (s.length() == word.length() || !Character.isLetterOrDigit(s.charAt(word.length())));
    }

    public static @Nullable JavaVersion tryParse(String versionString) {
        if (versionString != null) {
            try {
                return parse(versionString);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return null;
    }
}