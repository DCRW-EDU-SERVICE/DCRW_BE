package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.quiz.QuizResponseDto;
import com.example.DCRW.service.quiz.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/quiz/{subject}")
    public Mono<ResponseEntity<ResultDto<List<QuizResponseDto>>>> generateQuiz(@PathVariable("subject") int subjectId){
        return quizService.generateQuiz(subjectId)
                .map(quizList -> {
                    ResultDto<List<QuizResponseDto>> resultDto = ResultDto.<List<QuizResponseDto>>builder()
                            .status(HttpStatus.OK)
                            .message("퀴즈 생성 성공")
                            .data(quizList)
                            .build();
                    return ResponseEntity.ok(resultDto);
                });
    }
}
