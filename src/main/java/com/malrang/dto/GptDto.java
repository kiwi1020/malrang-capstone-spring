package com.malrang.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

public class GptDto {
    @Data
    @NoArgsConstructor
    public static class ChatGptMessage implements Serializable {
        private String role;
        private String content;

        @Builder
        public ChatGptMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Getter
    @NoArgsConstructor
//chatGPT에 요청할 DTO Format
    public static class ChatGptRequest implements Serializable {
        private String model;
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        private Double temperature;
        private Boolean stream;
        private List<ChatGptMessage> messages;

        //@JsonProperty("top_p")
        //private Double topP;

        @Builder
        public ChatGptRequest(String model, Integer maxTokens, Double temperature,
                              Boolean stream, List<ChatGptMessage> messages
                /*,Double topP*/) {
            this.model = model;
            this.maxTokens = maxTokens;
            this.temperature = temperature;
            this.stream = stream;
            this.messages = messages;
            //this.topP = topP;
        }

    }

    @Getter
    @NoArgsConstructor
//ChatGPT 답변을 담을 DTO
    public static class ChatGptResponse {
        private String id;
        private String object;
        private long created;
        private String model;
        private ChatGptResponse.Usage usage;
        private List<ChatGptResponse.Choice> choices;

        @Getter
        @Setter
        public static class Usage {
            @JsonProperty("prompt_tokens")
            private int promptTokens;
            @JsonProperty("completion_tokens")
            private int completionTokens;
            @JsonProperty("total_tokens")
            private int totalTokens;
        }

        @Getter
        @Setter
        public static class Choice {
            private ChatGptMessage message;
            @JsonProperty("finish_reason")
            private String finishReason;
            private int index;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
//Front단에서 요청하는 DTO
    public static class QuestionRequest implements Serializable {
        private String question;
    }
}
