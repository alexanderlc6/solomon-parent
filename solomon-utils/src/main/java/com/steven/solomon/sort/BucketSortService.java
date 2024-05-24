package com.steven.solomon.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 桶排序
 */
public class BucketSortService implements SortService{

  @Override
  public <T> Collection<T> sort(Collection<T> list, List<Comparator<T>> comparators) {
    if (list == null || list.isEmpty()) {
      return Collections.emptyList(); // 如果列表为空，直接返回空列表
    }

    // 创建一个复合的Comparator
    Comparator<T> compositeComparator = null;
    for (Comparator<T> comparator : comparators) {
      if (compositeComparator == null) {
        compositeComparator = comparator;
      } else {
        compositeComparator = compositeComparator.thenComparing(comparator);
      }
    }

    // 找到列表中的最大值和最小值
    T min = Collections.min(list, compositeComparator);
    T max = Collections.max(list, compositeComparator);

    // 计算桶的数量
    int bucketCount = Math.max(1, (compositeComparator.compare(max, min) / list.size()) + 1);
    List<List<T>> buckets = new ArrayList<>(bucketCount);
    for (int i = 0; i < bucketCount; i++) {
      buckets.add(new ArrayList<>());
    }

    // 将元素分配到各个桶中
    for (T element : list) {
      int bucketIndex = (compositeComparator.compare(element, min) * bucketCount) / (compositeComparator.compare(max, min) + 1);
      buckets.get(bucketIndex).add(element);
    }

    // 对每个桶内的元素进行排序
    for (List<T> bucket : buckets) {
      bucket.sort(compositeComparator);
    }

    // 合并所有桶中的元素
    List<T> sortedList = new ArrayList<>();
    for (Collection<T> bucket : buckets) {
      sortedList.addAll(bucket);
    }

    return sortedList;
  }
}
