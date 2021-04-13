export const environment = {
    
    ETL_URL: process.env.NODE_ENV === 'production' ? 'http://104.199.21.27:8100/v1/api/etl/' : 'http://localhost:8100/v1/api/etl/',
    TARGET_TABLE_URL: process.env.NODE_ENV === 'production' ? 'http://104.199.21.27:8100/v1/api/targetTable/' : 'http://localhost:8100/v1/api/targetTable/',
    TARGET_FIELD_URL: process.env.NODE_ENV === 'production' ? 'http://104.199.21.27:8100/v1/api/targetField/' : 'http://localhost:8100/v1/api/targetField/',
    SOURCE_TABLE_URL: process.env.NODE_ENV === 'production' ? 'http://104.199.21.27:8100/v1/api/sourceTable/' : 'http://localhost:8100/v1/api/sourceTable/',
    SOURCE_FIELD_URL: process.env.NODE_ENV === 'production' ? 'http://104.199.21.27:8100/v1/api/sourceField/' : 'http://localhost:8100/v1/api/sourceField/',
    TABLE_MAP_URL: process.env.NODE_ENV === 'production' ? 'http://104.199.21.27:8100/v1/api/tableMap/' : 'http://localhost:8100/v1/api/tableMap/',
    FIELD_MAP_URL: process.env.NODE_ENV === 'production' ? 'http://104.199.21.27:8100/v1/api/fieldMap/' : 'http://localhost:8100/v1/api/fieldMap/'
    

    /*    
    ETL_URL: 'http://104.199.21.27:8100/v1/api/etl/',
    TARGET_TABLE_URL: 'http://104.199.21.27:8100/v1/api/targetTable/',
    TARGET_FIELD_URL: 'http://104.199.21.27:8100/v1/api/targetField/',
    SOURCE_TABLE_URL: 'http://104.199.21.27:8100/v1/api/sourceTable/',
    SOURCE_FIELD_URL: 'http://104.199.21.27:8100/v1/api/sourceField/',
    TABLE_MAP_URL: 'http://104.199.21.27:8100/v1/api/tableMap/',
    FIELD_MAP_URL: 'http://104.199.21.27:8100/v1/api/fieldMap/'
    */
};

