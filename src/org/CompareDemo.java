package org;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareDemo {

    public static String FilterSymbol(String chapterName){//去除符号
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(chapterName);
        String replace = m.replaceAll("").trim().replace(" ", "");

        return replace;
    }
    @Test
    public void Test01(){
        System.out.println(FilterSymbol("第六百五十一章 钢铁直男（二合一）"));
    }

}
