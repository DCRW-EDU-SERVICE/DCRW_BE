package com.example.DCRW.service.quiz;

import com.example.DCRW.dto.quiz.QuizResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuizService {
    List<QuizResponseDto> generateQuiz(int subjectId);
}
