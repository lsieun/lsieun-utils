package lsieun.utils.asm.utils;

import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;
import lsieun.utils.asm.visitor.find.*;
import lsieun.utils.core.archive.jar.JarUtils;
import lsieun.utils.core.text.RegexUtils;
import org.objectweb.asm.ClassReader;

import java.util.ArrayList;
import java.util.List;

public class FindUtils {
    private static final int PARSING_OPTIONS = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;

    private static List<String> filter(String jar_path, String[] path_regex_array) {
        List<String> list = JarUtils.getClassEntries(jar_path);
        RegexUtils.filter(list, path_regex_array);
        return list;
    }

    public static List<MatchItem> findInterface(String jar_path, String[] path_regex_array, String[] interface_name_regex_array) {
        List<String> list = filter(jar_path, path_regex_array);

        List<MatchItem> resultList = new ArrayList<>();
        for (String item : list) {
            byte[] bytes = JarUtils.readClass(jar_path, item);
            ClassReader cr = new ClassReader(bytes);
            ClassRegexVisitor cv = new FindInterfaceRegexVisitor(interface_name_regex_array, null);
            cr.accept(cv, PARSING_OPTIONS);
            resultList.addAll(cv.resultList);
        }
        return resultList;
    }

    public static List<MatchItem> findSuper(String jar_path, String[] path_regex_array, String[] super_name_regex_array) {
        List<String> list = filter(jar_path, path_regex_array);

        List<MatchItem> resultList = new ArrayList<>();
        for (String item : list) {
            byte[] bytes = JarUtils.readClass(jar_path, item);
            ClassReader cr = new ClassReader(bytes);
            ClassRegexVisitor cv = new FindSuperRegexVisitor(super_name_regex_array);
            cr.accept(cv, PARSING_OPTIONS);
            resultList.addAll(cv.resultList);
        }
        return resultList;
    }

    public static List<MatchItem> findField(String jar_path, String[] path_regex_array, String[] includes, String[] excludes) {
        List<String> list = filter(jar_path, path_regex_array);

        List<MatchItem> resultList = new ArrayList<>();
        for (String item : list) {
            byte[] bytes = JarUtils.readClass(jar_path, item);
            ClassReader cr = new ClassReader(bytes);
            ClassRegexVisitor cv = new FindFieldRegexVisitor(includes, excludes);
            cr.accept(cv, PARSING_OPTIONS);
            resultList.addAll(cv.resultList);
        }
        return resultList;
    }

    /**
     * <b>path_regex_array</b>示例如下：
     * <ul>
     *     <li>部分路径：^com/jetbrains/ls/\\w+/\\w+\\.class$</li>
     *     <li>完整路径：^com/example/abc/xyz/HelloWorld\\.class$  （注意：“.”要进行转义）</li>
     * </ul>
     * <b>name_desc_regex_array</b>的值示例如下：
     * <ul>
     *      <li></li>
     *      <li></li>
     * </ul>
     * <b></b>
     *
     * @param path_regex_array path regex
     * @param includes         包含哪些方法
     * @param excludes         不包含哪些方法
     */
    public static List<MatchItem> findMethod(String jar_path, String[] path_regex_array, String[] includes, String[] excludes) {
        List<String> list = filter(jar_path, path_regex_array);

        List<MatchItem> resultList = new ArrayList<>();
        for (String item : list) {
            byte[] bytes = JarUtils.readClass(jar_path, item);
            ClassReader cr = new ClassReader(bytes);
            ClassRegexVisitor cv = new FindMethodRegexVisitor(includes, excludes);
            cr.accept(cv, PARSING_OPTIONS);
            resultList.addAll(cv.resultList);
        }
        return resultList;
    }

    public static List<MatchItem> findMethodRef(String jar_path, String[] path_regex_array,
                                                String refClassName, String[] includes, String[] excludes) {
        List<String> list = filter(jar_path, path_regex_array);

        List<MatchItem> resultList = new ArrayList<>();
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;

        for (String str : list) {
            byte[] bytes = JarUtils.readClass(jar_path, str);
            ClassReader cr = new ClassReader(bytes);
            FindMethodRefRegexVisitor cv = new FindMethodRefRegexVisitor(refClassName, includes, excludes);
            cr.accept(cv, parsingOptions);
            resultList.addAll(cv.resultList);
        }
        return resultList;
    }

    public static void displayResult(List<MatchItem> resultList) {
        if (resultList == null || resultList.size() < 1) return;

        for (int i = 0; i < resultList.size(); i++) {
            MatchItem result = resultList.get(i);
            System.out.println(String.format("(%s)ClassName: %s", (i + 1), result));
        }
    }

    public static void main(String[] args) {
        String jar_path = "D:/tmp/target.jar";
        List<MatchItem> list = findInterface(jar_path, new String[]{
                "^\\w+_you/.*\\.class$"
        }, new String[]{
                "^java/lang/instrument/ClassFileTransformer$"
        });
        displayResult(list);
    }
}
