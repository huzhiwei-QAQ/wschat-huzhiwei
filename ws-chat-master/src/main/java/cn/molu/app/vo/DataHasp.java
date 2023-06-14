package cn.molu.app.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 陌路
 * @Description
 * @date 2022-05-04 下午12:43:34
 */
public class DataHasp implements Serializable {

    private static final long serialVersionUID = -5475736323894517597L;
    private String str;

    public DataHasp() {
        super();
    }

    public DataHasp(String str) {
        super();
        this.str = getDataStr(str, 2);
    }

    public DataHasp(String str, int len) {
        super();
        this.str = getDataStr(str, len);
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = getDataStr(str, 2);
    }

    public void setStr(String str, int len) {
        this.str = getDataStr(str, len);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * @param num
     * @param len
     * @return String
     * @Title 根据输入字符串得到新的字符串
     */
    private static String getChar(Integer num, int len) {
        if (num == null) {
            return "";
        }
        len = len < 1 ? 1 : len > 3 ? 3 : len;
        char[] c1 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
                'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] c2 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
                'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        char[] c3 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        int index1 = num > 35 ? num % 35 : num;
        boolean even1 = index1 % 2 == 0 ? true : false;
        StringBuffer sb = new StringBuffer();
        if (even1) {
            char ch1 = c1[index1];
            sb.append(String.valueOf(ch1).toLowerCase());
        } else {
            char ch1 = c1[index1];
            sb.append(ch1);
        }
        if (len == 1) {
            return sb.toString().trim();
        }
        int index2 = num > 25 ? num % 25 : num;
        boolean even2 = index2 % 2 == 0 ? true : false;
        if (even2) {
            char ch2 = c2[index2];
            sb.append(String.valueOf(ch2).toLowerCase());
        } else {
            char ch2 = c2[index2];
            sb.append(ch2);
        }
        if (len == 2) {
            return sb.toString().trim();
        }
        int index3 = num > 9 ? num % 9 : num;
        boolean even3 = index3 % 2 == 0 ? true : false;
        if (even3) {
            char ch3 = c3[index3];
            sb.append(String.valueOf(ch3).toLowerCase());
        } else {
            char ch3 = c3[index3];
            sb.append(ch3);
        }
        return sb.toString().trim();
    }

    private int getHashCode(Character c) {
        int hashCode = c.hashCode();
        return hashCode;
    }

    public String getDataStr(String str, int len) {
        if (StringUtils.isBlank(str.trim())) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        char[] charArr = str.toCharArray();
        for (char c : charArr) {
            int code = getHashCode(c);
            String ch = getChar(code, len);
            sb.append(ch);
        }
        return sb.toString().trim();
    }

    public String getDataStr(String str) {
        return getDataStr(str, 2).trim();
    }
}
