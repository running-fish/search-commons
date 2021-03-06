package com.tqmall.search.commons.nlp;

import com.tqmall.search.commons.match.Hit;
import com.tqmall.search.commons.analyzer.AsciiAnalyzer;
import com.tqmall.search.commons.analyzer.MaxAsciiAnalyzer;
import com.tqmall.search.commons.analyzer.TokenType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xing on 16/3/10.
 * Ascii分词测试
 *
 * @author xing
 */
public class AsciiAnalyzerTest {

    private static final List<TestTextEntry> textEntryList = new ArrayList<>();

    @BeforeClass
    public static void init() {
        TestTextEntry entry = new TestTextEntry("xing09 78tqmall5.6ssd");
        Collections.addAll(entry.expect1, Utils.hitValueOf(0, "xing", TokenType.EN), Utils.hitValueOf(4, "09", TokenType.NUM),
                Utils.hitValueOf(7, "78", TokenType.NUM), Utils.hitValueOf(9, "tqmall", TokenType.EN), Utils.hitValueOf(15, "5.6", TokenType.DECIMAL),
                Utils.hitValueOf(18, "ssd", TokenType.EN));
        entry.expect2.addAll(entry.expect1);
        entry.expect3.addAll(entry.expect1);
        Collections.addAll(entry.expectMax, Utils.hitValueOf(0, "xing09", TokenType.EN_MIX), Utils.hitValueOf(7, "78tqmall5.6ssd", TokenType.EN_MIX));
        textEntryList.add(entry);

        entry = new TestTextEntry("xing09.  tqmall5..6.9ssd ");
        Collections.addAll(entry.expect1, Utils.hitValueOf(0, "xing", TokenType.EN), Utils.hitValueOf(4, "09", TokenType.NUM),
                Utils.hitValueOf(9, "tqmall", TokenType.EN), Utils.hitValueOf(15, "5", TokenType.NUM), Utils.hitValueOf(18, "6.9", TokenType.DECIMAL),
                Utils.hitValueOf(21, "ssd", TokenType.EN));
        entry.expect2.addAll(entry.expect1);
        entry.expect3.addAll(entry.expect1);
        Collections.addAll(entry.expectMax, Utils.hitValueOf(0, "xing09", TokenType.EN_MIX), Utils.hitValueOf(9, "tqmall5", TokenType.EN_MIX),
                Utils.hitValueOf(18, "6.9ssd", TokenType.EN_MIX));
        textEntryList.add(entry);

        entry = new TestTextEntry(".89.6xing tqmall5..6.9ssd78.0.9.78");
        Collections.addAll(entry.expect1, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(5, "xing", TokenType.EN),
                Utils.hitValueOf(10, "tqmall", TokenType.EN), Utils.hitValueOf(16, "5", TokenType.NUM), Utils.hitValueOf(19, "6.9", TokenType.DECIMAL),
                Utils.hitValueOf(22, "ssd", TokenType.EN), Utils.hitValueOf(25, "78.0", TokenType.EN), Utils.hitValueOf(30, "9.78", TokenType.DECIMAL));
        entry.expect2.addAll(entry.expect1);
        entry.expect3.addAll(entry.expect1);
        Collections.addAll(entry.expectMax, Utils.hitValueOf(1, "89.6xing", TokenType.EN_MIX), Utils.hitValueOf(10, "tqmall5", TokenType.EN_MIX),
                Utils.hitValueOf(19, "6.9ssd78.0.9.78", TokenType.EN_MIX));
        textEntryList.add(entry);

        entry = new TestTextEntry(".89.6..-xing-wang");
        Collections.addAll(entry.expect1, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(8, "xing", TokenType.EN), Utils.hitValueOf(13, "wang", TokenType.EN));
        Collections.addAll(entry.expect2, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(8, "xing-wang", TokenType.EN_MIX));
        Collections.addAll(entry.expect3, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(8, "xing", TokenType.EN),
                Utils.hitValueOf(8, "xing-wang", TokenType.EN), Utils.hitValueOf(13, "wang", TokenType.EN));
        Collections.addAll(entry.expectMax, Utils.hitValueOf(1, "89.6", TokenType.EN_MIX), Utils.hitValueOf(8, "xing", TokenType.EN_MIX),
                Utils.hitValueOf(13, "wang", TokenType.EN_MIX));
        textEntryList.add(entry);

        entry = new TestTextEntry(".89.6..-xing--wang");
        Collections.addAll(entry.expect1, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(8, "xing", TokenType.EN), Utils.hitValueOf(14, "wang", TokenType.EN));
        entry.expect2.addAll(entry.expect1);
        entry.expect3.addAll(entry.expect1);
        Collections.addAll(entry.expectMax, Utils.hitValueOf(1, "89.6", TokenType.EN_MIX), Utils.hitValueOf(8, "xing", TokenType.EN_MIX), Utils.hitValueOf(14, "wang", TokenType.EN_MIX));
        textEntryList.add(entry);

        entry = new TestTextEntry(".89.6..-xing-05-yan");
        Collections.addAll(entry.expect1, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(8, "xing", TokenType.EN),
                Utils.hitValueOf(13, "05", TokenType.NUM), Utils.hitValueOf(16, "yan", TokenType.EN));
        Collections.addAll(entry.expect2, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(8, "xing-05", TokenType.EN_MIX), Utils.hitValueOf(16, "yan", TokenType.EN));
        Collections.addAll(entry.expect3, Utils.hitValueOf(1, "89.6", TokenType.DECIMAL), Utils.hitValueOf(8, "xing", TokenType.EN),
                Utils.hitValueOf(8, "xing-05", TokenType.EN_MIX), Utils.hitValueOf(13, "05", TokenType.EN), Utils.hitValueOf(16, "yan", TokenType.EN));
        Collections.addAll(entry.expectMax, Utils.hitValueOf(1, "89.6", TokenType.EN_MIX), Utils.hitValueOf(8, "xing", TokenType.EN_MIX),
                Utils.hitValueOf(13, "05", TokenType.EN_MIX), Utils.hitValueOf(16, "yan", TokenType.EN_MIX));
        textEntryList.add(entry);
    }

    @AfterClass
    public static void clear() {
        textEntryList.clear();
    }

    @Test
    public void min_1_AnalyzerTest() {
        AsciiAnalyzer Analyzer = AsciiAnalyzer.build().create();
        for (TestTextEntry e : textEntryList) {
            Assert.assertEquals(e.text, e.expect1, Analyzer.match(e.text));
        }
    }

    @Test
    public void min_2_AnalyzerTest() {
        AsciiAnalyzer Analyzer = AsciiAnalyzer.build()
                .enMixAppend(false)
                .create();
        for (TestTextEntry e : textEntryList) {
            Assert.assertEquals(e.text, e.expect2, Analyzer.match(e.text));
        }
    }

    @Test
    public void min_3_AnalyzerTest() {
        AsciiAnalyzer Analyzer = AsciiAnalyzer.build()
                .enMixAppend(true)
                .create();
        for (TestTextEntry e : textEntryList) {
            Assert.assertEquals(e.text, e.expect3, Analyzer.match(e.text));
        }
    }

    @Test
    public void maxAnalyzerTest() {
        for (TestTextEntry e : textEntryList) {
            Assert.assertEquals(e.text, e.expectMax, MaxAsciiAnalyzer.INSTANCE.match(e.text));
        }
    }

    static class TestTextEntry {

        private final String text;

        private final List<Hit<TokenType>> expect1 = new ArrayList<>();

        private final List<Hit<TokenType>> expect2 = new ArrayList<>();

        private final List<Hit<TokenType>> expect3 = new ArrayList<>();

        private final List<Hit<TokenType>> expectMax = new ArrayList<>();

        TestTextEntry(String text) {
            this.text = text;
        }

    }

}
