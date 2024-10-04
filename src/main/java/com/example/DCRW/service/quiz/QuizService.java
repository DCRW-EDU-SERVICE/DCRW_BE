package com.example.DCRW.service.quiz;

import com.example.DCRW.dto.quiz.QuizResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface QuizService {
    Mono<List<QuizResponseDto>> generateQuiz(int subjectId);
}
