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

/**
 * Operation log
 *
 * @author wq li
 */
public class OperationLogEvent {

    private String module;

    private String action;

    private String params;

    private String result;

    private int status;

    private long duration;

    private String message;


    public OperationLogEvent() {
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

        public Builder params(String params) {
            event.params = params;
            return this;
        }

        public Builder result(String result) {
            event.result = result;
            return this;
        }

        public Builder status(int status) {
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
