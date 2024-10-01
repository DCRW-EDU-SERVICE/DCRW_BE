package com.example.DCRW.service.quiz;

import com.example.DCRW.dto.quiz.GPTRequestDto;
import com.example.DCRW.dto.quiz.QuizResponseDto;
import com.example.DCRW.entity.Subject;
import com.example.DCRW.repository.SubjectRepository;
import com.example.DCRW.service.quiz.QuizService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final WebClient webClient;
    private final SubjectRepository subjectRepository;

    // OpenAI API URL과 모델 정의
    private String model = "gpt-3.5-turbo";

    @Override
    public List<QuizResponseDto> generateQuiz(int subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new IllegalArgumentException("잘못된 요청"));

        // 퀴즈 정보 프롬프트 생성
        String quizInfo = "Create 10 simple quiz questions about " + subject.getName() + " for children from multicultural families. " +
                "Each question should include a question, 4 options, the correct answer, and an explanation. " +
                "Return the result in the following JSON format: { \"question\": \"\", \"option\": [\"\", \"\", \"\", \"\"], \"answer\": \"\", \"explanation\": \"\" }.";

        String combinedPrompt = quizInfo; // 필요시 기존 프롬프트와 조합

        // GPTRequestDto 객체 생성
        GPTRequestDto request = new GPTRequestDto(model, combinedPrompt);

        // WebClient를 이용한 API 요청
        String gptResponse = this.webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 비동기 응답을 동기로 대기

        // GPT 응답을 파싱
        List<QuizResponseDto> quizList = parseGptResponse(gptResponse);
        return quizList;
    }

    // GPT의 응답을 QuizResponseDto로 변환하는 메서드
    private List<QuizResponseDto> parseGptResponse(String gptResponse) {
        // JSON 응답을 QuizResponseDto 리스트로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<QuizResponseDto> quizList = new ArrayList<>();

        try {
            // GPT의 응답을 QuizResponseDto 배열로 파싱
            QuizResponseDto[] quizArray = objectMapper.readValue(gptResponse, QuizResponseDto[].class);
            quizList = Arrays.asList(quizArray);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return quizList;
    }
}
