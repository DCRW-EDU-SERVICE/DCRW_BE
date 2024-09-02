package com.example.DCRW.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@Builder
public class UserUpdateDto {
    // Builder 사용하면서 초기화 값이 있으면 Lombok은 그 값을 무시함. 그래서 Default 쓰면 기본값 유지 가능
    @Builder.Default
    private Optional<String> userName = Optional.empty();
    @Builder.Default
    private Optional<LocalDate> birthDate = Optional.empty();
    @Builder.Default
    private Optional<String> address = Optional.empty();
    @Builder.Default
    private Optional<String> image = Optional.empty();


}
