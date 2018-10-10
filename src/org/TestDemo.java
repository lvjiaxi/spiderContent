package org;

public class TestDemo {
    public static void main(String[] args) {
        String s="三百零二第三百零二章 怒杀使者(九十一)";
        String s1 = CompareChineseToEnglish.ChineseToEnglish(s);
        System.out.println(s1);
        String s2 = Bracket.BracketChineseToEnglish(s1);
        String s3 = CompareDemo.FilterSymbol(s2);

        System.out.println(s3);
    }
}
