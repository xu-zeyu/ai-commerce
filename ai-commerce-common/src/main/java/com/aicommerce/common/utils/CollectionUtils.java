package com.aicommerce.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 基于 commons-lang3 封装的集合工具类
 * 提供更丰富的集合操作方法
 */
public class CollectionUtils {

    private CollectionUtils() {
        // 工具类，防止实例化
    }

    // ==================== 空判断相关 ====================

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断Map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断Map是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否不为空
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    // ==================== Map转换操作 ====================

    /**
     * 将集合转换为Map (key由函数生成，value为元素本身)
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        return convertMap(from, keyFunc, Function.identity());
    }

    /**
     * 将集合转换为Map (key和value都由函数生成)
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, 
                                                Function<T, K> keyFunc, 
                                                Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        
        return from.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                    keyFunc, 
                    valueFunc, 
                    (v1, v2) -> v1,  // 重复key时取第一个值
                    LinkedHashMap::new  // 保持插入顺序
                ));
    }

    /**
     * 将集合转换为Map (可自定义重复key的处理策略)
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, 
                                                Function<T, K> keyFunc, 
                                                Function<T, V> valueFunc,
                                                BinaryOperator<V> mergeFunction) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        
        return from.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction));
    }

    /**
     * 将集合转换为Map (可指定Map类型)
     */
    public static <T, K, V, M extends Map<K, V>> M convertMap(Collection<T> from, 
                                                             Function<T, K> keyFunc, 
                                                             Function<T, V> valueFunc,
                                                             Supplier<M> mapSupplier) {
        if (isEmpty(from)) {
            return mapSupplier.get();
        }
        
        return from.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyFunc, valueFunc, (v1, v2) -> v1, mapSupplier));
    }

    /**
     * 将集合转换为多重Map (一个key对应多个value)
     */
    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from,
                                                            Function<T, K> keyFunc,
                                                            Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        
        return from.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                    keyFunc,
                    Collectors.mapping(valueFunc, Collectors.toList())
                ));
    }

    /**
     * 将集合转换为Set Map (一个key对应不重复的value集合)
     */
    public static <T, K, V> Map<K, Set<V>> convertSetMap(Collection<T> from,
                                                         Function<T, K> keyFunc,
                                                         Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        
        return from.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                    keyFunc,
                    Collectors.mapping(valueFunc, Collectors.toSet())
                ));
    }

    // ==================== 转换操作 ====================

    /**
     * 将集合转换为字符串（使用指定分隔符）
     */
    public static <T> String join(Collection<T> collection, String separator) {
        if (isEmpty(collection)) {
            return StringUtils.EMPTY;
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(separator));
    }

    /**
     * 将集合转换为字符串（使用默认分隔符）
     */
    public static <T> String join(Collection<T> collection) {
        return join(collection, ",");
    }

    /**
     * 提取集合中某个字段的值
     */
    public static <T, R> List<R> extractToList(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换为Map (已废弃，建议使用convertMap)
     * @deprecated 使用 {@link #convertMap(Collection, Function, Function)} 代替
     */
    @Deprecated
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, 
                                           Function<T, K> keyMapper, 
                                           Function<T, V> valueMapper) {
        return convertMap(collection, keyMapper, valueMapper);
    }

    /**
     * 将集合转换为Map（处理重复key的情况，已废弃）
     * @deprecated 使用 {@link #convertMap(Collection, Function, Function, BinaryOperator)} 代替
     */
    @Deprecated
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, 
                                           Function<T, K> keyMapper, 
                                           Function<T, V> valueMapper,
                                           BinaryOperator<V> mergeFunction) {
        return convertMap(collection, keyMapper, valueMapper, mergeFunction);
    }

    // ==================== 过滤操作 ====================

    /**
     * 过滤集合
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 过滤集合并去重
     */
    public static <T> List<T> filterDistinct(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .filter(predicate)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 过滤null值
     */
    public static <T> List<T> filterNotNull(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 分组操作 ====================

    /**
     * 根据条件分组
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> classifier) {
        if (isEmpty(collection)) {
            return new HashMap<>();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(classifier));
    }

    /**
     * 分组后对值进行转换
     */
    public static <T, K, V> Map<K, List<V>> groupBy(Collection<T> collection, 
                                                   Function<T, K> keyClassifier,
                                                   Function<T, V> valueMapper) {
        if (isEmpty(collection)) {
            return new HashMap<>();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                    keyClassifier,
                    Collectors.mapping(valueMapper, Collectors.toList())
                ));
    }

    // ==================== 排序操作 ====================

    /**
     * 根据字段排序
     */
    public static <T, U extends Comparable<? super U>> List<T> sortBy(Collection<T> collection, 
                                                                     Function<T, U> keyExtractor) {
        return sortBy(collection, keyExtractor, true);
    }

    /**
     * 根据字段排序（可指定顺序）
     */
    public static <T, U extends Comparable<? super U>> List<T> sortBy(Collection<T> collection, 
                                                                     Function<T, U> keyExtractor,
                                                                     boolean ascending) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        
        Comparator<T> comparator = Comparator.comparing(keyExtractor);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return collection.stream()
                .filter(Objects::nonNull)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * 多重排序
     */
    @SafeVarargs
    public static <T> List<T> sortByMultiple(Collection<T> collection, 
                                           Comparator<T>... comparators) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        
        if (ArrayUtils.isEmpty(comparators)) {
            return new ArrayList<>(collection);
        }
        
        Comparator<T> combinedComparator = Arrays.stream(comparators)
                .reduce(Comparator::thenComparing)
                .orElse((a, b) -> 0);
        
        return collection.stream()
                .filter(Objects::nonNull)
                .sorted(combinedComparator)
                .collect(Collectors.toList());
    }

    // ==================== 分页操作 ====================

    /**
     * 分页获取数据
     */
    public static <T> List<T> getPage(Collection<T> collection, int pageNum, int pageSize) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        
        if (pageNum <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("页码和页大小必须大于0");
        }
        
        int start = (pageNum - 1) * pageSize;
        if (start >= collection.size()) {
            return new ArrayList<>();
        }
        
        int end = Math.min(start + pageSize, collection.size());
        return collection.stream()
                .skip(start)
                .limit(end - start)
                .collect(Collectors.toList());
    }
    // ==================== 集合运算 ====================

    /**
     * 获取两个集合的交集
     */
    public static <T> List<T> intersection(Collection<T> coll1, Collection<T> coll2) {
        if (isEmpty(coll1) || isEmpty(coll2)) {
            return new ArrayList<>();
        }
        
        Set<T> set1 = new HashSet<>(coll1);
        Set<T> set2 = new HashSet<>(coll2);
        set1.retainAll(set2);
        
        return new ArrayList<>(set1);
    }

    /**
     * 获取两个集合的并集
     */
    public static <T> List<T> union(Collection<T> coll1, Collection<T> coll2) {
        Set<T> result = new HashSet<>();
        if (isNotEmpty(coll1)) {
            result.addAll(coll1);
        }
        if (isNotEmpty(coll2)) {
            result.addAll(coll2);
        }
        return new ArrayList<>(result);
    }

    /**
     * 获取两个集合的差集 (coll1 - coll2)
     */
    public static <T> List<T> difference(Collection<T> coll1, Collection<T> coll2) {
        if (isEmpty(coll1)) {
            return new ArrayList<>();
        }
        if (isEmpty(coll2)) {
            return new ArrayList<>(coll1);
        }
        
        Set<T> set1 = new HashSet<>(coll1);
        Set<T> set2 = new HashSet<>(coll2);
        set1.removeAll(set2);
        
        return new ArrayList<>(set1);
    }

    // ==================== 统计操作 ====================

    /**
     * 统计满足条件的元素数量
     */
    public static <T> long count(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return 0;
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .filter(predicate)
                .count();
    }

    /**
     * 求最大值
     */
    public static <T, U extends Comparable<? super U>> Optional<T> max(Collection<T> collection,
                                                                       Function<T, U> keyExtractor) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(keyExtractor));
    }

    /**
     * 求最小值
     */
    public static <T, U extends Comparable<? super U>> Optional<T> min(Collection<T> collection, 
                                                                      Function<T, U> keyExtractor) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparing(keyExtractor));
    }

    // ==================== 其他实用方法 ====================

    /**
     * 将数组转换为List
     */
    @SafeVarargs
    public static <T> List<T> asList(T... elements) {
        if (ArrayUtils.isEmpty(elements)) {
            return new ArrayList<>();
        }
        return Arrays.stream(elements)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 创建固定大小的List（不可修改）
     */
    @SafeVarargs
    public static <T> List<T> unmodifiableList(T... elements) {
        return Collections.unmodifiableList(asList(elements));
    }

    /**
     * 分割集合
     */
    public static <T> List<List<T>> partition(Collection<T> collection, int size) {
        if (isEmpty(collection) || size <= 0) {
            return new ArrayList<>();
        }
        
        List<T> list = new ArrayList<>(collection);
        List<List<T>> partitions = new ArrayList<>();
        
        for (int i = 0; i < list.size(); i += size) {
            int end = Math.min(i + size, list.size());
            partitions.add(list.subList(i, end));
        }
        
        return partitions;
    }

    /**
     * 随机获取一个元素
     */
    public static <T> Optional<T> random(Collection<T> collection) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }
        
        List<T> list = new ArrayList<>(collection);
        int randomIndex = new Random().nextInt(list.size());
        return Optional.ofNullable(list.get(randomIndex));
    }

    /**
     * 去重并保持原顺序
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }

        Set<T> seen = new LinkedHashSet<>();
        List<T> result = new ArrayList<>();

        for (T element : collection) {
            if (element != null && seen.add(element)) {
                result.add(element);
            }
        }

        return result;
    }

    /**
     * 获取集合的第一个元素
     *
     * @param collection 集合
     * @param <T> 元素类型
     * @return 第一个元素，如果集合为空则返回 null
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof List) {
            return ((List<T>) collection).get(0);
        }
        return collection.iterator().next();
    }

    /**
     * 获取集合的最后一个元素
     *
     * @param collection 集合
     * @param <T> 元素类型
     * @return 最后一个元素，如果集合为空则返回 null
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        T last = null;
        for (T element : collection) {
            last = element;
        }
        return last;
    }
}