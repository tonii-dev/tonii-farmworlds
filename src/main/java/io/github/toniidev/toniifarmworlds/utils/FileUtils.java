package io.github.toniidev.toniifarmworlds.utils;

import java.io.File;

public class FileUtils {
    public static void copyFolder(File source, File target) throws Exception{
        if (source.isDirectory()) {
            if (!target.exists() && !target.mkdirs()) {
                throw new Exception("Impossible to create folder " + target.getName());
            }
            String[] children = source.list();
            if (children != null) {
                for (String child : children) {
                    copyFolder(new File(source, child), new File(target, child));
                }
            }
        } else {
            java.nio.file.Files.copy(source.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
