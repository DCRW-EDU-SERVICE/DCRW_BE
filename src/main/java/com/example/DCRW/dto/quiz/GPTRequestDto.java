package com.example.DCRW.dto.quiz;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GPTRequestDto {
    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;
    private int top_p;
    private int frequency_penalty;
    private int presence_penalty;

    public GPTRequestDto(String model, String prompt) {
        this.model = model; //gpt-3.5-turbo 모델 사용
        this.messages =  new ArrayList<>();
        this.messages.add(new Message("system", "You are a helpful assistant.")); // system 메시지 추가
        this.messages.add(new Message("user", prompt)); //user 메시지 추가
        this.temperature = 0.7;//답변의 창의성과 무작위성을 조정하는 값, 낮을수록 사실에 근거한 답변 제공, 높을수록 창의적인 결과물 생성
        this.max_tokens = 4096; // 최대 토큰
        this.top_p = 1;//모든 단어를 고려 - 답변의 무작위성을 제어하는 조정값
        this.frequency_penalty = 0; // 값이 높을수록 AI가 흔하지않은 단어를 답변에 포함할 가능성을 제어
        this.presence_penalty = 0; // 값이 높을수록 AI가 유사하거나 동일한 단어를 반복할 가능성을 제어

    }
}
