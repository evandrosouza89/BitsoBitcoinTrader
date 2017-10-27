package com.evandro.challenges.bitsobitcointrader.controller.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static List queueToList(Queue queue) {
        if (queue != null && !queue.isEmpty()) {
            List list = new ArrayList();
            CollectionUtils.addAll(list, queue);
            return list;
        }
        return new ArrayList();
    }

}
