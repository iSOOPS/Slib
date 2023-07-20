package com.isoops.slib.redis;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.utils.SFieldUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.providers.PooledConnectionProvider;
import redis.clients.jedis.search.*;
import redis.clients.jedis.search.schemafields.SchemaField;

import java.util.*;

@Component
public class SRedisMod {

    /******************************* json custom mod *******************************/
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CustomPO extends AbstractObject {
        private Long rsdId;
        public CustomPO() {
            setRsdId(IdUtil.getSnowflakeNextId());
        }
        public String toJson() {
            return JSON.toJSONString(this);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class KvPO<T extends CustomPO> extends AbstractObject {
        private String key;
        private T object;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class IndexType extends AbstractObject {
        private String column;
        private Schema.FieldType type;
        public IndexType(String column,Schema.FieldType type) {
            this.column = column;
            this.type = type;
        }
    }

    /******************************* search query combination tool *******************************/
    @Data
    public static class QueryPlus<T extends CustomPO> {

        private String queryString = "*";

        public String build() {
            return this.queryString;
        }

        private java.lang.reflect.Field getFnField(SFunction<T,?> columnName) {
            java.lang.reflect.Field field = SFieldUtil.getFunctionFiled(columnName);
            if (field == null) {
                throw new RuntimeException("获取function-field失败");
            }
            return field;
        }

        private void setQuery(String qString) {
            queryString = "*".equals(queryString) ? qString : queryString + " " + qString;
        }

        enum QueryType {
            EQ,
            NOT_EQ,
            GT,
            GE,
            LT,
            LE,
            LIKE,
            LIKE_LEFT,
            LIKE_RIGHT,
            IN,
            NOT_IN
        }

        private final String[] TOKENIZATIONS =
                {"\\",",",".","<",">","{","}","[","]","\"","'",":",";","!","@","#","$","%","^","&","*","(",")","-","+","=","~"};

        /**
         * 查询条件的 string 需要转义
         * @param str 查询 string
         * @return f
         */
        private String spacingDone(String str) {
            String stringForQuery = str;
            for (String tokenized : TOKENIZATIONS) {
                stringForQuery = stringForQuery.replace(tokenized,"\\" + tokenized);
            }
            return stringForQuery;
        }

        private String queryFormat(java.lang.reflect.Field field, String value, QueryType queryType) {
            String fnName = StrUtil.toCamelCase(field.getName());
            boolean isNumeric = !field.getType().equals(String.class);
            return formats(fnName, Collections.singletonList(value),queryType,isNumeric);
        }

        private String queryFormats(java.lang.reflect.Field field, Collection<String> values, QueryType queryType) {
            String fnName = StrUtil.toCamelCase(field.getName());
            boolean isNumeric = !field.getType().equals(String.class);
            return formats(fnName, values,queryType,isNumeric);
        }

        private String rollMixInString(Collection<String> vs) {
            String vss = null;
            for (String str : vs) {
                vss = (vss == null ? (str + "|") : (vss + str + "|"));
            }
            assert vss != null;
            return vss.substring(0,vss.length()-1);
        }

        private String formats(String fnName,
                               Collection<String> values,
                               QueryType queryType,
                               Boolean isNumeric) {
            List<String> vs = new ArrayList<>();
            for (String value : values) {
                vs.add(spacingDone(value));
            }
            switch(queryType) {
                case EQ : {
                    if (isNumeric) {
                        return "@" + fnName + ":\"[" + vs.get(0) + " " + vs.get(0) + "]\"";
                    }
                    return "@" + fnName + ":" + vs.get(0);
                }
                case NOT_EQ : {
                    return "-@" + fnName + ":" + vs.get(0);
                }
                case GT : {
                    return "@" + fnName + ":" + "\"[(" + vs.get(0) + " +inf]\"";
                }
                case GE : {
                    return "@" + fnName + ":" + "\"[" + vs.get(0) + " +inf]\"";
                }
                case LT : {
                    return "@" + fnName + ":" + "\"[-inf( " + vs.get(0) + "]\"";
                }
                case LE : {
                    return "@" + fnName + ":" + "\"[-inf " + vs.get(0) + "]\"";
                }
                case LIKE : {
                    return "@" + fnName + ":*" + vs.get(0) + "*";
                }
                case LIKE_LEFT : {
                    return "@" + fnName + ":*" + vs.get(0);
                }
                case LIKE_RIGHT : {
                    return "@" + fnName + ":" + vs.get(0) + "*";
                }
                case IN : {
                    return "@" + fnName + ":(" + rollMixInString(vs) + ")";
                }
                case NOT_IN : {
                    return "-@" + fnName + ":(" + rollMixInString(vs) + ")";
                }
                default : {
                    return null;
                }
            }
        }

        public QueryPlus<T> eq(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.EQ));
            return this;
        }

        public QueryPlus<T> notEq(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.NOT_EQ));
            return this;
        }

        public QueryPlus<T> gt(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.GT));
            return this;
        }

        public QueryPlus<T> ge(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.GE));
            return this;
        }

        public QueryPlus<T> lt(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.LT));
            return this;
        }

        public QueryPlus<T> le(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.LE));
            return this;
        }

        public QueryPlus<T> like(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.LIKE));
            return this;
        }

        public QueryPlus<T> likeLeft(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.LIKE_LEFT));
            return this;
        }

        public QueryPlus<T> likeRight(SFunction<T,?> columnName, String value) {
            setQuery(queryFormat(getFnField(columnName),value, QueryType.LIKE_RIGHT));
            return this;
        }

        public QueryPlus<T> in(SFunction<T,?> columnName, Collection<String> values) {
            setQuery(queryFormats(getFnField(columnName),values, QueryType.IN));
            return this;
        }

        public QueryPlus<T> in(SFunction<T,?> columnName, String ...value) {
            setQuery(queryFormats(getFnField(columnName), Arrays.asList(value), QueryType.IN));
            return this;
        }

        public QueryPlus<T> notIn(SFunction<T,?> columnName, Collection<String> values) {
            setQuery(queryFormats(getFnField(columnName),values, QueryType.NOT_IN));
            return this;
        }

        public QueryPlus<T> notIn(SFunction<T,?> columnName, String ...value) {
            setQuery(queryFormats(getFnField(columnName), Arrays.asList(value), QueryType.NOT_IN));
            return this;
        }
    }

    /******************************* sRedis static function *******************************/

    /**
     * 初始化 jedis 客户端
     * @param host 地址
     * @param port 端口
     * @return f
     */
    public static UnifiedJedis initClient(String host,Integer port){
        // 获取连接
        HostAndPort config = new HostAndPort(host, port);
        return new UnifiedJedis(new PooledConnectionProvider(config));
    }

    /**
     * 初始化索引
     * @param client jedis 客户端
     * @param index 索引
     * @param keyName 绑定的 redis key
     * @param fields 索引的字段
     * @return f
     */
    public static UnifiedJedis initIndex(UnifiedJedis client,
                                         String index,
                                         String keyName,
                                         SchemaField ...fields){
        try {
            client.ftInfo(index);
            return client;
        } catch (JedisDataException exception) {
            if ("Unknown Index name".equals(exception.getMessage())) {
                client.ftCreate(index,
                        FTCreateParams.createParams().on(IndexDataType.JSON).addPrefix(keyName + ":"),
                        fields
                );
                return client;
            }
            return null;
        }
    }

    public static String merge(String v1, String v2) {
        return v1 + ":" + v2;
    }

    public static String merge(String v1, Integer v2) {
        return v1 + ":" + v2;
    }

    public static String merge(String v1, Long v2) {
        return v1 + ":" + v2;
    }

    /**
     * 写入 json 对象
     * @param client jedis 客户端
     * @param virtualKey 虚拟的 key(realkey="virtualKey:rsdId")
     * @param object 写入对象
     * @param <T> CustomPO类型子对象
     * @return f
     */
    public <T extends CustomPO> Long jsonvSet(UnifiedJedis client,
                                              String virtualKey,
                                              T object) {
        return jsonSet(client,merge(virtualKey,object.getRsdId()),object);
    }

    /**
     * 写入 json 对象
     * @param client jedis 客户端
     * @param realKey 真实的 key(realkey="virtualKey:rsdId")
     * @param object 写入对象
     * @param <T> CustomPO类型子对象
     * @return f
     */
    public <T extends CustomPO> Long jsonSet(UnifiedJedis client,
                                             String realKey,
                                             T object) {
        client.jsonSetWithEscape(realKey,Path2.ROOT_PATH,object);
        return object.getRsdId();
    }

    /**
     * 获取 json 对象
     * @param client jedis 客户端
     * @param clazz 解析成的对象
     * @param realkey 真实的 key(realkey="virtualKey:rsdId")
     * @param <T> CustomPO类型子对象
     * @return f
     */
    public <T extends CustomPO> T jsonGet(UnifiedJedis client,
                                          Class<T> clazz,
                                          String realkey) {
        return client.jsonGet(realkey, clazz);
    }

    /**
     * 获取 json 对象-批量
     * @param client jedis 客户端
     * @param clazz 解析成的对象
     * @param realkey 真实的 key(realkey="virtualKey:rsdId")
     * @param <T> CustomPO类型子对象
     * @return f
     */
    public <T extends CustomPO> List<T> jsonMget(UnifiedJedis client,
                                                 Class<T> clazz,
                                                 String ...realkey) {
        return client.jsonMGet(clazz, realkey);
    }

    /**
     * 删除 json 对象
     * @param client jedis 客户端
     * @param realkey 真实的 key(realkey="virtualKey:rsdId")
     * @return f
     */
    public Long jsonDel(UnifiedJedis client,
                        String realkey) {
        return client.jsonDel(realkey,Path2.ROOT_PATH);
    }

    /**
     * 搜索
     * @param client jedis 客户端
     * @param clazz 返回对象类型
     * @param index 索引
     * @param query 搜索条件
     * @param <T> CustomPO类型子对象
     * @return f
     */
    public <T extends CustomPO> List<T> ftSearch(UnifiedJedis client,
                                                 Class<T> clazz,
                                                 String index,
                                                 Query query) {
        SearchResult results = client.ftSearch(index,query);
        List<T> beans = new ArrayList<>();
        for (Document record : results.getDocuments()) {
            Object object = record.get(Path2.ROOT_PATH.toString());
            String jsonString = (String) object;
            T bean = JSON.parseObject(jsonString,clazz);
            beans.add(bean);
        }
        return beans;
    }

}
