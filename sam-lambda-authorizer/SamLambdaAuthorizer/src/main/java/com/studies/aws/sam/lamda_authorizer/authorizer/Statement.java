package com.studies.aws.sam.lamda_authorizer.authorizer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Statement.Builder.class)
public class Statement {

    public String Action;
    public String Effect;
    public String Resource;

    private Statement(Builder builder) {
        this.Action = builder.action;
        this.Effect = builder.effect;
        this.Resource = builder.resource;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String action;
        private String effect;
        private String resource;

        private Builder() { }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder effect(String effect) {
            this.effect = effect;
            return this;
        }

        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        public Statement build() {
            return new Statement(this);
        }
    }
}
