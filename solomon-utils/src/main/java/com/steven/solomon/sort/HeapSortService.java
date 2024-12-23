package com.steven.solomon.sort;

import java.util.*;

/**
 * 堆排序
 */
public class HeapSortService implements SortService {

    @Override
    public <T> Collection<T> sort(Collection<T> list, Comparator<? super T> comparator) {

        int n = list.size();
        List<T> data = new ArrayList<>(list);

        // 构建最大堆
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(data, i, n, comparator);
        }

        // 一个一个地从堆中取出元素
        for (int i = n - 1; i >= 0; i--) {
            swap(data, 0, i);

            // 重新对堆进行调整
            heapify(data, 0, i, comparator);
        }

        return data;
    }

    private static <T> void heapify(List<T> list, int i, int n, Comparator<? super T> comparator) {
        int largest = i; // 初始化最大值为根节点
        int left = 2 * i + 1; // 左子节点
        int right = 2 * i + 2; // 右子节点

        // 如果左子节点在范围内且大于根节点
        if (left < n && comparator.compare(list.get(left), list.get(largest)) > 0) {
            largest = left;
        }

        // 如果右子节点在范围内且大于当前最大值
        if (right < n && comparator.compare(list.get(right), list.get(largest)) > 0) {
            largest = right;
        }

        // 如果最大值不是根节点
        if (largest != i) {
            swap(list, i, largest);
            // 递归地调整受影响的子树
            heapify(list, largest, n, comparator);
        }
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

}
