package com.example.DCRW.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영문 소문자와 숫자 4~20자리여야 합니다.")
    private String userId;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$", message = "비밀번호 8~16자리수. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함")
    private String password;

    @NotBlank(message = "이름는 필수 입력값입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "이름은 특수문자를 포함하지 않은 2~10자리")
    private String name;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;

    @JsonProperty("birthdate") // JSON 속성명과 Java 객체 필드명 매핑
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "생일은 필수 입력값입니다.")
    private LocalDate birthDate;

    private String teacherCode;
    private LocalDate joinDate;

//    private String provider, providerId; // 구글 OAuth는 나중에...

    @Builder
    public UserDto(String userId, String password, String name, String address, LocalDate birthDate) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.address = address;
        this.birthDate = birthDate;
    }

}
