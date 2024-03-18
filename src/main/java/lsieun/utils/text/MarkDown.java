package lsieun.utils.text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MarkDown {
    public static List<String> addText(List<String> list, String content) {
        int index = findLine(list);
        int size = list.size();

        List<String> newList = new ArrayList<>();
        for (int i = 0; i <= index; i++) {
            String line = list.get(i);
            newList.add(line);
        }

        newList.add("");
        newList.add(content);
//        newList.add("");

        for (int i = index + 1; i < size; i++) {
            String line = list.get(i);
            newList.add(line);
        }

        return newList;
    }

    private static int findLine(List<String> list) {
        int size = list.size();

        int count = 0;
        for (int i = 0; i < size; i++) {
            String line = list.get(i);

            if ("---".equals(line)) {
                count++;
            }

            if (count == 2) {
                return i;
            }
        }

        throw new RuntimeException("can not find '---'");
    }

    private static int findNoneBlankIndex(List<String> list, int from) {
        int size = list.size();

        for (int i = from; i < size; i++) {
            String line = list.get(i);

            if (!line.trim().equals("")) {
                return i;
            }
        }

        return size - 1;
    }

    public static List<String> removeLineByPrefix(List<String> list, String prefix) {
        return list.stream()
                .filter(line -> !line.startsWith(prefix))
                .collect(Collectors.toList());
    }

    public static List<String> normalizeBlank(List<String> list) {
        int index = findLine(list);
        int nextIndex = findNoneBlankIndex(list, index + 1);

        List<String> newList = new ArrayList<>();
        for (int i = 0; i <= index; i++) {
            String line = list.get(i);
            newList.add(line);
        }

        newList.add("");
        newList.add("");

        int lastRow = findLastNonBlankRow(list);

        for (int i = nextIndex; i < lastRow; i++) {
            String line = list.get(i);
            newList.add(line);
        }

        return newList;
    }

    private static int findLastNonBlankRow(List<String> list) {
        int size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            String line = list.get(i);
            if (!line.trim().equals("")) {
                return i + 1;
            }
        }

        return size;
    }
}
