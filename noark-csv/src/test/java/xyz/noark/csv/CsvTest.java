/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.csv;

import org.junit.BeforeClass;
import org.junit.Test;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.csv.converter.RewardConverter;
import xyz.noark.csv.template.BaseCriticalTemplate;
import xyz.noark.csv.template.ItemTemplate;
import xyz.noark.csv.template.MonsterRefreshTemplate;
import xyz.noark.csv.template.MonsterRefreshTemplate2;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * CSV测试.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class CsvTest {
    private static String templatePath;
    private static Csv csv;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConvertManager.getInstance().register(RewardConverter.class);
        templatePath = "classpath:";
        csv = new Csv('	');
    }

    @Test
    public void testCsv() {
        assertTrue(csv != null);
    }

    @Test
    public void testCsvChar() {
        assertTrue(csv != null);
    }

    @Test
    public void testLoadAllStringClass() {
        List<ItemTemplate> ts = csv.loadAll(templatePath, ItemTemplate.class);
        assertTrue(ts.size() == 22);
    }

    @Test
    public void testLoadAllStringStringClass() {
        List<MonsterRefreshTemplate> templates = csv.loadAll(templatePath, MonsterRefreshTemplate.class);
        List<MonsterRefreshTemplate2> templates2 = csv.loadAll(templatePath, MonsterRefreshTemplate2.class);
        for (int i = 0; i < templates.size(); i++) {
            assertTrue(templates.get(i).getLevelNumList().equals(templates2.get(i).getLevelNumList()));
        }
    }

    @Test
    public void test() {
        List<BaseCriticalTemplate> templates = csv.loadAll(templatePath, BaseCriticalTemplate.class);
        assertTrue(templates.size() == 151);
    }
}