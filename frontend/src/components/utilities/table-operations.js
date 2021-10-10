import { CDMVersions } from './CDMVersions'
import moment from 'moment'

class TableOperations {

	/**
	 * Sorts the list of ETL procedures according to the parameter and order
	 *
	 * @param paramSort parameter to sort to (OMOP CDM, creation date, modification date)
	 * @param sortOrder sort order (descendent or ascendant)
	 * @param procedures list of ETL procedures
	 * @returns {*|*[]} list of sorted items
	 */

	sortData = (paramSort, sortOrder, procedures) => {
		let itemsToSort = JSON.parse(JSON.stringify(procedures));
		let sortedItems = [];
		let compareFn = null;

		switch (paramSort) {
			case "omop":
				compareFn = (i, j) => {
					let cdmIndexI = CDMVersions.findIndex(function(item) { return item.id === i.omopDatabase.databaseName});
					let cdmIndexJ = CDMVersions.findIndex(function(item) { return item.id === j.omopDatabase.databaseName});
					if (cdmIndexI < cdmIndexJ)
						return sortOrder === "desc" ? -1 : 1;
					else if (cdmIndexI > cdmIndexJ)
						return sortOrder === "desc" ? 1 : -1;
					else
						return 0;
				}
				break;
			case "deleted":
				compareFn = (i, j) => {
					if (i.deleted === false && j.deleted)
						return sortOrder === "desc" ? -1 : 1;
					else if (i.deleted && j.deleted === false)
						return sortOrder === "desc" ? 1 : -1;
					else
						return 0;
				}
				break;
			case "creationDate":
				compareFn = (i, j) => {
					let dateI = moment(i.creationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')
					let dateJ = moment(j.creationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')

					if (dateI > dateJ)
						return sortOrder === "desc" ? -1 : 1;
					else if (dateI < dateJ)
						return sortOrder === "desc" ? 1 : -1;
					else
						return 0;
				}
				break;
			case "modificationDate":
				compareFn = (i, j) => {
					let dateI = moment(i.modificationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')
					let dateJ = moment(j.modificationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')

					if (dateI > dateJ)
						return sortOrder === "desc" ? -1 : 1;
					else if (dateI < dateJ)
						return sortOrder === "desc" ? 1 : -1;
					else
						return 0;
				}
				break;
			case "admin":
				compareFn = (i, j) => {
					let userA = i.roles.some(role => role['name'] === "ROLE_ADMIN");
					let userB = j.roles.some(role => role['name'] === "ROLE_ADMIN");

					if (userA === userB) return 0;
					else if (userA === true && userB === false) return sortOrder === 'desc' ? -1 : 1;
					else return sortOrder === 'desc' ? 1 : -1;
				}
				break;
			default:
				break;
		}

		sortedItems = itemsToSort.sort(compareFn);
		return sortedItems
	}
}

export default new TableOperations();

