package com.api.tests.utils;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtils {

    public static <T> T getRandomListElement(final List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalStateException("Impossible to get random element from empty list");
        }
        final int index = Faker.instance().random().nextInt(0, list.size() - 1);
        return list.get(index);
    }

    public static <T extends Enum<?>> T getRandomEnum(final Class<T> enumeration) {
        return getRandomListElement(Arrays.stream(enumeration.getEnumConstants()).collect(Collectors.toList()));
    }

}
