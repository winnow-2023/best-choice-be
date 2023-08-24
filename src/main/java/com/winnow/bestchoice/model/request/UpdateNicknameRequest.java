package com.winnow.bestchoice.model.request;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class UpdateNicknameRequest {
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]+$", message = "닉네임은 2-20자 사이로 한글, 영어, 숫자만 가능하며 중간 공백은 허용되지 않습니다.")
    @Size(min = 2, max = 20)
    private String nickname;
}
