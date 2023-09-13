package com.winnow.bestchoice.service;

import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Notification;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.CreatingRoomNotificationData;
import com.winnow.bestchoice.model.response.NotificationRes;
import com.winnow.bestchoice.repository.EmitterRepository;
import com.winnow.bestchoice.repository.NotificationRepository;
import com.winnow.bestchoice.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long TIMEOUT = 60L * 1000 * 60;
    private final PostQueryRepository postQueryRepository;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final String EVENT_CONNECT = "connect";
    private final String EVENT_NOTIFICATION = "notification";

    public SseEmitter subscribe(long userId) {
        SseEmitter emitter = createEmitter(String.valueOf(userId));

        sendToClient(userId, EVENT_CONNECT, "EventStream Created. [userId=" + userId + "]");
        return emitter;
    }

    @Async("notificationExecutor")
    public void notifyCreatingRoomByPost(Post post) {
        HashSet<Long> memberIds = getRelatedMemberIds(post);
        CreatingRoomNotificationData data = CreatingRoomNotificationData.of(post);
        List<Notification> notifications = new ArrayList<>();

        for (Long memberId : memberIds) {
            notifications.add(Notification.builder().post(post)
                    .member(new Member(memberId))
                    .postTitle(post.getTitle()).build());
            notify(memberId, data);
        }
        notificationRepository.saveAll(notifications);
    }

    private HashSet<Long> getRelatedMemberIds(Post post) {
        long postId = post.getId();
        List<Long> memberIdByLike = postQueryRepository.findMemberIdRelatedWithLikeByPostId(postId);
        List<Long> memberIdByChoice = postQueryRepository.findMemberIdRelatedWithChoiceByPostId(postId);
        List<Long> memberIdByComment = postQueryRepository.findMemberIdRelatedWithCommentByPostId(postId);

        HashSet<Long> memberIds = new HashSet<>();
        memberIds.addAll(memberIdByLike);
        memberIds.addAll(memberIdByChoice);
        memberIds.addAll(memberIdByComment);
        memberIds.remove(post.getMember().getId()); //게시글 작성자는 제외

        return memberIds;
    }

    private void sendToClient(long id, String eventName, Object data) {
        SseEmitter emitter = emitterRepository.get(String.valueOf(id));
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException exception) {
                emitterRepository.deleteById(String.valueOf(id));
                emitter.completeWithError(exception);
            }
        }
    }

    private void notify(long userId, Object event) {
        sendToClient(userId, EVENT_NOTIFICATION, event);
    }

    private SseEmitter createEmitter(String id) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.save(id, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        return emitter;
    }

    public Slice<NotificationRes> getNotifications(long memberId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return notificationRepository.findByMember_IdOrderByCreatedDateDesc(memberId, pageRequest)
                .map(NotificationRes::of);
    }

    public void deleteNotification(long memberId, long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (memberId != notification.getMember().getId()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteAllNotifications(long memberId) {
        notificationRepository.deleteAllByMemberId(memberId);
    }
}
