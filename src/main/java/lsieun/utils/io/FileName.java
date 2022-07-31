package lsieun.utils.io;

public class FileName {
    /**
     * 文件是否有扩展名/后缀名
     *
     * @param filename 文件名，例如abc.txt
     * @param ext      后缀名/扩展名，例如txt
     * @return 是否有扩展名
     */
    public static boolean hasExtension(String filename, String ext) {
        if (filename == null) return false;
        if (ext == null) return false;

        int index = filename.lastIndexOf(".");
        if (index < 0) return false;

        String str = filename.substring(index + 1);
        return str.equalsIgnoreCase(ext);
    }
}
