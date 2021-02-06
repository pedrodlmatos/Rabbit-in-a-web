package com.ua.riah.service.tableMapping;

import com.ua.riah.model.TableMapping;

import java.util.List;

public interface TableMappingService {

    TableMapping addTableMapping(Long source_id, Long target_id, Long etl_id);

    void removeFromETL(Long etl_id);

    TableMapping getTableMappingById(Long map_id);

    TableMapping removeTableMapping(Long map_id);

    List<TableMapping> getTableMappingFromETL(Long etl_id);
}