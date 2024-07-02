package com.steven.solomon.sort;

import java.util.*;

/**
 * 冒泡排序
 */
public class BubbleSortService implements SortService {

    @Override
    public <T> Collection<T> sort(Collection<T> list, Comparator<? super T> comparator, boolean ascending) {
        if(!ascending){
            comparator = comparator.reversed();
        }
        return sort(list, Arrays.asList(comparator)); // 返回排序后的列表
    }

    @Override
    public <T> Collection<T> sort(Collection<T> list, List<Comparator<? super T>> comparators) {

        List<T> sortedList = new ArrayList<>(list); // 创建一个副本以避免修改原始列表
        boolean swapped;
        int n = sortedList.size();

        // 创建一个复合的Comparator
        Comparator<? super T> compositeComparator = null;
        for (Comparator comparator : comparators) {
            if (compositeComparator == null) {
                compositeComparator = comparator;
            } else {
                compositeComparator = compositeComparator.thenComparing(comparator);
            }
        }

        do {
            swapped = false;
            for (int i = 1; i < n; i++) {
                // 使用复合比较器比较相邻元素
                if (compositeComparator.compare(sortedList.get(i - 1), sortedList.get(i)) > 0) {
                    // 如果前一个元素大于后一个元素，则交换它们
                    Collections.swap(sortedList, i - 1, i);
                    swapped = true;
                }
            }
            // 减少下一次遍历的范围，因为每轮遍历都会将最大的元素移动到正确的位置
            n--;
        } while (swapped); // 如果某轮遍历没有发生交换，说明列表已经排序完成
        return sortedList; // 返回排序后的列表
    }
}
