package com.ua.hiah.service.tableMapping;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.repository.TableMappingRepository;
import com.ua.hiah.service.etl.ETLService;
import com.ua.hiah.service.source.table.SourceTableService;
import com.ua.hiah.service.target.table.TargetTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TableMappingServiceImpl implements TableMappingService {

    @Autowired
    private TableMappingRepository repository;

    @Autowired
    private ETLService etlService;

    @Autowired
    private SourceTableService sourceTableService;

    @Autowired
    private TargetTableService targetTableService;

    @Override
    public TableMapping getTableMappingById(Long map_id) {
        return repository.findById(map_id).orElse(null);
    }

    @Override
    public TableMapping removeTableMapping(Long map_id) {
        TableMapping mapping = repository.findById(map_id).orElse(null);

        if (mapping == null)
            return null;

        repository.delete(mapping);
        return mapping;
    }

    @Override
    public List<TableMapping> getTableMappingFromETL(Long etl_id) {
        return repository.findAllByEtl_Id(etl_id);
    }

    @Override
    public TableMapping addTableMapping(Long source_id, Long target_id, Long etl_id) {
        TableMapping mapping = new TableMapping();
        mapping.setSource(sourceTableService.getTableById(source_id));
        mapping.setTarget(targetTableService.getTableById(target_id));
        mapping.setComplete(false);
        mapping.setEtl(etlService.getETLWithId(etl_id));
        return repository.save(mapping);
    }

    @Override
    public TableMapping changeCompletionStatus(Long id, boolean completion) {
        TableMapping mapping = repository.findById(id).orElse(null);

        if (mapping != null) {
            mapping.setComplete(completion);
            return repository.save(mapping);
        }
        return null;
    }

    @Override
    public TableMapping changeMappingLogic(Long id, String logic) {
        TableMapping mapping = repository.findById(id).orElse(null);

        if (mapping != null) {
            mapping.setLogic(logic);
            return repository.save(mapping);
        }
        return null;
    }

    @Override
    public void removeTableMappingsFromETL(long etl_id) {
        repository.deleteAllByEtl_Id(etl_id);
    }

    @Override
    public List<TableMapping> getTableMappingsFromJSON(ETL etl, List<TableMapping> tableMappings, SourceDatabase source, TargetDatabase target) {
        List<TableMapping> responseMappings = new ArrayList<>();
        for (TableMapping mapping : tableMappings) {
            SourceTable src = source.getTables().stream().filter(sourceTable -> sourceTable.getName().equals(mapping.getSource().getName())).findFirst().orElse(null);
            TargetTable trg = target.getTables().stream().filter(targetTable -> targetTable.getName().equals(mapping.getTarget().getName())).findFirst().orElse(null);

            if (src != null && trg != null) {
                TableMapping responseMapping = new TableMapping(etl, src, trg, mapping.getLogic());

                for (FieldMapping fieldMapping : mapping.getFieldMappings()) {
                    SourceField srcField = src.getFields().stream().filter(sourceField -> sourceField.getName().equals(fieldMapping.getSource().getName())).findFirst().orElse(null);
                    TargetField trgField = trg.getFields().stream().filter(targetField -> targetField.getName().equals(fieldMapping.getTarget().getName())).findFirst().orElse(null);

                    if (srcField != null && trgField != null) {
                        FieldMapping responseFieldMapping = new FieldMapping(
                                srcField,
                                trgField,
                                fieldMapping.getLogic(),
                                responseMapping
                        );
                        responseMapping.getFieldMappings().add(responseFieldMapping);
                    }

                }
                responseMappings.add(responseMapping);
            }
        }

        return responseMappings;
    }
}
