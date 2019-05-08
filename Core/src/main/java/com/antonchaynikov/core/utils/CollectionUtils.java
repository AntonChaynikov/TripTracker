package com.antonchaynikov.core.utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static <T, K> List<K> map(@NonNull List<T> inputList, @NonNull Converter<T, K> converter) {
        List<K> output = new ArrayList<>(inputList.size());
        for (T item: inputList) {
            output.add(converter.convert(item));
        }
        return output;
    }

    public interface Converter<T, K> {
        K convert(T item);
    }
}
