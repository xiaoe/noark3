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
package xyz.noark.core.lang;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 画图辅助类.
 * <p>
 * 在不了解Graphics2D的情况下，也能画个差不多的示意图，常用于，输出场景中野怪分布啊，矿藏分布的情况
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class Img {
    private final int width;
    private final int height;
    private BufferedImage image;
    private Graphics2D graphics;

    public Img(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        this.graphics = image.createGraphics();
    }

    /**
     * 在(x,y)位置使用指定颜色把文本画到图片上
     *
     * @param text  文本
     * @param x     坐标X
     * @param y     坐标Y
     * @param color 指定颜色
     */
    public void drawString(String text, int x, int y, Color color) {
        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }

    /**
     * 在(x,y)位置使用指定颜色把文本画到图片上
     *
     * @param text  文本
     * @param point 坐标
     * @param color 指定颜色
     */
    public void drawString(String text, Point point, Color color) {
        this.drawString(text, point.getX(), point.getY(), color);
    }

    /**
     * 在(x,y)位置使用指定颜色把数字画到图片上
     *
     * @param num   数字
     * @param x     坐标X
     * @param y     坐标Y
     * @param color 指定颜色
     */
    public void drawInt(int num, int x, int y, Color color) {
        this.drawString(String.valueOf(num), x, y, color);
    }

    /**
     * 在(x,y)位置使用指定颜色把数字画到图片上
     *
     * @param num   数字
     * @param point 坐标
     * @param color 指定颜色
     */
    public void drawInt(int num, Point point, Color color) {
        this.drawInt(num, point.getX(), point.getY(), color);
    }

    /**
     * 保存到文件
     *
     * @param path 文件路径
     * @throws IOException 保存文件时可能会抛出IO异常
     */
    public void save(String path) throws IOException {
        graphics.dispose();
        ImageIO.write(image, "png", new File(path));
    }
}
