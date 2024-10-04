package com.example.DCRW.service.quiz;

import com.example.DCRW.dto.quiz.GPTRequestDto;
import com.example.DCRW.dto.quiz.QuizResponseDto;
import com.example.DCRW.entity.Subject;
import com.example.DCRW.repository.SubjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final SubjectRepository subjectRepository;

    // OpenAI API URL과 모델 정의
    private String model = "gpt-3.5-turbo";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public QuizServiceImpl(SubjectRepository subjectRepository, WebClient webClient) {
        this.subjectRepository = subjectRepository;
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Jackson이 알 수 없는 필드 무시

    }

    @Override
    public Mono<List<QuizResponseDto>> generateQuiz(int subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new IllegalArgumentException("잘못된 요청"));

        // 퀴즈 프롬프트 생성
        String quizInfo = "5살에서 13살 다문화 가정 아이들을 위한 간단한 퀴즈를 10개 만들어줘. " +
                "퀴즈 주제는 '" + subject.getName() + "'이고, 언어는 한국어로 해야 해. " +
                "각 퀴즈는 1개의 질문과 4개의 선택지, 그리고 정답과 풀이가 포함되어야 해. " +
                "응답은 아래와 같은 JSON 배열 형식으로 10개 보내줘. \n" +
                "[\n" +
                "  {\n" +
                "    \"question\": \"첫 번째 질문\",\n" +
                "    \"option\": [\"옵션0\", \"옵션1\", \"옵션2\", \"옵션3\"],\n" +
                "    \"answer\": \"option에서 정답인 index(0~3)\",\n" +
                "    \"explanation\": \"설명\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"question\": \"두 번째 질문\",\n" +
                "    \"option\": [\"옵션0\", \"옵션1\", \"옵션2\", \"옵션3\"],\n" +
                "    \"answer\": \"option에서 정답인 index(0~3)\",\n" +
                "    \"explanation\": \"설명\"\n" +
                "  }\n" +
                "  // 이렇게 8개 더 추가\n" +
                "]\n";


        String combinedPrompt = quizInfo; // 필요시 기존 프롬프트와 조합

        // GPTRequestDto 객체 생성
        GPTRequestDto request = new GPTRequestDto(model, combinedPrompt);

        // WebClient를 이용한 API 요청
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(gptResponse -> {
                    // GPT 응답을 파싱
                    List<QuizResponseDto> quizList = parseGptResponse(gptResponse);
                    return Mono.just(quizList); // 비동기식으로 quizList 반환
                });
    }

    // GPT의 응답을 QuizResponseDto로 변환하는 메서드
    private List<QuizResponseDto> parseGptResponse(String gptResponse) {
        List<QuizResponseDto> quizList = new ArrayList<>();

        try {
            // JSON 응답 파싱
            JsonNode root = objectMapper.readTree(gptResponse);
            JsonNode choices = root.path("choices");

            // choices 배열의 첫 번째 항목에서 content를 가져옴
            String content = choices.get(0).path("message").path("content").asText();

            // content를 QuizResponseDto 배열로 변환
            QuizResponseDto[] quizArray = objectMapper.readValue(content, QuizResponseDto[].class);
            quizList = List.of(quizArray);
        } catch (Exception e) {
            e.printStackTrace();  // 오류 처리
        }

        return quizList;
    }

}
