package com.ua.hiah.service.tableMapping;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.repository.TableMappingRepository;
import com.ua.hiah.service.etl.ETLService;
import com.ua.hiah.service.source.table.SourceTableService;
import com.ua.hiah.service.target.table.TargetTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
