import ETLService from '../../services/etl-list-service'

class ETLOperations {

    /**
     * Sends request to API to change ETL procedure name and after receiving response,
     * disables input to change
     *
     * @param etl ETL procedure
     * @param setDisable function to disable input changes
     */

    saveETLProcedureName = (etl, setDisable) => {
        ETLService
            .changeETLProcedureName(etl.id, etl.name)
            .then(() => {
                setDisable(true);
            })
            .catch(e => console.log(e));
    }


    /**
     * Sends request to API to change EHR database name and after receiving response,
     * disables input to change
     *
     * @param etl ETL procedure
     * @param setDisable function to disable input changes
     */

    saveEHRDatabaseName = (etl, setDisable) => {
        ETLService
            .changeEHRDatabaseName(etl.ehrDatabase.id, etl.id, etl.ehrDatabase.databaseName)
            .then(() => {
                setDisable(true);
            })
            .catch(e => console.log(e));
    }


    /**
     * Delete (mark as deleted) the open ETL procedure and redirect to the user's
     * ETL procedures list page
     */

    deleteETLProcedure = (etl) => {
        ETLService.markETLProcedureAsDeleted(etl.id).then(() => { window.location.href = '/procedures' });
    }
}

export default new ETLOperations();