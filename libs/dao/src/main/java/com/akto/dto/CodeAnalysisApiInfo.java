package com.akto.dto;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class CodeAnalysisApiInfo {

    @BsonId
    private CodeAnalysisApiInfoKey id;
    public static final String ID = "_id";
    private CodeAnalysisApiLocation location;
    public static final String LOCATION = "location";
    
    public static class CodeAnalysisApiInfoKey {
        private ObjectId codeAnalysisCollectionId;
        public static final String CODE_ANALYSIS_COLLECTION_ID = "codeAnalysisCollectionId";
        private String method;
        private String endpoint;

        public CodeAnalysisApiInfoKey() {
        }

        public CodeAnalysisApiInfoKey(ObjectId codeAnalysisCollectionId, String method, String endpoint) {
            this.codeAnalysisCollectionId = codeAnalysisCollectionId;
            this.method = method;
            this.endpoint = endpoint;
        }

        public ObjectId getCodeAnalysisCollectionId() {
            return codeAnalysisCollectionId;
        }

        public void setCodeAnalysisCollectionId(ObjectId codeAnalysisCollectionId) {
            this.codeAnalysisCollectionId = codeAnalysisCollectionId;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

    public CodeAnalysisApiInfo() {
    }

    public CodeAnalysisApiInfo(CodeAnalysisApiInfoKey id, CodeAnalysisApiLocation location) {
        this.id = id;
        this.location = location;
    }

    public CodeAnalysisApiInfoKey getId() {
        return id;
    }

    public void setId(CodeAnalysisApiInfoKey id) {
        this.id = id;
    }

    public CodeAnalysisApiLocation getLocation() {
        return location;
    }

    public void setLocation(CodeAnalysisApiLocation location) {
        this.location = location;
    }

    public String generateCodeAnalysisApisMapKey() {
        return id.getMethod() + " " + id.getEndpoint();
    }
}
