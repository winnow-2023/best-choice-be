package com.winnow.bestchoice.model.request;

import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter @Setter
public class CreatePostForm {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String optionA;
    @NotBlank
    private String optionB;
    @Size(max = 5)
    private List<String> tags;

    public Post toEntity(Member member) {
        return Post.builder()
                .member(member)
                .title(this.title)
                .content(this.content)
                .optionA(this.optionA)
                .optionB(this.optionB)
                .tags(this.tags)
                .build();
    }
}
