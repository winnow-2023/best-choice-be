package com.winnow.bestchoice.model.request;

import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter @Setter
@Builder
public class CreatePostForm {

    @NotBlank
    private String title;
    private String content;
    @NotBlank
    private String optionA;
    @NotBlank
    private String optionB;
    @Size(max = 5)
    private List<String> tags;

    public Post toEntity(Member member) {
        if (ObjectUtils.isEmpty(this.content)) {
            this.content = "";
        }
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
