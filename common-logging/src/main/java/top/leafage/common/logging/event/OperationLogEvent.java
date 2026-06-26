/*
 * Copyright (c) 2026.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.common.logging.event;

import java.util.Map;

/**
 * Operation log
 *
 * @author wq li
 */
public class OperationLogEvent {

    private String module;

    private String action;

    private Long targetId;

    private Map<String, Object> params;

    private String response;

    private Status status;

    private long duration;

    private String message;


    /**
     * Constructor for OperationLogEvent.
     */
    public OperationLogEvent() {
    }

    /**
     * Status enum.
     */
    public enum Status {
        /**
         * succeed
         */
        SUCCEED,

        /**
         * failed
         */
        FAILED;

        /**
         * Get Status from value.
         *
         * @param value a {@link String} object
         * @return The Status
         */
        public static Status of(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    /**
     * Getter for the field <code>module</code>.
     *
     * @return a {@link java.lang.String} object
     */
    public String getModule() {
        return module;
    }

    /**
     * Setter for the field <code>module</code>.
     *
     * @param module a {@link java.lang.String} object
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * Getter for the field <code>action</code>.
     *
     * @return a {@link java.lang.String} object
     */
    public String getAction() {
        return action;
    }

    /**
     * Setter for the field <code>action</code>.
     *
     * @param action a {@link java.lang.String} object
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Getter for the field <code>targetId</code>.
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * Setter for the field <code>targetId</code>.
     *
     * @param targetId a {@link java.lang.Long} object
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * Getter for the field <code>params</code>.
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * Setter for the field <code>params</code>.
     *
     * @param params a {@link java.util.Map} object
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * Getter for the field <code>response</code>.
     *
     * @return a {@link java.lang.String} object
     */
    public String getResponse() {
        return response;
    }

    /**
     * Setter for the field <code>response</code>.
     *
     * @param response a {@link java.lang.String} object
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Setter for the field <code>status</code>.
     *
     * @param status a {@link OperationLogEvent.Status} object
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Getter for the field <code>status</code>.
     *
     * @return a {@link OperationLogEvent.Status} object
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Getter for the field <code>duration</code>.
     *
     * @return a long
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Setter for the field <code>duration</code>.
     *
     * @param duration a long
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Getter for the field <code>message</code>.
     *
     * @return a {@link java.lang.String} object
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the field <code>message</code>.
     *
     * @param message a {@link java.lang.String} object
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * builder.
     *
     * @return a {@link top.leafage.common.logging.event.OperationLogEvent.Builder} object
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * builder.
     */
    public static class Builder {

        private final OperationLogEvent event = new OperationLogEvent();

        /**
         * Set module
         *
         * @param module a {@link String} object
         * @return the builder
         */
        public Builder module(String module) {
            event.module = module;
            return this;
        }

        /**
         * Set action
         *
         * @param action a {@link String} object
         * @return the builder
         */
        public Builder action(String action) {
            event.action = action;
            return this;
        }

        /**
         * Set targetId
         *
         * @param targetId a {@link Long} object
         * @return the builder
         */
        public Builder targetId(Long targetId) {
            event.targetId = targetId;
            return this;
        }

        /**
         * Set params
         *
         * @param params a {@link Map} object
         * @return the builder
         */
        public Builder params(Map<String, Object> params) {
            event.params = params;
            return this;
        }

        /**
         * Set response
         *
         * @param response a {@link String} object
         * @return the builder
         */
        public Builder response(String response) {
            event.response = response;
            return this;
        }

        /**
         * Set status
         *
         * @param status a {@link String} object
         * @return the builder
         */
        public Builder status(Status status) {
            event.status = status;
            return this;
        }

        /**
         * Set duration
         *
         * @param duration a long object
         * @return the builder
         */
        public Builder duration(long duration) {
            event.duration = duration;
            return this;
        }

        /**
         * Set message
         *
         * @param message a {@link String} object
         * @return the builder
         */
        public Builder message(String message) {
            event.message = message;
            return this;
        }

        /**
         * Build a OperationLogEvent.
         *
         * @return the OperationLogEvent
         */
        public OperationLogEvent build() {
            return event;
        }
    }
}
