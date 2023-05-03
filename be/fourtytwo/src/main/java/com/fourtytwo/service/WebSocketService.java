package com.fourtytwo.service;

import com.fourtytwo.config.WebSocketConfig;
import com.fourtytwo.dto.socket.MessageDto;
import com.fourtytwo.dto.socket.MethodType;
import com.fourtytwo.entity.Message;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
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
    private static final Map<Long, List<Double>> locations = Collections.synchronizedMap(new HashMap<>());
    private static final ConcurrentSkipListMap<Double, Set<Long>> userLatitudes = new ConcurrentSkipListMap<>();
    private static final ConcurrentSkipListMap<Double, Set<Long>> userLongitudes = new ConcurrentSkipListMap<>();


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
            userSession.put(session, userIdx);
            userSessionMap.put(userIdx, session);
            session.getAttributes().put("type", type);
            session.getAttributes().put("idx", userIdx);
        } else {
            guestSession.add(session);
            session.getAttributes().put("type", type);
        }
        session.getAttributes().put("nearUsers", new HashSet<WebSocketSession>());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if ("user".equals(session.getAttributes().get("type"))) {
            Long userIdx = userSession.get(session);
            Double userLatitude = locations.get(userIdx).get(0);
            Double userLongitude = locations.get(userIdx).get(1);
            Set<WebSocketSession> nearUsers = (Set<WebSocketSession>) session.getAttributes().get("nearUsers");
            if (!nearUsers.isEmpty()) {
                for (WebSocketSession otherUserSession : nearUsers) {
                    ((Set<WebSocketSession>) otherUserSession.getAttributes().get("nearUsers")).remove(session);
                }
            }
            userLatitudes.get(userLatitude).remove(userIdx);
            if (userLatitudes.get(userLatitude).isEmpty()) {
                userLatitudes.remove(userLatitude);
            }
            userLongitudes.get(userLongitude).remove(userIdx);
            if (userLongitudes.get(userLongitude).isEmpty()) {
                userLongitudes.remove(userLongitude);
            }
            locations.remove(userIdx);
            userSession.remove(session);
        } else {
            guestSession.remove(session);
        }
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
        } else {
            // 다른 메소드 처리를 여기에 추가합니다.
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
        log.warning("onError:" + exception.getMessage());
    }


    public Set<WebSocketSession> changeLocation(WebSocketSession session) {
        MessageDto messageDto = new MessageDto(MethodType.NEAR);
        messageDto.setData(session.getAttributes());
        Gson gson = new Gson();
        String near_message = gson.toJson(messageDto);
        MessageDto far_messageDto = new MessageDto(MethodType.REMOVE);
        messageDto.setData(session.getAttributes());
        String far_message = gson.toJson(messageDto);

        List<Double> location = Arrays.asList((Double) session.getAttributes().get("latitude"), (Double) session.getAttributes().get("longitude"));
        Set<WebSocketSession> nearUsers = getNearUsers(location);
        if (session.getAttributes().get("type").equals("user")) {
            Set<WebSocketSession> farUsers = (Set<WebSocketSession>) session.getAttributes().get("nearUsers");
            Long userIdx = userSession.get(session);
            locations.put(userIdx, location);
            renewGps(userIdx, location);
            nearUsers.removeAll(farUsers);
            System.out.println(nearUsers);
            System.out.println(farUsers);
            if (!nearUsers.isEmpty()) {
                for (WebSocketSession targetSession : nearUsers) {
                    if (targetSession.equals(session)) {continue;}
                    ((Set<WebSocketSession>) targetSession.getAttributes().get("nearUsers")).add(session);
                    ((Set<WebSocketSession>) session.getAttributes().get("nearUsers")).add(targetSession);
                    if (targetSession.isOpen()) {
                        try {
                            targetSession.sendMessage(new TextMessage(near_message));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            farUsers.removeAll(nearUsers);
            if (!farUsers.isEmpty()) {
                for (WebSocketSession targetSession : farUsers) {
                    ((Set<WebSocketSession>) targetSession.getAttributes().get("nearUsers")).remove(session);
                    ((Set<WebSocketSession>) session.getAttributes().get("nearUsers")).remove(targetSession);
                    if (targetSession.isOpen()) {
                        try {
                            targetSession.sendMessage(new TextMessage(far_message));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return nearUsers;
    }

    public void renewGps(Long userIdx, List<Double> location) {
        if (userLatitudes.containsKey(location.get(0))) {
            userLatitudes.get(location.get(0)).add(userIdx);
        } else {
            userLatitudes.put(location.get(0), new HashSet<>(Collections.singletonList(userIdx)));
        }

        if (userLongitudes.containsKey(location.get(1))) {
            userLongitudes.get(location.get(1)).add(userIdx);
        } else {
            userLongitudes.put(location.get(1), new HashSet<>(Collections.singletonList(userIdx)));
        }
        System.out.println("위도들 : "+userLatitudes);
        System.out.println("경도들 : "+userLongitudes);
        System.out.println("위치들 : "+locations);
    }

    public Set<WebSocketSession> getNearUsers(List<Double> location) {
        Set<Long> nearUsers = new HashSet<>();
        Set<Long> nearLongUsers = new HashSet<>();
        ConcurrentNavigableMap<Double, Set<Long>> nearLats = userLatitudes.subMap(location.get(0)-0.0005, true, location.get(0)+0.0005, true);
        nearLats.forEach((k, v) -> nearUsers.addAll(v));
        ConcurrentNavigableMap<Double, Set<Long>> nearLongs = userLongitudes.subMap(location.get(1)-0.0005, true, location.get(1)+0.0005, true);
        nearLats.forEach((k, v) -> nearLongUsers.addAll(v));
        nearUsers.retainAll(nearLongUsers);
        Set<WebSocketSession> nearUserInfos = new HashSet<>();
        if (!nearUsers.isEmpty()) {
            for (Long userIdx : nearUsers) {
                WebSocketSession targetSession = userSessionMap.get(userIdx);
                nearUserInfos.add(targetSession);
            }
        }
        return nearUserInfos;
    }

    public void METHOD_INIT(WebSocketSession session, Map<String, Object> info) throws IOException {
        for (String key : info.keySet()) {
            session.getAttributes().put(key, info.get(key));
        }

        changeLocation(session);
    }

}
