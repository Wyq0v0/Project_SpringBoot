package com.wyq.project_springboot.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyq.project_springboot.dto.chat.ChatDTO;
import com.wyq.project_springboot.dto.chat.ChatUserDTO;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.Chat;
import com.wyq.project_springboot.entity.enumClass.ChatRead;
import com.wyq.project_springboot.service.ChatService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.*;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static ApplicationContext applicationContext;
    private static ChatService chatService;
    private static final Map<String, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    public static void setChatService(ApplicationContext context){
        applicationContext = context;
        chatService = applicationContext.getBean(ChatService.class);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        //查询聊天用户列表
        Result resultDTO = chatService.getChatUserList((int) attributes.get("id"));

        String result = objectMapper.writeValueAsString(Result.success(resultDTO));
        session.sendMessage(new TextMessage(result));
        //向用户session集合添加该用户
        userSessionMap.put(attributes.get("id").toString(),session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        //获取session的attribute，并从attribute中获取用户Id
        Map<String, Object> attributes = session.getAttributes();
        int userId = (int) attributes.get("id");

        //获取请求消息中的主体，并转换成json对象
        Object payload = message.getPayload();
        JsonNode reqJSONNode = objectMapper.readTree(payload.toString());

        int type = reqJSONNode.get("type").asInt();
        Integer receiverId = null;
        String lastMark = null;
        Integer offset = null;
        Integer pageSize = null;
        Result resultDTO = null;

        switch (type){
            case 0:
                //添加聊天关系
                chatService.insertChatRelation(userId,reqJSONNode.get("receiverId").asInt());
                //此处不加break
            case 1://给其他用户发送消息
                receiverId = reqJSONNode.get("receiverId").asInt();
                String msg = reqJSONNode.get("msg").asText();

                Chat chat = new Chat();
                //将消息存入数据库
                chat.setSenderId(userId);
                chat.setReceiverId(receiverId);
                chat.setContent(msg);
                chat.setRecordTime(new Date());
                chat.setIsRead(ChatRead.UNREAD);
                chatService.insertChat(chat);
                //转发给用户
                ChatDTO chatDTO = new ChatDTO();
                chatDTO.setType(3);
                chatDTO.setSenderId(userId);
                chatDTO.setContent(msg);
                chatDTO.setSendTime(chat.getRecordTime());

                //转发给指定session客户端
                WebSocketSession receiverSession = userSessionMap.get(receiverId.toString());
                if(receiverSession != null){
                    String msgResult = objectMapper.writeValueAsString(Result.success(chatDTO));
                    receiverSession.sendMessage(new TextMessage(msgResult));
                }
                break;
            case 2://获取聊天内容列表
                receiverId = reqJSONNode.get("receiverId").asInt();
                lastMark = reqJSONNode.get("lastMark").asText();
                offset = reqJSONNode.get("offset").asInt();
                pageSize = reqJSONNode.get("pageSize").asInt();
                resultDTO = chatService.getChatMsgList(userId,receiverId,lastMark,offset,pageSize);
                String result = objectMapper.writeValueAsString(Result.success(resultDTO));
                session.sendMessage(new TextMessage(result));
                break;
            case 4://获取聊天用户列表
                //查询聊天用户列表
                resultDTO = chatService.getChatUserList((int) attributes.get("id"));
                String resultList = objectMapper.writeValueAsString(Result.success(resultDTO));
                session.sendMessage(new TextMessage(resultList));
                break;
            case 5://获取指定用户信息
                receiverId = reqJSONNode.get("userId").asInt();
                resultDTO = chatService.getChatUser(userId,receiverId);
                ChatUserDTO chatUserDTO = (ChatUserDTO) resultDTO.getData();
                chatUserDTO.setType(5);
                String resultUser = objectMapper.writeValueAsString(Result.success(resultDTO));
                session.sendMessage(new TextMessage(resultUser));
                break;
            case 6://获取聊天对象用户
                receiverId = reqJSONNode.get("userId").asInt();
                resultDTO = chatService.getChatUser(userId,receiverId);
                ChatUserDTO chatUserDTO2 = (ChatUserDTO) resultDTO.getData();
                chatUserDTO2.setType(6);
                String resultChatUser = objectMapper.writeValueAsString(Result.success(resultDTO));
                session.sendMessage(new TextMessage(resultChatUser));
                break;
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        //获取session的attribute，并从attribute中获取用户Id
        Map<String, Object> attributes = session.getAttributes();
        Integer userId = (int) attributes.get("id");
        //从用户session集合中移除该用户
        userSessionMap.remove(userId.toString());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
