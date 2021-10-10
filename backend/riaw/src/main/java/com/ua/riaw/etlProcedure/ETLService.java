package com.ua.riaw.etlProcedure;

import com.ua.riaw.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ETLService {

    /**
     * Retrieve all ETL procedure
     *
     * @return list with ETL procedures
     */

    List<ETL> getAllETL();


    /**
     * Retrieves list of ETL procedures managed by user
     *
     * @param username user
     * @return list of ETL procedures
     */

    List<ETL> getETLByUsername(String username);


    /**
     * Retrieves list of the most recently updated ETL procedures where user has access
     *
     * @param username user
     * @return list of most recent ETL procedures
     */

    List<ETL> getRecentProcedures(String username);


    /**
     * Retrieves list of ETL procedures shared between two users
     *
     * @param username user who made request
     * @param otherUser other user
     * @return list of ETL procedures
     */

    List<ETL> getAllBetweenUsers(String username, String otherUser);


    /**
     * Retrieves and ETL procedure's by its id and if user has access to it
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return ETL procedure if user has access to it, error otherwise
     */

    ETL getETLWithId(Long etl_id, String username);


    /**
     * Retrieves an ETL procedure by its id
     *
     * @param id ETL procedure's id
     * @return ETL session
     */

    ETL getETLWithId(Long id);


    /**
     * Creates a new ETL procedure
     *
     * @param ehrName EHR database name
     * @param ehrScan EHR Scan report
     * @param cdm OMOP CDM version
     * @param username
     * @return created ETL procedure
     */

    ETL createETLProcedure(String ehrName, MultipartFile ehrScan, String cdm, String username);


    /**
     * Creates an ETL procedure from the save file created
     *
     * @param saveFile JSON file containing info about an ETL procedure
     * @param username
     * @return created ETL procedure
     */

    ETL createETLProcedureFromFile(MultipartFile saveFile, String username);


    /**
     * Changes the name of an ETL procedure
     *
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @param name name to change to
     */

    void changeETLProcedureName(Long etl_id, String username, String name);


    /**
     * Deletes ETL procedure given its id (operation by ADMIN)
     *
     * @param etl_id ETL procedure's id
     */

    void deleteETLProcedure(Long etl_id);


    /**
     * Changes the name of the EHR database
     *
     * @param sourceDatabaseId source database's id
     * @param name name to change to
     * @param etl_id ETL procedure's id
     * @param username user's username
     */

    void changeEHRDatabaseName(Long sourceDatabaseId, String name, Long etl_id, String username);


    /**
     * Adds a list of users to the list of collaborators of an ETL procedure
     *
     * @param usersToInvite users to add usernames
     * @param etl_id ETL procedure's id
     * @param username User's who made request username
     * @return ETL procedure with new list of collaborators
     */

    ETL addETLCollaborator(String[] usersToInvite, Long etl_id, String username);


    /**
     * Removes a user from the list of collaborators of an ETL procedure
     *
     * @param userToRemove user's to remove username
     * @param etl_id ETL procedure's id
     * @param username User's who made request username
     * @return ETL procedure with new list of collaborators
     */

    ETL removeETLCollaborator(String userToRemove, Long etl_id, String username);


    /**
     * Removes a user from the list of collaborators of an ETL procedure
     *  @param user user to remove
     * @param etl_id ETL procedure's id
     */

    void removeETLCollaborator(User user, Long etl_id);




    /**
     * Marks an ETL procedure as deleted but doesn't remove it from database
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    void markAsDeleted(Long etl_id, String username);


    /**
     * Verifies if a user is a collaborator of an ETL procedure
     *
     * @param etl ETL procedure
     * @param user user
     * @return true if user is a collaborator, false otherwise
     */

    boolean userHasAccessToEtl(ETL etl, User user);


    /**
     * Verifies if a user is a collaborator of an ETL procedure
     *
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return true if user is a collaborator, false otherwise
     */

    boolean userHasAccessToEtl(Long etl_id, String username);


    /**
     * Marks an ETL procedure as not deleted
     *
     * @param etl_id ETL procedure's id
     */

    void markAsNotDeleted(Long etl_id);


    /**
     * Changes the OMOP CDM version of an ETL procedure and removes the previous OMOP CDM used
     *
     * @param etl_id ETL procedure's id
     * @param cdm OMOP CDM version to change to
     * @param username
     * @return modified ETL procedure
     */

    ETL changeTargetDatabase(Long etl_id, String cdm, String username);


    /**
     * Adds stem table on EHR and on OMOP CDM database
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered ETL procedure
     */

    ETL addStemTable(Long etl_id, String username);


    /**
     * Removes stem table from EHR and OMOP CDM database and their respective mapping
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered ETL procedure
     */

    ETL removeStemTable(Long etl_id, String username);


    /**
     * Creates a JSON file with the current state of an ETL procedure given its id
     * (can be used later to create an ETL procedure using a file)
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return ETL content as JSON object
     */

    byte[] createSavingFile(Long etl_id, String username);


    /**
     * Creates the file with source fields summary
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return source field summary
     */

    byte[] createSourceFieldListCSV(Long etl_id, String username);


    /**
     * Creates the file with target fields summary
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return target field summary
     */

    byte[] createTargetFieldListCSV(Long etl_id, String username);


    /**
     * Creates the ETL procedure summary file
     *
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return file content or null
     */

    byte[] createWordSummaryFile(Long etl_id, String username);


    /**
     * Updates the modification date of an ETL procedure
     *
     * @param etl_id ETL procedure's id
     */

    void updateModificationDate(Long etl_id);
}
