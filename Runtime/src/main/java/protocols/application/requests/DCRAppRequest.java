package protocols.application.requests;

import pt.unl.di.novasys.babel.webservices.WebAPICallback;
import rest.DCRGraphREST.DCREndpoints;

import java.util.LinkedList;
import java.util.List;

// TODO [deprecate]
public class DCRAppRequest {

    private String opID;
    private WebAPICallback callback;
    private List<DCRAppResponse> responses;
    private Object requestBody;
    private DCREndpoints path;
    private int expectedResponses;

    public DCRAppRequest(String opID, WebAPICallback callback, DCREndpoints path) {
        this.opID = opID;
        this.callback = callback;
        this.path = path;
        this.responses = new LinkedList<>();
        this.expectedResponses = 1;
        this.requestBody = null;
    }

    public DCRAppRequest(String opID, WebAPICallback callback, DCREndpoints path,
                                    int expectedResponses) {
        this.opID = opID;
        this.callback = callback;
        this.path = path;
        this.responses = new LinkedList<>();
        this.expectedResponses = expectedResponses;
        this.requestBody = null;
    }

    public DCRAppRequest(String opID, WebAPICallback callback, DCREndpoints path,
                                    Object requestBody) {
        this.opID = opID;
        this.callback = callback;
        this.path = path;
        this.responses = new LinkedList<>();
        this.expectedResponses = 1;
        this.requestBody = requestBody;
    }

    public DCRAppRequest(String opID, WebAPICallback callback, DCREndpoints path,
                                    int expectedResponses, Object requestBody) {
        this.opID = opID;
        this.callback = callback;
        this.path = path;
        this.responses = new LinkedList<>();
        this.expectedResponses = expectedResponses;
        this.requestBody = requestBody;
    }

    public String getOpID() {
        return opID;
    }

    public WebAPICallback getCallback() {
        return callback;
    }

    public List<DCRAppResponse> getResponses() {
        return responses;
    }

    public void addResponse(String postID, String objectID, Object result, Class<? extends Object> type) {
        responses.add(new DCRAppResponse(postID, objectID, result, type));
    }

    public DCREndpoints getPath() {
        return path;
    }

    public int getExpectedResponses() {
        return expectedResponses;
    }

    public boolean hasAllResponses() {
        return this.expectedResponses == responses.size();
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public class DCRAppResponse {
        private String postID;
        private String objectID;
        private Object value;
        private Class<? extends Object> type;

        public DCRAppResponse(String postID, String objectID, Object value, Class<? extends Object> type) {
            this.postID = postID;
            this.objectID = objectID;
            this.value = value;
            this.type = type;
        }

        public String getObjectID() {
            return objectID;
        }

        public Object getValue() {
            return value;
        }

        public Class<? extends Object> getType() {
            return type;
        }

        public String getPostID() {
            return postID;
        }
    }


}
