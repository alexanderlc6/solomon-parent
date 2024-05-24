package com.steven.solomon.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 桶排序
 */
public class BucketSortService implements SortService{

  @Override
  public <T> List<T> sort(List<T> list, Comparator<? super T> comparator, boolean ascending) {
    if (list == null || list.isEmpty()) {
      return Collections.emptyList(); // 如果列表为空，直接返回空列表
    }

    // 找到列表中的最大值和最小值
    T min = Collections.min(list, comparator);
    T max = Collections.max(list, comparator);

    // 计算桶的数量
    int bucketCount = Math.max(1, (comparator.compare(max, min) / list.size()) + 1);
    List<List<T>> buckets = new ArrayList<>(bucketCount);
    for (int i = 0; i < bucketCount; i++) {
      buckets.add(new ArrayList<>());
    }

    // 将元素分配到各个桶中
    for (T element : list) {
      int bucketIndex = (comparator.compare(element, min) * bucketCount) / (comparator.compare(max, min) + 1);
      buckets.get(bucketIndex).add(element);
    }

    // 对每个桶内的元素进行排序
    for (List<T> bucket : buckets) {
      Collections.sort(bucket, comparator);
    }

    // 合并所有桶中的元素
    List<T> sortedList = new ArrayList<>();
    for (List<T> bucket : buckets) {
      sortedList.addAll(bucket);
    }

    // 如果需要降序排序，则反转列表
    if (!ascending) {
      Collections.reverse(sortedList);
    }

    return sortedList;
  }
}
