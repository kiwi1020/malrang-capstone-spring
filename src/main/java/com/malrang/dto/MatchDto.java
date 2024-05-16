package com.malrang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

public class MatchDto {
    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class MatchRequest {
        private String sessionId;
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MatchRequest)) {
                return false;
            }

            MatchRequest that = (MatchRequest) o;
            return Objects.equals(this.sessionId, that.sessionId);
        }

        @Override
        public int hashCode() {
            return sessionId.hashCode();
        }

        @Override
        public String toString() {
            return "ChatRequest{" + "sessionId='" + sessionId + '\'' + '}';
        }
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class MatchResponse {
        private ResponseResult responseResult;
        private String sessionId;

        private String roomId;

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public void setResponseResult(ResponseResult responseResult) {
            this.responseResult = responseResult;
        }

        @Override
        public String toString() {
            return "ChatResponse{" + "responseResult=" + responseResult + ", sessionId='" + sessionId + '\'' + '}';
        }

        public enum ResponseResult {
            SUCCESS, CANCEL, TIMEOUT;
        }
    }

}
