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

import xyz.noark.core.util.StringUtils;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * 一个用于计算的秒表.
 * <p>
 * 这个来源于Spring5.0，感觉这个表比Apache的那个表好使，我去年买了个表
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class StopWatch {
    /**
     * 开始时间(单位：纳秒)
     */
    private long startTimeNano;
    /**
     * 总的运行时间(单位：纳秒)
     */
    private long totalTimeNano;

    /**
     * 当前任务名称
     */
    private String currentTaskName;

    /**
     * 如果用得上任务报表，建议打开此状态(默认：关闭)
     */
    private final boolean taskListEnabled;
    private final List<TaskInfo> taskList;

    /**
     * 构建一个秒表，默认关闭任务收集功能
     */
    public StopWatch() {
        this(false);
    }

    /**
     * 构建一个秒表
     *
     * @param taskListEnabled 是否开启任务收集功能
     */
    public StopWatch(boolean taskListEnabled) {
        this.taskListEnabled = taskListEnabled;
        this.taskList = taskListEnabled ? new LinkedList<>() : null;
    }

    /**
     * 开始计时
     */
    public void start() {
        start(StringUtils.EMPTY);
    }

    /**
     * 开始计时
     *
     * @param taskName 这个任务名称
     */
    public void start(String taskName) {
        this.currentTaskName = taskName;
        this.startTimeNano = System.nanoTime();
    }

    /**
     * 停止计时.
     *
     * @see #start()
     */
    public void stop() {
        long execTime = System.nanoTime() - this.startTimeNano;
        this.totalTimeNano += execTime;
        // 启用收集
        if (this.taskListEnabled) {
            this.taskList.add(new TaskInfo(this.currentTaskName, execTime));
        }
        // 清理掉当前任务名称
        this.currentTaskName = null;
    }


    /**
     * 获取所有任务运行总时长(单位：纳秒)
     *
     * @return 所有任务运行总时长
     */
    public long getTotalTimeNano() {
        return this.totalTimeNano;
    }

    /**
     * 获取所有任务运行总时长(单位：豪秒)
     *
     * @return 所有任务运行总时长
     */
    public float getTotalTimeMillis() {
        return this.totalTimeNano / 100_0000F;
    }


    /**
     * @return 概况
     */
    public String shortSummary() {
        return "StopWatch: running time (millis) = " + getTotalTimeMillis();
    }

    /**
     * Return a string with a table describing all tasks performed.
     * For custom reporting, call getTaskInfo() and use the task info directly.
     *
     * @return 秒表报表
     */
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder(shortSummary());
        sb.append('\n');
        if (!this.taskListEnabled) {
            sb.append("No task info kept");
        } else {
            sb.append("-----------------------------------------\n");
            sb.append("ms     %     Task name\n");
            sb.append("-----------------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumIntegerDigits(5);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumIntegerDigits(3);
            pf.setGroupingUsed(false);
            for (TaskInfo task : taskList) {
                sb.append(nf.format(task.getTimeMillis())).append("  ");
                sb.append(pf.format(1D * task.getTimeNano() / getTotalTimeNano())).append("  ");
                sb.append(task.getTaskName()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Return an informative string describing all tasks performed
     * For custom reporting, call {@code getTaskInfo()} and use the task info directly.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(shortSummary());
        if (this.taskListEnabled) {
            for (TaskInfo task : taskList) {
                sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeMillis());
                long percent = Math.round((100.0 * task.getTimeNano()) / getTotalTimeNano());
                sb.append(" = ").append(percent).append("%");
            }
        } else {
            sb.append("; no task info kept");
        }
        return sb.toString();
    }

    public static final class TaskInfo {
        private final String taskName;
        private final long timeNano;

        TaskInfo(String taskName, long timeNano) {
            this.taskName = taskName;
            this.timeNano = timeNano;
        }

        public String getTaskName() {
            return this.taskName;
        }

        public long getTimeNano() {
            return timeNano;
        }

        public long getTimeMillis() {
            return timeNano / 100_0000;
        }

    }
}
