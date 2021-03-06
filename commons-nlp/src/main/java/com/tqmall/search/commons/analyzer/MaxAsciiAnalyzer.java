package com.tqmall.search.commons.analyzer;

import com.tqmall.search.commons.match.AbstractTextMatch;
import com.tqmall.search.commons.match.Hit;
import com.tqmall.search.commons.nlp.NlpUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xing on 16/3/16.
 * 字母,数字最大分词, 相连的数字,字母都整到一个词里面
 *
 * @author xing
 */
public class MaxAsciiAnalyzer extends AbstractTextMatch<TokenType> {

    public static final MaxAsciiAnalyzer INSTANCE = new MaxAsciiAnalyzer();

    MaxAsciiAnalyzer() {
    }

    private boolean usefulChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    @Override
    public List<Hit<TokenType>> match(char[] text, int off, int len) {
        final int endPos = off + len;
        NlpUtils.arrayIndexCheck(text, off, endPos);
        if (len == 0) return null;
        List<Hit<TokenType>> hits = new LinkedList<>();
        int matchStart = -1;
        for (int i = off; i < endPos; i++) {
            char c = text[i];
            if (usefulChar(c) || (c == '.' && i > off && (i + 1) < endPos
                    && isNumber(text[i - 1]) && isNumber(text[i + 1]))) {
                if (matchStart == -1) matchStart = i;
            } else if (matchStart != -1) {
                hits.add(new Hit<>(matchStart, i, TokenType.EN_MIX));
                matchStart = -1;
            }
        }
        if (matchStart != -1) {
            hits.add(new Hit<>(matchStart, endPos, TokenType.EN_MIX));
        }
        return hits;
    }

}
