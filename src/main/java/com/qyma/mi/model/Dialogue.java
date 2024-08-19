package com.qyma.mi.model;

import lombok.Data;

import java.util.List;


@lombok.Data
public class Dialogue {
    private List<Answer> answers;
    private long time;
    private String query;
    private String requestId;

    @Data
    public static class Answer {
        private String type;
        private TTS tts;
        @Data
        public static class TTS {
            private String text;
        }
    }

    public String getAnswerText(){
        if (answers == null || answers.size() == 0){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        answers.stream().forEach(answer -> {sb.append(answer.tts.text);});
        return sb.toString();
    }

    public boolean isNewDialogue(long time){
        return this.time > time;
    }

    public boolean isCommand(){
        return false;
    }
}

