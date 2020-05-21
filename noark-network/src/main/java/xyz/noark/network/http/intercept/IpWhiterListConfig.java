package xyz.noark.network.http.intercept;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class IpWhiterListConfig {
    private static Pattern pattern = Pattern.compile("(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})");
    private final Set<String> ipList;

    IpWhiterListConfig(String allowIp) {
        this.ipList = this.getAvailIpList(allowIp);
    }

    /**
     * getAvaliIpList:(根据IP白名单设置获取可用的IP列表).
     */
    public Set<String> getAvailIpList(String allowIp) {
        Set<String> ipList = new HashSet<String>();
        for (String allow : allowIp.replaceAll("\\s", "").split(";")) {
            if (allow.indexOf("*") > -1) {
                String[] ips = allow.split("\\.");
                String[] from = new String[]{"0", "0", "0", "0"};
                String[] end = new String[]{"255", "255", "255", "255"};
                List<String> tem = new ArrayList<String>();
                for (int i = 0; i < ips.length; i++) {
                    if (ips[i].indexOf("*") > -1) {
                        tem = complete(ips[i]);
                        from[i] = null;
                        end[i] = null;
                    } else {
                        from[i] = ips[i];
                        end[i] = ips[i];
                    }
                }
                StringBuffer fromIP = new StringBuffer();
                StringBuffer endIP = new StringBuffer();
                for (int i = 0; i < 4; i++) {
                    if (from[i] != null) {
                        fromIP.append(from[i]).append(".");
                        endIP.append(end[i]).append(".");
                    } else {
                        fromIP.append("[*].");
                        endIP.append("[*].");
                    }
                }
                fromIP.deleteCharAt(fromIP.length() - 1);
                endIP.deleteCharAt(endIP.length() - 1);

                for (String s : tem) {
                    String ip = fromIP.toString().replace("[*]", s.split(";")[0]) + "-" + endIP.toString().replace("[*]", s.split(";")[1]);
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
        String from = "";
        String end = "";
        if (length == 1) {
            from = arg.replace("*", "0");
            end = arg.replace("*", "9");
        } else {
            from = arg.replace("*", "00");
            end = arg.replace("*", "99");
        }
        if (Integer.valueOf(from) > 255) {
            return null;
        }
        if (Integer.valueOf(end) > 255) {
            end = "255";
        }
        return from + ";" + end;
    }

    /**
     * 在添加至白名单时进行格式校验
     */
    private static boolean validate(String ip) {
        for (String s : ip.split("-")) {
            if (!pattern.matcher(s).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对单个IP节点进行范围限定
     *
     * @param arg
     * @return 返回限定后的IP范围，格式为List[10;19, 100;199]
     */
    private static List<String> complete(String arg) {
        List<String> com = new ArrayList<String>();
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
            if (allow.indexOf("-") > -1) {
                String[] from = allow.split("-")[0].split("\\.");
                String[] end = allow.split("-")[1].split("\\.");
                String[] tag = ip.split("\\.");

                // 对IP从左到右进行逐段匹配
                boolean check = true;
                for (int i = 0; i < 4; i++) {
                    int s = Integer.valueOf(from[i]);
                    int t = Integer.valueOf(tag[i]);
                    int e = Integer.valueOf(end[i]);
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
