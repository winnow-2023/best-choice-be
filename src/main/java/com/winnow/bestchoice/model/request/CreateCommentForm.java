package com.winnow.bestchoice.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class CreateCommentForm {

    @NotBlank
    private String content;
}
