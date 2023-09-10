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
package xyz.noark.network.http.intercept;

import xyz.noark.core.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * IP允许访问的列表配置.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class IpAllowListConfig {
    private static final Pattern PATTERN = Pattern.compile("(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})");
    private final Set<String> ipList;

    IpAllowListConfig(String allowIp) {
        this.ipList = this.getAvailIpList(allowIp);
    }

    /**
     * getAvaliIpList:(根据IP白名单设置获取可用的IP列表).
     */
    public Set<String> getAvailIpList(String allowIp) {
        Set<String> ipList = new HashSet<>();
        for (String allow : allowIp.replaceAll("\\s", "").split(StringUtils.SEMICOLON)) {
            if (allow.contains("*")) {
                String[] ips = allow.split("\\.");
                String[] from = new String[]{"0", "0", "0", "0"};
                String[] end = new String[]{"255", "255", "255", "255"};
                List<String> tem = new ArrayList<>();
                for (int i = 0; i < ips.length; i++) {
                    if (ips[i].contains("*")) {
                        tem = complete(ips[i]);
                        from[i] = null;
                        end[i] = null;
                    } else {
                        from[i] = ips[i];
                        end[i] = ips[i];
                    }
                }
                StringBuilder fromIp = new StringBuilder();
                StringBuilder endIp = new StringBuilder();
                for (int i = 0; i < 4; i++) {
                    if (from[i] != null) {
                        fromIp.append(from[i]).append(".");
                        endIp.append(end[i]).append(".");
                    } else {
                        fromIp.append("[*].");
                        endIp.append("[*].");
                    }
                }
                fromIp.deleteCharAt(fromIp.length() - 1);
                endIp.deleteCharAt(endIp.length() - 1);

                for (String s : tem) {
                    String ip = fromIp.toString().replace("[*]", s.split(";")[0]) + "-" + endIp.toString().replace("[*]", s.split(";")[1]);
                    if (validate(ip)) {
                        ipList.add(ip);
                    }
                }
            } else {
                if (validate(allow)) {
                    ipList.add(allow);
                }
            }
        }
        return ipList;
    }

    private static String complete(String arg, int length) {
        String from;
        String end;
        if (length == 1) {
            from = arg.replace("*", "0");
            end = arg.replace("*", "9");
        } else {
            from = arg.replace("*", "00");
            end = arg.replace("*", "99");
        }
        if (Integer.parseInt(from) > 255) {
            return null;
        }
        if (Integer.parseInt(end) > 255) {
            end = "255";
        }
        return from + ";" + end;
    }

    /**
     * 在添加至白名单时进行格式校验
     */
    private static boolean validate(String ip) {
        for (String s : ip.split(StringUtils.HYPHEN)) {
            if (!PATTERN.matcher(s).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对单个IP节点进行范围限定
     *
     * @param arg 参数
     * @return 返回限定后的IP范围，格式为List[10;19, 100;199]
     */
    private static List<String> complete(String arg) {
        List<String> com = new ArrayList<>();
        if (arg.length() == 1) {
            com.add("0;255");
        } else if (arg.length() == 2) {
            String s1 = complete(arg, 1);
            if (s1 != null) {
                com.add(s1);
            }
            String s2 = complete(arg, 2);
            if (s2 != null) {
                com.add(s2);
            }
        } else {
            String s1 = complete(arg, 1);
            if (s1 != null) {
                com.add(s1);
            }
        }
        return com;
    }

    public boolean access(String ip) {
        // 列表为空或包含指定IP，则可以访问
        if (ipList.isEmpty() || ipList.contains(ip)) {
            return true;
        }

        // 多组配置
        for (String allow : ipList) {
            if (allow.contains("-")) {
                String[] from = allow.split("-")[0].split("\\.");
                String[] end = allow.split("-")[1].split("\\.");
                String[] tag = ip.split("\\.");

                // 对IP从左到右进行逐段匹配
                boolean check = true;
                for (int i = 0; i < 4; i++) {
                    int s = Integer.parseInt(from[i]);
                    int t = Integer.parseInt(tag[i]);
                    int e = Integer.parseInt(end[i]);
                    if (!(s <= t && t <= e)) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean notAccess(String ip) {
        return !access(ip);
    }
}
