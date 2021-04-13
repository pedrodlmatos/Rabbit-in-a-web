package com.ua.hiah.service.tableMapping;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.target.TargetDatabase;

import java.util.List;

public interface TableMappingService {

    TableMapping addTableMapping(Long source_id, Long target_id, Long etl_id);

    TableMapping getTableMappingById(Long map_id);

    TableMapping removeTableMapping(Long map_id);

    List<TableMapping> getTableMappingFromETL(Long etl_id);

    TableMapping changeCompletionStatus(Long id, boolean completion);

    TableMapping changeMappingLogic(Long id, String logic);

    void removeTableMappingsFromETL(long etl_id);

    List<TableMapping> getTableMappingsFromJSON(ETL response, List<TableMapping> tableMappings, SourceDatabase source, TargetDatabase target);
}
