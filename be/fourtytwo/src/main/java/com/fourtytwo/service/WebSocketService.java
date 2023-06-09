package com.fourtytwo.service;

import com.fourtytwo.dto.socket.MessageDto;
import com.fourtytwo.dto.socket.MethodType;
import com.fourtytwo.dto.socket.SessionInfoDto;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Log
@Service
@RequiredArgsConstructor
public class WebSocketService extends TextWebSocketHandler {

    private static final Map<WebSocketSession, Long> userSession = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, WebSocketSession> userSessionMap = Collections.synchronizedMap(new HashMap<>());
    private static final Set<WebSocketSession> guestSession = Collections.synchronizedSet(new HashSet<>());
    private static final Map<WebSocketSession, List<Double>> locations = Collections.synchronizedMap(new HashMap<>());
    private static final ConcurrentSkipListMap<Double, Set<WebSocketSession>> userLatitudes = new ConcurrentSkipListMap<>();
    private static final ConcurrentSkipListMap<Double, Set<WebSocketSession>> userLongitudes = new ConcurrentSkipListMap<>();
    private static final Set<WebSocketSession> noResponseSession = Collections.synchronizedSet(new HashSet<>());

    private final Gson gson = new Gson();
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = session.getUri().toString(); // 연결된 URL에서 WebSocket 경로를 가져옵니다.
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri).build();
        String type = uriComponents.getQueryParams().getFirst("type");
        if (type == null) {
            throw new IllegalArgumentException("Missing 'type' parameter in WebSocket URL.");
        }

        if (type.equals("user")) {
            String userIdxStr = uriComponents.getQueryParams().getFirst("user_idx");
            if (userIdxStr == null) {
                throw new IllegalArgumentException("Missing 'user_idx' parameter in WebSocket URL.");
            }
            Long userIdx = Long.valueOf(userIdxStr);
            User user = userRepository.findByIdAndIsActiveTrue(userIdx);

            if (user == null) {
                throw new EntityNotFoundException("존재하지 않는 유저입니다.");
            }

            userSession.put(session, userIdx);
            userSessionMap.put(userIdx, session);
            session.getAttributes().put("userIdx", userIdx);
            session.getAttributes().put("nickname", user.getNickname());
            Message message = messageRepository.findRecentByUserIdx(userIdx);
            if (message != null) {
                session.getAttributes().put("message", message.getContent());
            } else {
                session.getAttributes().put("message", "");
            }
            session.getAttributes().put("emoji", user.getEmoji());
        } else {
            guestSession.add(session);
        }
        session.getAttributes().put("type", type);
        session.getAttributes().put("nearUsers", new HashSet<WebSocketSession>());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        handleClosedSessions(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지를 JSON으로 변환합니다.

        Gson gson = new Gson();
        Map<String, Object> info = gson.fromJson(message.getPayload(), new TypeToken<Map<String, Object>>() {
        }.getType());

        // 리플렉션을 사용하지 않고 메서드 처리
        String method = (String) info.remove("method");
        if (method == null) {
            session.sendMessage(new TextMessage("메세지 형식을 지켜주세요."));
            return;
        }
        if (method.equals(MethodType.INIT.name())) {
            METHOD_INIT(session, info);
        } else if (method.equals(MethodType.MOVE.name())) {
            METHOD_MOVE(session, info);
        } else if (method.equals(MethodType.CHANGE_STATUS.name())) {
            METHOD_CHANGE_STATUS(session, info);
        } else if (method.equals(MethodType.MESSAGE_CHANGED.name())) {
            METHOD_MESSAGE_CHANGED(session, info);
        } else if (method.equals(MethodType.CLOSE.name())) {
            METHOD_CLOSE(session, info);
        } else if (method.equals(MethodType.PONG.name())) {
            METHOD_PONG(session, info);
        }
        {
            // 다른 메소드 처리를 여기에 추가합니다.
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
        log.warning("onError:" + exception.getMessage());
    }

    private void handleClosedSessions(WebSocketSession session) throws Exception {

        if (!locations.containsKey(session) || locations.get(session).isEmpty()) {
            return;
        }

        Double userLatitude = locations.get(session).get(0);
        Double userLongitude = locations.get(session).get(1);
        Set<WebSocketSession> nearUsers = (Set<WebSocketSession>) session.getAttributes().get("nearUsers");
        if (!nearUsers.isEmpty()) {
            for (WebSocketSession otherUserSession : nearUsers) {
                if (otherUserSession.isOpen()) {
                    otherUserSession.sendMessage(new TextMessage(gson.toJson(createMessage(otherUserSession, session, MethodType.CLOSE))));
                    ((Set<WebSocketSession>) otherUserSession.getAttributes().get("nearUsers")).remove(session);
                }
            }
        }
        userLatitudes.get(userLatitude).remove(session);
        if (userLatitudes.get(userLatitude).isEmpty()) {
            userLatitudes.remove(userLatitude);
        }
        userLongitudes.get(userLongitude).remove(session);
        if (userLongitudes.get(userLongitude).isEmpty()) {
            userLongitudes.remove(userLongitude);
        }
        locations.remove(session);
        noResponseSession.remove(session);
        if ("user".equals(session.getAttributes().get("type"))) {
            userSession.remove(session);
        } else {
            guestSession.remove(session);
        }
    }

    public MessageDto createMessage(WebSocketSession recievingSession, WebSocketSession sendingSession, MethodType type) {
        MessageDto message = new MessageDto(type);
        Map<String, Object> messageData = new HashMap<>();
        if (sendingSession != null) {
            messageData.put("type", sendingSession.getAttributes().get("type"));
            if (sendingSession.getAttributes().get("type").equals("user")) {
                messageData.put("userIdx", sendingSession.getAttributes().get("userIdx"));
                messageData.put("message", sendingSession.getAttributes().get("message"));
                messageData.put("emoji", sendingSession.getAttributes().get("emoji"));
                messageData.put("nickname", sendingSession.getAttributes().get("nickname"));
                messageData.put("latitude", sendingSession.getAttributes().get("latitude"));
                messageData.put("longitude", sendingSession.getAttributes().get("longitude"));
                messageData.put("status", sendingSession.getAttributes().get("status"));
            }
            else {
                messageData.put("latitude", sendingSession.getAttributes().get("latitude"));
                messageData.put("longitude", sendingSession.getAttributes().get("longitude"));
                messageData.put("status", sendingSession.getAttributes().get("status"));
            }
        }
        if (type.equals(MethodType.INFO)){
//            List<SessionInfoDto> nearUsers = new ArrayList<>();
            messageData.put("type", recievingSession.getAttributes().get("type"));
            if (recievingSession.getAttributes().get("type").equals("user")) {
                messageData.put("userIdx", recievingSession.getAttributes().get("userIdx"));
                messageData.put("message", recievingSession.getAttributes().get("message"));
                messageData.put("emoji", recievingSession.getAttributes().get("emoji"));
                messageData.put("nickname", recievingSession.getAttributes().get("nickname"));
                messageData.put("latitude", recievingSession.getAttributes().get("latitude"));
                messageData.put("longitude", recievingSession.getAttributes().get("longitude"));
                messageData.put("status", recievingSession.getAttributes().get("status"));
            }
            else {
                messageData.put("latitude", recievingSession.getAttributes().get("latitude"));
                messageData.put("longitude", recievingSession.getAttributes().get("longitude"));
                messageData.put("status", recievingSession.getAttributes().get("status"));
            }
//            for (WebSocketSession targetSession : (Set<WebSocketSession>) recievingSession.getAttributes().get("nearUsers")) {
//                if (targetSession.getAttributes().get("type").equals("user")) {
//                    SessionInfoDto sessionInfoDto = SessionInfoDto.builder()
//                            .type((String) targetSession.getAttributes().get("type"))
//                            .userIdx((Long) targetSession.getAttributes().get("userIdx"))
//                            .latitude((Double) targetSession.getAttributes().get("latitude"))
//                            .longitude((Double) targetSession.getAttributes().get("longitude"))
//                            .nickname((String) targetSession.getAttributes().get("nickname"))
//                            .message((String) targetSession.getAttributes().get("message"))
//                            .emoji((String) targetSession.getAttributes().get("emoji"))
//                            .status((String) targetSession.getAttributes().get("status"))
//                            .build();
//                    nearUsers.add(sessionInfoDto);
//                } else {
//                    SessionInfoDto sessionInfoDto = SessionInfoDto.builder()
//                            .type((String) targetSession.getAttributes().get("type"))
//                            .latitude((Double) targetSession.getAttributes().get("latitude"))
//                            .longitude((Double) targetSession.getAttributes().get("longitude"))
//                            .status((String) targetSession.getAttributes().get("status"))
//                            .build();
//                    nearUsers.add(sessionInfoDto);
//                }
//            }
//            messageData.put("nearUsers", nearUsers);
        }
        message.setData(messageData);
        return message;
    }


    public Set<WebSocketSession> changeLocation(WebSocketSession session) throws Exception {


        List<Double> location = Arrays.asList((Double) session.getAttributes().get("latitude"), (Double) session.getAttributes().get("longitude"));
        Set<WebSocketSession> nearUsers = getNearUsers(location);
        Set<WebSocketSession> farUsers = new HashSet<>((Set<WebSocketSession>) session.getAttributes().get("nearUsers"));
        Set<WebSocketSession> intersection = new HashSet<>();
        for (WebSocketSession nearSession : nearUsers) {
            if (farUsers.contains(nearSession)) {
                intersection.add(nearSession);
            }
        }

        locations.put(session, location);
        renewGps(session, location);
        if (!nearUsers.isEmpty()) {
            for (WebSocketSession targetSession : nearUsers) {
                if (targetSession.equals(session)) {
                    continue;
                }
//                if (targetSession.getAttributes().get("type").equals("user")) {
//                    if (targetSession.getAttributes().get("userIdx").equals(session.getAttributes().get("userIdx"))) {continue;}
//                }
                ((Set<WebSocketSession>) targetSession.getAttributes().get("nearUsers")).add(session);
                ((Set<WebSocketSession>) session.getAttributes().get("nearUsers")).add(targetSession);

//                if (targetSession.isOpen()) {
//                    String targetMessage = gson.toJson(createMessage(targetSession, session, MethodType.NEAR));
//                    try {
//                        targetSession.sendMessage(new TextMessage(targetMessage));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    handleClosedSessions(targetSession);
//                }
            }
        }
        nearUsers.removeAll(intersection);
        farUsers.removeAll(intersection);
        MessageDto messageDto = createMessage(session, null, MethodType.INFO);
        messageDto.getData().put("nearUsers", new ArrayList<>());
        messageDto.getData().put("farUsers", new ArrayList<>());

        if (!farUsers.isEmpty()) {
            for (WebSocketSession targetSession : farUsers) {
                ((Set<WebSocketSession>) targetSession.getAttributes().get("nearUsers")).remove(session);
                ((Set<WebSocketSession>) session.getAttributes().get("nearUsers")).remove(targetSession);
                if (targetSession.isOpen()) {
                    String targetMessage = gson.toJson(createMessage(targetSession, session, MethodType.CLOSE));
                    ((ArrayList) messageDto.getData().get("farUsers")).add(SessionInfoDto.builder()
                            .type((String) targetSession.getAttributes().get("type"))
                            .userIdx((Long) targetSession.getAttributes().get("userIdx"))
                            .latitude((Double) targetSession.getAttributes().get("latitude"))
                            .longitude((Double) targetSession.getAttributes().get("longitude"))
                            .nickname((String) targetSession.getAttributes().get("nickname"))
                            .message((String) targetSession.getAttributes().get("message"))
                            .emoji((String) targetSession.getAttributes().get("emoji"))
                            .status((String) targetSession.getAttributes().get("status"))
                            .build());
                    try {
                        targetSession.sendMessage(new TextMessage(targetMessage));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    handleClosedSessions(targetSession);
                }
            }
        }
        if (!nearUsers.isEmpty()) {
            for (WebSocketSession targetSession : nearUsers) {
                if (targetSession.equals(session)) {
                    continue;
                }
                if (targetSession.isOpen()) {
                    String targetMessage = gson.toJson(createMessage(targetSession, session, MethodType.NEAR));
                    ((ArrayList) messageDto.getData().get("nearUsers")).add(SessionInfoDto.builder()
                            .type((String) targetSession.getAttributes().get("type"))
                            .userIdx((Long) targetSession.getAttributes().get("userIdx"))
                            .latitude((Double) targetSession.getAttributes().get("latitude"))
                            .longitude((Double) targetSession.getAttributes().get("longitude"))
                            .nickname((String) targetSession.getAttributes().get("nickname"))
                            .message((String) targetSession.getAttributes().get("message"))
                            .emoji((String) targetSession.getAttributes().get("emoji"))
                            .status((String) targetSession.getAttributes().get("status"))
                            .build());
                    try {
                        targetSession.sendMessage(new TextMessage(targetMessage));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    handleClosedSessions(targetSession);
                }
            }
        }

        String message = gson.toJson(messageDto);
        session.sendMessage(new TextMessage(message));

        return nearUsers;
    }

    public void renewGps(WebSocketSession session, List<Double> location) {
        if (userLatitudes.containsKey(location.get(0))) {
            userLatitudes.get(location.get(0)).add(session);
        } else {
            userLatitudes.put(location.get(0), new HashSet<>(Collections.singletonList(session)));
        }

        if (userLongitudes.containsKey(location.get(1))) {
            userLongitudes.get(location.get(1)).add(session);
        } else {
            userLongitudes.put(location.get(1), new HashSet<>(Collections.singletonList(session)));
        }
    }

    public Set<WebSocketSession> getNearUsers(List<Double> location) {
        Set<WebSocketSession> nearUsers = new HashSet<>();
        Set<WebSocketSession> nearLongUsers = new HashSet<>();
        ConcurrentNavigableMap<Double, Set<WebSocketSession>> nearLats = userLatitudes.subMap(location.get(0)-0.0005, true, location.get(0)+0.0005, true);
        nearLats.forEach((k, v) -> nearUsers.addAll(v));
        ConcurrentNavigableMap<Double, Set<WebSocketSession>> nearLongs = userLongitudes.subMap(location.get(1)-0.0005, true, location.get(1)+0.0005, true);
        nearLongs.forEach((k, v) -> nearLongUsers.addAll(v));
        nearUsers.retainAll(nearLongUsers);
        Set<WebSocketSession> nearUserInfos = new HashSet<>();
        if (!nearUsers.isEmpty()) {
            nearUserInfos.addAll(nearUsers);
        }
        return nearUserInfos;
    }

    public void sendMessagesToNearUsers(WebSocketSession session, MethodType methodType) throws Exception {
        Set<WebSocketSession> nearUsers = new HashSet<>((Set<WebSocketSession>) session.getAttributes().get("nearUsers"));
        if (!nearUsers.isEmpty()) {
            for (WebSocketSession targetSession : nearUsers) {
                String message = gson.toJson(createMessage(targetSession, session, methodType));
                if (targetSession.isOpen()) {
                    targetSession.sendMessage(new TextMessage(message));
                } else {
                    handleClosedSessions(targetSession);
                }
            }
        }
    }

    public void METHOD_INIT(WebSocketSession session, Map<String, Object> info) throws Exception {
        for (String key : info.keySet()) {
            session.getAttributes().put(key, info.get(key));
        }

        changeLocation(session);
    }

    public void METHOD_MOVE(WebSocketSession session, Map<String, Object> info) throws Exception {
        for (String key : info.keySet()) {
            session.getAttributes().put(key, info.get(key));
        }

        changeLocation(session);
    }

    public void METHOD_CHANGE_STATUS(WebSocketSession session, Map<String, Object> info) throws Exception {
        for (String key : info.keySet()) {
            session.getAttributes().put(key, info.get(key));
        }

        sendMessagesToNearUsers(session, MethodType.CHANGE_STATUS);
    }

    public void METHOD_CLOSE(WebSocketSession session, Map<String, Object> info) throws IOException {
        session.close();
    }

    public void METHOD_MESSAGE_CHANGED(WebSocketSession session, Map<String, Object> info) throws Exception {
        for (String key : info.keySet()) {
            session.getAttributes().put(key, info.get(key));
        }
        Long userIdx = (Long) session.getAttributes().get("userIdx");
        Message newMessage = messageRepository.findRecentByUserIdx(userIdx);
        if (newMessage != null) {
            session.getAttributes().put("message", newMessage.getContent());
        } else {
            session.getAttributes().put("message", null);
        }

        sendMessagesToNearUsers(session, MethodType.MESSAGE_CHANGED);
    }

    public void METHOD_PONG(WebSocketSession session, Map<String, Object> info) {
        if (!noResponseSession.isEmpty()) {
            noResponseSession.remove(session);
        }
    }

    @Scheduled(fixedRate = 100000)
    public void customTimeOut() throws Exception {

        Set<WebSocketSession> copiedNoResponseSession = new HashSet<>(noResponseSession);

        if (!noResponseSession.isEmpty()) {
            for (WebSocketSession session : copiedNoResponseSession) {
                if (session.isOpen()) {
                    session.close();
                } else { handleClosedSessions(session); }
            }
        }
        noResponseSession.clear();
        if (!guestSession.isEmpty()) {
            for (WebSocketSession session : guestSession) {
                if (session.getAttributes().containsKey("status")) {
                    noResponseSession.add(session);
                }
            }
        }
        if (!userSession.isEmpty()) {
            for (WebSocketSession session : userSession.keySet()) {
                if (session.getAttributes().containsKey("status")) {
                    noResponseSession.add(session);
                }
            }
        }
    }

    @Scheduled(fixedRate = 20000)
    public void sendPING() throws Exception {
        if (!guestSession.isEmpty()) {
            for (WebSocketSession session : guestSession) {
                if (session.isOpen()) {
                    Map<String, Object> checkMessage = new HashMap<>();
                    checkMessage.put("method", "PING");
                    session.sendMessage(new TextMessage(gson.toJson(checkMessage)));
                } else { handleClosedSessions(session); }
            }
        }
        if (!userSession.isEmpty()) {
            for (WebSocketSession session : userSession.keySet()) {
                if (session.isOpen()) {
                    Map<String, Object> checkMessage = new HashMap<>();
                    checkMessage.put("method", "PING");
                    session.sendMessage(new TextMessage(gson.toJson(checkMessage)));
                } else { handleClosedSessions(session); }
            }
        }
    }

}
