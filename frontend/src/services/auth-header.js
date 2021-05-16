export default function authHeader() {
    const user = JSON.parse(localStorage.getItem('user'));

    if (user && user.accessToken) {
        return {
            Authorization: 'Bearer ' + user.accessToken,
            'Access-Control-Allow-Origin': true
        }
    } else
        return {}

    /*
    return {
        'Access-Control-Allow-Origin': true
    }
    */
}