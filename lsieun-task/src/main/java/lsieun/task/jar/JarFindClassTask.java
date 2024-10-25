package lsieun.task.jar;

import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.description.MemberDesc;
import lsieun.base.coll.ListUtils;
import lsieun.base.io.dir.DirNioUtils;
import lsieun.base.log.LogLevel;
import lsieun.base.log.Logger;
import lsieun.core.sam.match.archive.ZipEntryMatch;
import lsieun.core.sam.match.archive.ZipFileSystemMatch;
import lsieun.core.sam.match.path.FilePathMatch;
import lsieun.core.sam.match.text.TextMatch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class JarFindClassTask {
    public static void findClassByLine(Path dirPath, boolean quick, String stackFrameLine) throws IOException {
        MemberDesc memberDesc = MemberDesc.of(stackFrameLine);
        String entry = AsmTypeNameUtils.toJarEntryName(memberDesc.owner());
        TextMatch textMatch = TextMatch.endsWith(entry);
        find(dirPath, quick, textMatch);
    }

    public static void find(Path dirPath, boolean quick, String infix) throws IOException {
        TextMatch textMatch = TextMatch.contains(infix);
        find(dirPath, quick, textMatch);
    }

    public static void find(Path dirPath, boolean quick, TextMatch textMatch) {
        List<Path> jarList = DirNioUtils.findFileListInDirByExt(dirPath, ".jar");
        List<Path> candidateList = jarList.stream()
                .filter(
                        path -> FilePathMatch.byZipFileSystem(
                                ZipFileSystemMatch.byZipEntry(
                                        ZipEntryMatch.byText(
                                                textMatch
                                        ),
                                        quick
                                )
                        ).test(path)
                )
                .toList();
        ListUtils.print(candidateList);
    }

    public static void main(String[] args) throws IOException {
        Logger.CURRENT_LEVEL = LogLevel.DEBUG;
        Path dirPath = Path.of("D:\\tmp\\intellij");
//        find(dirPath, "analysis");
        findClassByLine(dirPath, true, "com.intellij.ide.Q.c::b:(Ljava/nio/file/Path;)Ljava/util/Map$Entry;:bci=164");
//        find(dirPath, false,
//                MatchLogic.and(
//                        TextMatch.lookup(),
//                        TextMatch.class,
//                        List.of(
//                                TextMatch.containsIgnoreCase("logic"),
//                                TextMatch.containsIgnoreCase("and"),
//                                TextMatch.endsWith(".class")
//                        )
//                )
//        );
    }
}
