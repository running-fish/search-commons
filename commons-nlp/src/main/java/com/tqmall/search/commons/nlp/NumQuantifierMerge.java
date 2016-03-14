package com.tqmall.search.commons.nlp;

import com.tqmall.search.commons.match.Hit;
import com.tqmall.search.commons.utils.CommonsUtils;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by xing on 16/3/14.
 * 数量词合并
 *
 * @author xing
 */
public class NumQuantifierMerge {

    /**
     * 合成的数量词是否作为扩充的词添加, 也就是原先的数词和两次在匹配结果中是否保留
     */
    private final boolean appendNumQuantifier;

    public NumQuantifierMerge(boolean appendNumQuantifier) {
        this.appendNumQuantifier = appendNumQuantifier;
    }

    /**
     * 数量词合并
     *
     * @param hits 有序的匹配结果
     */
    public List<Hit<TokenType>> merge(List<Hit<TokenType>> hits) {
        if (CommonsUtils.isEmpty(hits)) return hits;
        ListIterator<Hit<TokenType>> it = hits.listIterator();
        Hit<TokenType> preNumHit = null;
        while (it.hasNext()) {
            Hit<TokenType> curHit = it.next();
            TokenType curType = curHit.getValue();
            if (curType == TokenType.NUM || curType == TokenType.DECIMAL) {
                preNumHit = curHit;
            } else if (preNumHit != null && curType == TokenType.QUANTIFIER
                    && preNumHit.getEndPos() == curHit.getStartPos()) {
                Hit<TokenType> numQuantifierHit = new Hit<>(preNumHit.getStartPos(), preNumHit.getKey() + curHit.getKey(),
                        TokenType.NUM_QUANTIFIER);
                if (!appendNumQuantifier) {
                    it.remove();
                    it.remove();
                }
                it.add(numQuantifierHit);
            } else {
                preNumHit = null;
            }
        }
        return hits;
    }
}
