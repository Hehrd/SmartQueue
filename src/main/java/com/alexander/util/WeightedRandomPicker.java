package com.alexander.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class WeightedRandomPicker<T> {

    public T pick(List<T> items,
                  List<Integer> weights,
                  Random random) {
        int weightSum = sumWeights(weights);
        List<Range> weightRanges = getRangesList(weights);
        int randomNum = random.nextInt(weightSum) + 1;
        T randomItem = items.get(binarySearchIndex(randomNum, weightRanges));
        return randomItem;
    }

    private int sumWeights(List<Integer> weights) {
        int sum = 0;
        for (Integer weight : weights) {
            sum += weight;
        }
        return sum;
    }

    private List<Range> getRangesList(List<Integer> weights) {
        List<Range> ranges = new ArrayList<>();
        Range firstRange = new Range(1, weights.get(0));
        ranges.add(firstRange);
        for (int i = 1; i < weights.size(); i++) {
            int start = ranges.get(i - 1).getEnd() + 1;
            int end = start + weights.get(i);
            Range range = new Range(start, end);
            ranges.add(range);
        }
        return ranges;
    }

    private int binarySearchIndex(int randomNum, List<Range> ranges) {
        int left = 0;
        int right = ranges.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Range currRange = ranges.get(mid);
            if (currRange.isNumberInRange(randomNum)) {
                return mid;
            } else if (currRange.isAfter(randomNum)) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
}
