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
async function getDCRAvailableEvents(device){
    let result = [];
    let response = await getApiData(BASE_URL(device));
    if (response) {
        const boolPair = Array.isArray(response)
            ? response.map(item => [!!item.pending, !!item.included])
            : [];
        // debug console.log('Bool pairs:', boolPair);
        result = boolPair;
    }
    return result;
}

module.exports = { getDCRAvailableEvents };


module.exports = { getDCRAvailableEvents };


module.exports = { getDCRAvailableEvents };