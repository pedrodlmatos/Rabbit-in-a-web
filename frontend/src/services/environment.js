const productionURL = 'http://34.76.46.230:8100/v1/api/';

export const environment = {

    ETL_URL: process.env.NODE_ENV === 'production' ? productionURL + 'etl/' : 'http://localhost:8100/v1/api/etl/',
    TARGET_TABLE_URL: process.env.NODE_ENV === 'production' ? productionURL + 'omopTable/' : 'http://localhost:8100/v1/api/omopTable/',
    TARGET_FIELD_URL: process.env.NODE_ENV === 'production' ? productionURL + 'omopField/' : 'http://localhost:8100/v1/api/omopField/',
    SOURCE_TABLE_URL: process.env.NODE_ENV === 'production' ? productionURL + 'ehrTable/' : 'http://localhost:8100/v1/api/ehrTable/',
    SOURCE_FIELD_URL: process.env.NODE_ENV === 'production' ? productionURL + 'ehrField/' : 'http://localhost:8100/v1/api/ehrField/',
    TABLE_MAP_URL: process.env.NODE_ENV === 'production' ? productionURL + 'tableMap/' : 'http://localhost:8100/v1/api/tableMap/',
    FIELD_MAP_URL: process.env.NODE_ENV === 'production' ? productionURL + 'fieldMap/' : 'http://localhost:8100/v1/api/fieldMap/',
    AUTH_URL : process.env.NODE_ENV === 'production' ? productionURL + 'auth/' : 'http://localhost:8100/v1/api/auth/',
    USER_URL : process.env.NODE_ENV === 'production' ? productionURL + 'users/' : 'http://localhost:8100/v1/api/users/'

};

