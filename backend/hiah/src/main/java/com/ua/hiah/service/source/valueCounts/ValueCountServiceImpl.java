package com.ua.hiah.service.source.valueCounts;

import com.ua.hiah.model.source.ValueCount;
import com.ua.hiah.repository.source.ValueCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValueCountServiceImpl implements ValueCountService {

    @Autowired
    private ValueCountRepository repository;

    @Override
    public ValueCount createValueCount(ValueCount valueCount) {
        return repository.save(valueCount);
    }

    @Override
    public List<ValueCount> createAll(List<ValueCount> valueCounts) {
        return repository.saveAll(valueCounts);
    }
}
