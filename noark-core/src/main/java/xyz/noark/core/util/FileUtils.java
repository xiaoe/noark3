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

import java.io.*;
import java.text.DecimalFormat;
import java.util.Optional;

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
            return Optional.ofNullable(StringUtils.readString(is));
        } catch (Exception e) {
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
        try (FileOutputStream fos = new FileOutputStream(fileName, append);
             OutputStreamWriter osw = new OutputStreamWriter(fos, CharsetUtils.CHARSET_UTF_8)) {
            osw.write(content);
            osw.flush();
        }
    }


    public static void writerFile(String fileName, byte[] data, boolean append) throws IOException {
        File file = new File(fileName);
        createNewFile(file);
        try (FileOutputStream fos = new FileOutputStream(file, append);
             OutputStream out = new BufferedOutputStream(fos)) {
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

}