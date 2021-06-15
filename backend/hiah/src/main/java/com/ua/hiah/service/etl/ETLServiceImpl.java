package com.ua.hiah.service.etl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.ETL;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.auth.User;
import com.ua.hiah.model.ehr.EHRDatabase;
import com.ua.hiah.model.ehr.EHRField;
import com.ua.hiah.model.ehr.EHRTable;
import com.ua.hiah.model.omop.OMOPDatabase;
import com.ua.hiah.model.omop.OMOPField;
import com.ua.hiah.model.omop.OMOPTable;
import com.ua.hiah.security.services.UserDetailsServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import rabbitcore.utilities.ETLSummaryGenerator;
import rabbitcore.utilities.files.Row;
import com.ua.hiah.repository.ETLRepository;
import com.ua.hiah.service.ehr.database.EHRDatabaseService;
import com.ua.hiah.service.tableMapping.TableMappingService;
import com.ua.hiah.service.omop.database.OMOPDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rabbitinahat.ETLWordDocumentGenerator;
import rabbitinahat.model.ETL_RIAH;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class ETLServiceImpl implements ETLService {

    @Autowired
    ETLRepository etlRepository;

    @Autowired
    UserDetailsServiceImpl userService;

    @Autowired
    OMOPDatabaseService omopDatabaseService;

    @Autowired
    EHRDatabaseService ehrDatabaseService;

    @Autowired
    TableMappingService mappingService;


    /**
     * Retrieve all ETL procedure
     *
     * @return list with ETL procedures
     */

    @Override
    public List<ETL> getAllETL() {
        return etlRepository.findAll();
    }


    /**
     * Retrieves list of ETL procedures managed by user
     *
     * @param username user
     * @return list of ETL procedures
     */

    @Override
    public List<ETL> getETLByUsername(String username) {
        User user = userService.getUserByUsername(username);
        // user not found
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        return etlRepository.findAllByUsersContainingAndDeleted(user, false);
    }


    /**
     * Retrieves and ETL procedure's by its id and if user has access to it
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return ETL procedure if user has access to it, error otherwise
     */

    @Override
    public ETL getETLWithId(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        // username not found
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        // user has access to ETL or is admin
        if (userHasAccessToEtl(etl, user) || userService.userIsAdmin(user)) {
            // sort field from the EHR database by id
            for (EHRTable ehrTable : etl.getSourceDatabase().getTables())
                ehrTable.getFields().sort(Comparator.comparingLong(EHRField::getId));
            // sort tables from the EHR database by id
            etl.getSourceDatabase().getTables().sort(Comparator.comparingLong(EHRTable::getId));

            // sort fields from the OMOP CDM database by id
            for (OMOPTable omopTable : etl.getTargetDatabase().getTables())
                omopTable.getFields().sort(Comparator.comparingLong(OMOPField::getId));
            // sort tables from the OMOP CDM database by id
            etl.getTargetDatabase().getTables().sort(Comparator.comparingLong(OMOPTable::getId));

            return etl;
        } else {
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
        }
    }


    /**
     * Retrieves an ETL procedure by its id
     *
     * @param id ETL procedure's id
     * @return ETL session
     */

    @Override
    public ETL getETLWithId(Long id) {
        ETL etl = etlRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", id.toString()));

        // sort field from the EHR database by id
        for (EHRTable ehrTable : etl.getSourceDatabase().getTables())
            ehrTable.getFields().sort(Comparator.comparingLong(EHRField::getId));
        // sort tables from the EHR database by id
        etl.getSourceDatabase().getTables().sort(Comparator.comparingLong(EHRTable::getId));

        // sort fields from the OMOP CDM database by id
        for (OMOPTable omopTable : etl.getTargetDatabase().getTables())
            omopTable.getFields().sort(Comparator.comparingLong(OMOPField::getId));
        // sort tables from the OMOP CDM database by id
        etl.getTargetDatabase().getTables().sort(Comparator.comparingLong(OMOPTable::getId));

        return etl;
    }


    /**
     * Creates a new ETL procedure
     *
     * @param ehrName EHR database name
     * @param ehrScan EHR Scan report
     * @param cdm OMOP CDM version
     * @param username User's username
     * @return created ETL procedure
     */

    @Override
    public ETL createETLProcedure(String ehrName, MultipartFile ehrScan, String cdm, String username) {
        User user = userService.getUserByUsername(username);
        // user not found
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        if (omopDatabaseService.CDMExists(cdm)) {
            ETL etl = new ETL();
            etl.setName("ETL procedure " + etlRepository.count());
            etl.setTargetDatabase(omopDatabaseService.generateModelFromCSV(CDMVersion.valueOf(cdm)));
            etl.setSourceDatabase(ehrDatabaseService.createDatabaseFromScanReport(ehrName, ehrScan));
            etl.getUsers().add(user);

            // define dates
            etl.setCreationDate(Date.from(Instant.now()));
            etl.setModificationDate(Date.from(Instant.now()));
            return etlRepository.save(etl);
        } else {
            throw new EntityNotFoundException(CDMVersion.class, "OMOP CDM", cdm);
        }
    }


    /**
     * Creates an ETL procedure from the save file created
     *
     * @param saveFile JSON file containing info about an ETL procedure
     * @param username User's username
     * @return created ETL procedure
     */

    @Override
    public ETL createETLProcedureFromFile(MultipartFile saveFile, String username) {
        User user = userService.getUserByUsername(username);
        // user not found
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        try {
            // write content in a file object
            File tempSaveFile = new File("tempSave.json");
            if(tempSaveFile.createNewFile()) {
                OutputStream os = new FileOutputStream(tempSaveFile);
                os.write(saveFile.getBytes());
                os.close();
            }

            // read file as a json object
            FileInputStream inputStream = new FileInputStream(tempSaveFile);
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonReader jsonReader = new JsonReader(reader);
            Gson gson = new Gson();
            ETL request = gson.fromJson(jsonReader, ETL.class);

            ETL response = new ETL();
            response.setName("ETL procedure " + etlRepository.count());

            // create source database from json
            EHRDatabase source = ehrDatabaseService.createDatabaseFromJSON(request.getSourceDatabase());
            response.setSourceDatabase(source);

            // create target database from json
            OMOPDatabase target = omopDatabaseService.createDatabaseFromJSON(request.getTargetDatabase());
            response.setTargetDatabase(target);

            // create mappings from json
            List<TableMapping> mappings = mappingService.getTableMappingsFromJSON(response, request.getTableMappings(), source, target);
            response.setTableMappings(mappings);

            // add user to etl object
            response.getUsers().add(user);

            // define dates
            response.setCreationDate(Date.from(Instant.now()));
            response.setModificationDate(Date.from(Instant.now()));

            // delete file object
            if (tempSaveFile.delete()) { }

            // persist
            return etlRepository.save(response);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Deletes ETL procedure given its id (operation by ADMIN)
     *
     * @param etl_id ETL procedure's id
     */

    @Override
    public void deleteETLProcedure(Long etl_id) {
        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        etlRepository.delete(etl);
    }


    /**
     * Changes the name of an ETL procedure
     *
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @param name name to change to
     */

    @Override
    public void changeETLProcedureName(Long etl_id, String username, String name) {
        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        if (userHasAccessToEtl(etl, user)) {
            etl.setName(name);
            etl.setModificationDate(Date.from(Instant.now()));
            etlRepository.save(etl);
        } else throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Changes the name of the EHR database
     *
     * @param sourceDatabaseId source database's id
     * @param name name to change to
     * @param etl_id ETL procedure's id
     * @param username user's username
     */

    @Override
    public void changeEHRDatabaseName(Long sourceDatabaseId, String name, Long etl_id, String username) {
        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        if (userHasAccessToEtl(etl, user)) {
            etl.getSourceDatabase().setDatabaseName(name);
            etl.setModificationDate(Date.from(Instant.now()));
            etlRepository.save(etl);
        } else throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Adds a list of users to the list of collaborators of an ETL procedure
     *
     * @param usersToInvite users to add usernames
     * @param etl_id ETL procedure's id
     * @param username User's who made request username
     * @return ETL procedure with new list of collaborators
     */

    @Override
    public ETL addETLCollaborator(String[] usersToInvite, Long etl_id, String username) {
        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        if (userHasAccessToEtl(etl, user)) {
            for (String usernameToInvite : usersToInvite) {
                User invited = userService.getUserByUsername(usernameToInvite);
                if (usernameToInvite == null) throw new EntityNotFoundException(User.class, "username", username);

                etl.getUsers().add(invited);
            }
            etl.setModificationDate(Date.from(Instant.now()));
            return etlRepository.save(etl);
        } else throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Removes a user from the list of collaborators of an ETL procedure
     *
     * @param userToRemove user's to remove username
     * @param etl_id ETL procedure's id
     * @param username User's who made request username
     * @return ETL procedure with new list of collaborators
     */

    @Override
    public ETL removeETLCollaborator(String userToRemove, Long etl_id, String username) {
        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        if (userHasAccessToEtl(etl, user)) {
            // get user to remove
            User removeUser = userService.getUserByUsername(userToRemove);
            if (removeUser == null) throw new EntityNotFoundException(User.class, "username", username);
            // remove user
            etl.getUsers().remove(removeUser);
            // update dates
            etl.setModificationDate(Date.from(Instant.now()));
            return etlRepository.save(etl);
        } else throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Marks an ETL procedure as deleted but doesn't remove it from database
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    @Override
    public void markAsDeleted(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        // user not found
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user) || userService.userIsAdmin(user)) {
            etl.setDeleted(true);
            etl.setModificationDate(Date.from(Instant.now()));
            etlRepository.save(etl);
        } else {
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
        }
    }


    /**
     * Verifies if a user is a collaborator of an ETL procedure
     *
     * @param etl ETL procedure
     * @param user user
     * @return true if user is a collaborator, false otherwise
     */

    @Override
    public boolean userHasAccessToEtl(ETL etl, User user) {
        return etl.getUsers().contains(user);
    }


    /**
     * Verifies if a user is a collaborator of an ETL procedure
     *
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return true if user is a collaborator, false otherwise
     */

    @Override
    public boolean userHasAccessToEtl(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        return userHasAccessToEtl(etl, user);
    }


    /**
     * Marks an ETL procedure as not deleted
     *
     * @param etl_id ETL procedure's id
     */

    @Override
    public void markAsNotDeleted(Long etl_id) {
        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));;

        etl.setDeleted(false);
        etlRepository.save(etl);
    }


    /**
     * Changes the OMOP CDM version of an ETL procedure and removes the previous OMOP CDM used
     *
     * @param etl_id ETL procedure's id
     * @param cdm OMOP CDM version to change to
     * @param username User's username
     * @return modified ETL procedure
     */

    @Override
    public ETL changeTargetDatabase(Long etl_id, String cdm, String username) {
        User user = userService.getUserByUsername(username);
        // user not found
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user)) {
            // create an OMOP CDM from a different version
            etl.setTargetDatabase(omopDatabaseService.generateModelFromCSV(CDMVersion.valueOf(cdm)));
            // remove previous cdm and mappings
            mappingService.removeTableMappingsFromETL(etl.getId());
            //targetDatabaseService.removeDatabase(previous.getId());

            // order tables by id
        /*
        List<SourceTable> sourceTables = etl.getSourceDatabase().getTables();
        Collections.sort(sourceTables, Comparator.comparingLong(SourceTable::getId));

        List<TargetTable> targetTables = etl.getTargetDatabase().getTables();
        Collections.sort(targetTables, Comparator.comparingLong(TargetTable::getId));
         */

            // define dates
            etl.setModificationDate(Date.from(Instant.now()));
            return etlRepository.save(etl);
        } else
            throw new UnauthorizedAccessException(User.class, username, etl_id);
    }


    /**
     * Adds stem table on EHR and on OMOP CDM database
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered ETL procedure
     */

    @Override
    public ETL addStemTable(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user)) {
            if (!containsStemTable(etl)) {
                CDMVersion version = etl.getTargetDatabase().getVersion();
                // add stem table on EHR database
                EHRDatabase ehrDatabase = etl.getSourceDatabase();
                EHRTable sourceStemTable = ehrDatabaseService.createEHRStemTable(version, ehrDatabase);
                ehrDatabase.getTables().add(sourceStemTable);
                etl.setSourceDatabase(ehrDatabase);

                // add stem table on OMOP CDM database
                OMOPDatabase omopDatabase = etl.getTargetDatabase();
                OMOPTable targetStemTable = omopDatabaseService.createTargetStemTable(version, omopDatabase);
                omopDatabase.getTables().add(targetStemTable);
                etl.setTargetDatabase(omopDatabase);

                // add mappings from and to stem table
                List<TableMapping> prevTableMappings = etl.getTableMappings();
                List<TableMapping> tableMappings = mappingService.createMappingsWithStemTable(version, omopDatabase, sourceStemTable, etl);
                prevTableMappings.addAll(tableMappings);
                etl.setTableMappings(prevTableMappings);

                // define dates
                etl.setModificationDate(Date.from(Instant.now()));
                return etlRepository.save(etl);
            } else
                return etl;
        } else
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Verifies if a database has a stem table
     *
     * @param etl ETL procedure
     * @return true if already has stem table, false otherwise
     */

    private boolean containsStemTable(ETL etl) {
        for (EHRTable table : etl.getSourceDatabase().getTables()) {
            if (table.isStem())
                return true;
        }
        return false;
    }


    /**
     * Removes stem table from EHR and OMOP CDM database and their respective mapping
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered ETL procedure
     */

    @Override
    public ETL removeStemTable(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user)) {
            for (EHRTable table : etl.getSourceDatabase().getTables()) {
                if (table.isStem()) {
                    //mappingService.removeTableMappingsFromTable(etl_id, table);
                    ehrDatabaseService.removeTable(table);
                }
            }

            for (OMOPTable table : etl.getTargetDatabase().getTables())
                if (table.isStem()) {
                    //mappingService.removeTableMappingsToTable(etl_id, table);
                    omopDatabaseService.removeTable(table);
                }

            // define dates
            etl.setModificationDate(Date.from(Instant.now()));
            return etlRepository.findById(etl_id).orElse(null);
        } else
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Creates a JSON file with the current state of an ETL procedure given its id
     * (can be used later to create an ETL procedure using a file)
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return ETL content as JSON object
     */

    @Override
    public byte[] createSavingFile(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user)) {
            try {
                // Create GSON object
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

                // sort field from the EHR database by id
                for (EHRTable ehrTable : etl.getSourceDatabase().getTables())
                    ehrTable.getFields().sort(Comparator.comparingLong(EHRField::getId));
                // sort tables from the EHR database by id
                etl.getSourceDatabase().getTables().sort(Comparator.comparingLong(EHRTable::getId));

                // sort fields from the OMOP CDM database by id
                for (OMOPTable omopTable : etl.getTargetDatabase().getTables())
                    omopTable.getFields().sort(Comparator.comparingLong(OMOPField::getId));
                // sort tables from the OMOP CDM database by id
                etl.getTargetDatabase().getTables().sort(Comparator.comparingLong(OMOPTable::getId));

                // transform ETL object to json object (keeping only needed attributes)
                String etlJsonStr = gson.toJson(etl);

                FileOutputStream fileOutputStream = new FileOutputStream("Scan.json");
                //GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                out.write(etlJsonStr);

                return etlJsonStr.getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        } else
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }




    /**
     * Creates the file with source fields summary
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return source field summary
     */

    @Override
    public byte[] createSourceFieldListCSV(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user)) {
            // sort field from the EHR database by id
            for (EHRTable ehrTable : etl.getSourceDatabase().getTables())
                ehrTable.getFields().sort(Comparator.comparingLong(EHRField::getId));
            // sort tables from the EHR database by id
            etl.getSourceDatabase().getTables().sort(Comparator.comparingLong(EHRTable::getId));

            // sort fields from the OMOP CDM database by id
            for (OMOPTable omopTable : etl.getTargetDatabase().getTables())
                omopTable.getFields().sort(Comparator.comparingLong(OMOPField::getId));
            // sort tables from the OMOP CDM database by id
            etl.getTargetDatabase().getTables().sort(Comparator.comparingLong(OMOPTable::getId));

            List<Row> rows = ETLSummaryGenerator.createSourceFieldList(etl);
            return ETLSummaryGenerator.writeCSV("sourceList.csv", rows);
        } else
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Creates the file with target fields summary
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return target field summary
     */

    @Override
    public byte[] createTargetFieldListCSV(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user)) {
            // sort field from the EHR database by id
            for (EHRTable ehrTable : etl.getSourceDatabase().getTables())
                ehrTable.getFields().sort(Comparator.comparingLong(EHRField::getId));
            // sort tables from the EHR database by id
            etl.getSourceDatabase().getTables().sort(Comparator.comparingLong(EHRTable::getId));

            // sort fields from the OMOP CDM database by id
            for (OMOPTable omopTable : etl.getTargetDatabase().getTables())
                omopTable.getFields().sort(Comparator.comparingLong(OMOPField::getId));
            // sort tables from the OMOP CDM database by id
            etl.getTargetDatabase().getTables().sort(Comparator.comparingLong(OMOPTable::getId));

            List<Row> rows = ETLSummaryGenerator.createTargetFieldList(etl);
            return ETLSummaryGenerator.writeCSV("targetList.csv", rows);
        } else
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }


    /**
     * Creates the ETL procedure summary file
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return file content or null
     */

    @Override
    public byte[] createWordSummaryFile(Long etl_id, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) throw new EntityNotFoundException(User.class, "username", username);

        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));

        if (userHasAccessToEtl(etl, user)) {
            // sort field from the EHR database by id
            for (EHRTable ehrTable : etl.getSourceDatabase().getTables())
                ehrTable.getFields().sort(Comparator.comparingLong(EHRField::getId));
            // sort tables from the EHR database by id
            etl.getSourceDatabase().getTables().sort(Comparator.comparingLong(EHRTable::getId));

            // sort fields from the OMOP CDM database by id
            for (OMOPTable omopTable : etl.getTargetDatabase().getTables())
                omopTable.getFields().sort(Comparator.comparingLong(OMOPField::getId));
            // sort tables from the OMOP CDM database by id
            etl.getTargetDatabase().getTables().sort(Comparator.comparingLong(OMOPTable::getId));

            ETL_RIAH etlRiah = new ETL_RIAH(etl);

            byte[] documentData = ETLWordDocumentGenerator.generate(etlRiah);
            if (documentData != null)
                return documentData;
            else
                throw new RuntimeException();
        } else
            throw new UnauthorizedAccessException(ETL.class, username, etl_id);
    }

    @Override
    public void updateModificationDate(Long etl_id) {
        ETL etl = etlRepository.findById(etl_id).orElseThrow(() -> new EntityNotFoundException(ETL.class, "id", etl_id.toString()));
        etl.setModificationDate(Date.from(Instant.now()));
        etlRepository.save(etl);
    }






























    @Override
    public void updateModificationDate(ETL etl) {
        etl.setModificationDate(Date.from(Instant.now()));
        etlRepository.save(etl);
    }
}
