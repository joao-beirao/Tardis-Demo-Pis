const axios = require('axios');

const API_URL_EXECUTE_EVENT = (id) => `http://localhost:8080/rest/dcr/events/input/${id}/`;

async function updateEvent(eventId, eventData) {
    try {
        const response = await axios.put(
            API_URL_EXECUTE_EVENT(eventId),
            eventData,
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Error updating event:', error.message);
        throw error;
    }
}

// * Extremly hardcoded example
async function executeConsume() {

    const CONSUME_ID = '_csm_2';
    
    const BODY =   
    {
        "eventID": "_csm_2",
        "value": {
            "type": "Record",
            "value": {
                "kw": {
                    "type": "Number",
                    "value": 10
                }
            }
        }
    };

    try {
        const result = await updateEvent(CONSUME_ID, BODY);
        console.log(result);
    } catch (error) {
        console.error(error);
    }
}

executeConsume();

module.exports = { executeConsume };