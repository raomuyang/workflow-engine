package org.radrso.workflow.wfservice.utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by raomengnan on 16-8-30.
 */
public class MongoUtil {

    public static final Sort.Direction ASC = Sort.Direction.ASC;
    public static final Sort.Direction DESC = Sort.Direction.DESC;
    /**
     * 模糊查询
     * @param key
     * @param value
     * @return
     */
    public static Criteria fuzzyCriteria(String key, String value){
        return Criteria.where(key).regex(".*?" + value + ".*");
    }


    /**
     * 简单根据key排序
     * @param
     * @param direction Direction.ASC||Direction.DESC
     * @param key
     * @return
     */
    public static Query soryBy(Query query, Sort.Direction direction, String key){
        return query.with(new Sort(direction, key));
    }

    /**
     * 获取 x in [a,b] 的记录。两边均为闭区间
     * @param key
     * @param left
     * @param right
     * @return
     */
    public static Criteria betweenCriterial(String key, Object left, Object right){
        Criteria c1 = Criteria.where(key).gte(left);
        Criteria c2 = Criteria.where(key).lte(right);
        return new Criteria().andOperator(c1, c2);
    }

}
