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


    public OperationLogEvent() {
    }

    public enum Status {
        SUCCEED,
        FAILED;

        public static Status of(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final OperationLogEvent event = new OperationLogEvent();

        public Builder module(String module) {
            event.module = module;
            return this;
        }

        public Builder action(String action) {
            event.action = action;
            return this;
        }

        public Builder targetId(Long targetId) {
            event.targetId = targetId;
            return this;
        }

        public Builder params(Map<String, Object> params) {
            event.params = params;
            return this;
        }

        public Builder response(String response) {
            event.response = response;
            return this;
        }

        public Builder status(Status status) {
            event.status = status;
            return this;
        }

        public Builder duration(long duration) {
            event.duration = duration;
            return this;
        }

        public Builder message(String message) {
            event.message = message;
            return this;
        }

        public OperationLogEvent build() {
            return event;
        }
    }
}
