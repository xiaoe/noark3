/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 *        http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.util;

import xyz.noark.core.exception.UnrealizedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 文件操作工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class FileUtils {
    /**
     * 可读大小的单位
     */
    private static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "EB"};

    /**
     * 加载类路径下指定名称文件中的文本.
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     */
    public static Optional<String> loadFileText(String fileName) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            return Optional.of(StringUtils.readString(is));
        } catch (Exception ignored) {
        }
        // 文件不存在等其他情况返回null
        return Optional.empty();
    }

    /**
     * 读取指定名称文件中的文本.
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException 文件未找到会抛出此异常
     */
    public static String readFileText(String fileName) throws FileNotFoundException, IOException {
        try (FileReader reader = new FileReader(fileName)) {
            return StringUtils.readString(reader);
        }
    }

    /**
     * 写入指定文本到文件中.
     * <p>
     * 文件不存在，则会自动创建，默认是覆盖原文件
     *
     * @param fileName 文件名称
     * @param content  要写入的内容
     * @throws IOException If an I/O error occurs
     */
    public static void writeFileText(String fileName, String content) throws IOException {
        writeFileText(fileName, false, content);
    }

    /**
     * 写入指定文本到文件中.
     * <p>
     * 文件不存在，则会自动创建
     *
     * @param fileName 文件名称
     * @param append   是否追加写入
     * @param content  要写入的内容
     * @throws IOException If an I/O error occurs
     */
    public static void writeFileText(String fileName, boolean append, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName, append); OutputStreamWriter osw = new OutputStreamWriter(fos, CharsetUtils.CHARSET_UTF_8)) {
            osw.write(content);
            osw.flush();
        }
    }


    public static void writerFile(String fileName, byte[] data, boolean append) throws IOException {
        File file = new File(fileName);
        createNewFile(file);
        try (FileOutputStream fos = new FileOutputStream(file, append); OutputStream out = new BufferedOutputStream(fos)) {
            out.write(data);
            out.flush();
        }
    }

    /**
     * 可读的文件大小
     *
     * @param file 文件
     * @return 大小
     */
    public static String readableFileSize(File file) {
        return readableFileSize(file.length());
    }

    /**
     * 可读的文件大小<br>
     *
     * @param size Long类型大小
     * @return 大小
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + UNITS[digitGroups];
    }

    /**
     * 创建指定文件,目录不存在则自动创建
     * <p>
     * 如果目标文件不存在并且创建成功，则为true；如果目标文件存在，则为false
     *
     * @param file 文件对象
     * @return 如果目标文件不存在并且创建成功，则为true；如果目标文件存在，则为false
     * @throws IOException IO异常
     */
    public static boolean createNewFile(File file) throws IOException {
        // 目标文件存在，直接返回false.
        if (file.exists()) {
            return false;
        }

        // 如果目录不存在则创建此父目录
        final File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 目录有了，直接调用JDK的创建新文件命令
        return file.createNewFile();
    }

    /**
     * 复制指定文件或目录到目标文件或目录.
     *
     * @param srcPath    指定文件或目录
     * @param targetPath 目标文件或目录
     * @throws IOException 创建文件或目录时可能会抛出此异常
     */
    public static void copy(String srcPath, String targetPath) throws IOException {
        copy(new File(srcPath), new File(targetPath), Collections.emptySet(), Collections.emptySet());
    }

    /**
     * 复制指定文件或目录到目标文件或目录.
     *
     * @param srcPath    指定文件或目录
     * @param targetPath 目标文件或目录
     * @param checklist  只处理指定的文件清单，如果为空则为全部
     * @throws IOException 创建文件或目录时可能会抛出此异常
     */
    public static void copy(String srcPath, String targetPath, Set<String> checklist) throws IOException {
        copy(new File(srcPath), new File(targetPath), checklist, Collections.emptySet());
    }

    /**
     * 复制指定文件或目录到目标文件或目录.
     *
     * @param srcPath      指定文件或目录
     * @param targetPath   目标文件或目录
     * @param checklist    只处理指定的文件清单，如果为空则为全部
     * @param ignoreDirSet 忽略目录名称的列表，如果为空则不忽略
     * @throws IOException 创建文件或目录时可能会抛出此异常
     */
    public static void copy(String srcPath, String targetPath, Set<String> checklist, Set<String> ignoreDirSet) throws IOException {
        copy(new File(srcPath), new File(targetPath), checklist, ignoreDirSet);
    }

    /**
     * 复制指定文件或目录到目标文件或目录.
     *
     * @param srcFile      指定文件或目录
     * @param targetFile   目标文件或目录
     * @param checklist    只处理指定的文件清单，如果为空则为全部
     * @param ignoreDirSet 忽略目录名称的列表，如果为空则不忽略
     * @throws IOException 创建文件或目录时可能会抛出此异常
     */
    public static void copy(File srcFile, File targetFile, Set<String> checklist, Set<String> ignoreDirSet) throws IOException {
        // 0. src不存在，你复制什么呢???
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcFile.getPath());
        }

        // 1. src是个文件
        if (srcFile.isFile()) {
            // 1.1 target 不存在，自动创建
            if (!targetFile.exists()) {
                createNewFile(targetFile);
                Files.copy(srcFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            // 1.2 target 存在，覆盖
            else if (targetFile.isFile()) {
                Files.copy(srcFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            // 1.3 target 是个目录
            else if (targetFile.isDirectory()) {
                // 创建新文件
                File newFile = new File(targetFile.getPath(), srcFile.getName());
                newFile.createNewFile();
                Files.copy(srcFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            // 未实现的情况
            else {
                throw new UnrealizedException("未知实现 targetFile=" + targetFile.getPath());
            }
        }
        // 2. src 是个目录
        else if (srcFile.isDirectory()) {
            // 2.1 target 不存在，自动创建目标进行复制
            if (!targetFile.exists()) {
                targetFile.mkdirs();
                copyDir(srcFile, targetFile, checklist, ignoreDirSet);
            }
            // 2.2 target 也是一个目录，覆盖此目录
            else if (targetFile.isDirectory()) {
                copyDir(srcFile, targetFile, checklist, ignoreDirSet);
            }
            // 其他情况都是不正常情况
            else {
                throw new UnrealizedException("目录不能复制到文件中 targetFile=" + targetFile.getPath());
            }
        }
        // 未实现的情况
        else {
            throw new UnrealizedException("未实现的文件类型复制 srcFile=" + srcFile.getPath());
        }
    }

    private static void copyDir(File srcFile, File target, Set<String> fileNameSet, Set<String> ignoreSet) throws IOException {
        for (File file : Objects.requireNonNull(srcFile.listFiles(v -> v.isDirectory() || fileNameSet.isEmpty() || fileNameSet.contains(v.getName())))) {
            // 忽略复制那就丢掉
            if (ignoreSet.contains(file.getName())) {
                continue;
            }
            // 复制文件
            if (file.isFile()) {
                // 创建新文件
                File newFile = new File(target.getPath(), file.getName());
                newFile.createNewFile();
                Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            // 递归复制目录
            else if (file.isDirectory()) {
                // 创建子目录
                File subDir = new File(target.getPath(), file.getName());
                subDir.mkdirs();
                copyDir(file, subDir, fileNameSet, ignoreSet);
            }
        }
    }
}