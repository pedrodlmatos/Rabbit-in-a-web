package com.ua.hiah.service.source.valueCounts;

import com.ua.hiah.model.source.ValueCount;

import java.util.List;

public interface ValueCountService {

    ValueCount createValueCount(ValueCount valueCount);

    List<ValueCount> createAll(List<ValueCount> valueCounts);
}
