package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.quiz.QuizResponseDto;
import com.example.DCRW.service.quiz.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/quiz")
    public ResponseEntity<ResultDto<String>> generateQuiz(@RequestParam("subject") int subjectId){
        List<QuizResponseDto> quizList = quizService.generateQuiz(subjectId);

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("퀴즈 생성 성공")
                .data(quizList)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }
}
