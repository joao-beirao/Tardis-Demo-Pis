const { get } = require('http');

const BASE_URL = (device) => `http://${device}:8080/rest/dcr/events/enable`;

// * Reads URL and returns JSON response
function getApiData(url) {
    return new Promise((resolve, reject) => {
        get(url, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                try {
                    const json = JSON.parse(data);
                    resolve(json);
                } catch (err) {
                    reject(err);
                }
            });
        }).on('error', (err) => {
            reject(err);
        });
    });
}

// * Fetches DCR enable events from the specified device
function getDCREnableEvents(device){
    let response = getApiData(BASE_URL(device));
    response.then((data) => {
        console.log('API Response:', data);
        return data;
    })
    .catch((err) => {
        console.error('Error:', err);
    });
}
// * Fetches DCR available events and converts to boolean pairs
function getDCRAvailableEvents(device){
    let result = [];
    let response = getApiData(BASE_URL(device));
    response.then((data) => {
        const boolPair = Array.isArray(data)
            ? data.map(item => [!!item.pending, !!item.included])
            : [];
        console.log('Bool pairs:', boolPair);
        result = boolPair;
    })
    .catch((err) => {
        console.error('Error:', err);
    });
    return result;
}

module.exports = { getDCRAvailableEvents };


module.exports = { getDCRAvailableEvents };