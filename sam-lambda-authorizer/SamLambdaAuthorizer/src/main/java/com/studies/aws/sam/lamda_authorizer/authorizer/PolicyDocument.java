package com.studies.aws.sam.lamda_authorizer.authorizer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(builder = PolicyDocument.Builder.class)
public class PolicyDocument {

    public String Version;
    public List<Statement> Statement;

    private PolicyDocument(Builder builder) {
        this.Version = builder.version;
        this.Statement = builder.statements;
    }

    public static Builder builder(){
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String version;
        private List<Statement> statements;

        private Builder() {
            statements = new ArrayList<>();
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder statements(List<Statement> statements) {
            this.statements = statements;
            return this;
        }

        public PolicyDocument build() {
            return new PolicyDocument(this);
        }
    }
}