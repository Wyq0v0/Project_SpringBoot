package com.wyq.project_springboot.service.Impl;

import com.wyq.project_springboot.dto.chat.ChatListDTO;
import com.wyq.project_springboot.dto.chat.ChatMsgAndUserDTO;
import com.wyq.project_springboot.dto.chat.ChatUserDTO;
import com.wyq.project_springboot.entity.*;
import com.wyq.project_springboot.entity.enumClass.ChatRead;
import com.wyq.project_springboot.mapper.*;
import com.wyq.project_springboot.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatRelationMapper chatRelationMapper;
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConcernMapper concernMapper;

    /**
     * 获取聊天用户列表
     * @param userId
     * @return
     */
    @Override
    public Result getChatUserList(int userId) {
        ChatListDTO chatListDTO = new ChatListDTO();
        chatListDTO.setType(4);

        //获取聊天关系
        List<ChatRelation> chatRelationList = chatRelationMapper.selectChatRelationList(userId);

        //创建chatUserDTO列表
        List<ChatUserDTO> chatUserDTOList = new ArrayList<>();
        for (ChatRelation chatRelation : chatRelationList) {

            ChatUserDTO chatUserDTO = new ChatUserDTO();

            //获取接收者id
            int receiverId = chatRelation.getReceiverId();
            User receiver = userMapper.selectUserById(receiverId);

            receiver.setPassword(null);

            chatUserDTO.setUser(receiver);

            //查询最后一条聊天记录
            Chat chat = chatMapper.selectLastChat(userId, receiver.getId());

            chatUserDTO.setLastMsgTime(chat.getRecordTime());

            if (chat.getIsRead() == ChatRead.UNREAD) {
                chatUserDTO.setRead(false);
            } else {
                chatUserDTO.setRead(true);
            }

            Concern concern = concernMapper.selectConcern(userId, receiverId);
            //如果有关注，则设置备注名
            if (concern != null) {
                chatUserDTO.setNotesName(concern.getNotesName());
            }

            //为chatUserDTOList添加该对象
            chatUserDTOList.add(chatUserDTO);
        }

        chatListDTO.setListData(chatUserDTOList);

        return Result.success(chatListDTO);
    }

    /**
     * 获取聊天内容列表
     * @param userId
     * @param receiverId
     * @param lastMark
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public Result getChatMsgList(int userId, int receiverId,String lastMark,int offset,int pageSize) {
        ChatListDTO chatListDTO = new ChatListDTO();
        chatListDTO.setType(2);

        Date lastMarkDate = new Date(Long.parseLong(lastMark));

        List<Chat> chats = chatMapper.selectChatList(userId, receiverId, lastMarkDate, offset, pageSize);
        if(chats == null || chats.isEmpty()){
            return Result.success(chatListDTO);
        }

        Date lastMarkCount = new Date();
        int offsetCount = 1;
        for(Chat chat:chats){
            //计算偏移量
            Date time = chat.getRecordTime();

            if(time.compareTo(lastMarkCount) == 0){
                offsetCount++;
            }else {
                //得到最后一个元素标识
                lastMarkCount = time;
                offsetCount = 1;
            }
        }

        //设置最后一个元素标识和偏移量
        chatListDTO.setLastMark(lastMarkCount.toString());
        chatListDTO.setOffset(offsetCount);

        chatListDTO.setListData(chats);

        return Result.success(chatListDTO);
    }

    /**
     * 获取指定用户的用户对象和聊天内容
     * @param userId
     * @param receiverId
     * @param pageSize
     * @return
     */
    @Override
    public Result getChatMsgAndUser(int userId, int receiverId,int pageSize) {
        ChatMsgAndUserDTO chatMsgAndUserDTO = new ChatMsgAndUserDTO();
        chatMsgAndUserDTO.setType(5);

        List<Chat> chats = chatMapper.selectChatList(userId, receiverId, new Date(System.currentTimeMillis()), 0, pageSize);
        if(chats == null || chats.isEmpty()){
            return Result.success(chatMsgAndUserDTO);
        }

        chatMsgAndUserDTO.setChatList(chats);

        //获取聊天对象信息
        ChatUserDTO chatUserDTO = new ChatUserDTO();

        //获取接收者id
        User receiver = userMapper.selectUserById(userId);

        receiver.setPassword(null);

        chatUserDTO.setUser(receiver);

        //查询最后一条聊天记录
        Chat chat = chatMapper.selectLastChat(userId, receiver.getId());

        chatUserDTO.setLastMsgTime(chat.getRecordTime());

        if (chat.getIsRead() == ChatRead.UNREAD) {
            chatUserDTO.setRead(false);
        } else {
            chatUserDTO.setRead(true);
        }

        Concern concern = concernMapper.selectConcern(userId, receiverId);
        //如果有关注，则设置备注名
        if (concern != null) {
            chatUserDTO.setNotesName(concern.getNotesName());
        }

        chatMsgAndUserDTO.setChatUserDTO(chatUserDTO);

        chatMsgAndUserDTO.setType(10);

        return Result.success(chatMsgAndUserDTO);
    }

    /**
     * 向数据库插入聊天内容
     * @param chat
     */
    @Override
    public void insertChat(Chat chat) {
        chatMapper.insertChat(chat);
    }

    /**
     * 向数据库插入聊天关系
     * @param originatorId
     * @param receiverId
     */
    @Override
    public void insertChatRelation(int originatorId, int receiverId) {
        ChatRelation chatRelation = new ChatRelation();

        //需要添加两条聊天关系，发送者和接收者互换
        chatRelation.setOriginatorId(originatorId);
        chatRelation.setReceiverId(receiverId);
        chatRelationMapper.insertChatRelationList(chatRelation);

        chatRelation.setOriginatorId(receiverId);
        chatRelation.setReceiverId(originatorId);
        chatRelationMapper.insertChatRelationList(chatRelation);

    }

    @Override
    public Result getChatUser(int userId,int receiverId) {
        ChatUserDTO chatUserDTO = new ChatUserDTO();

        User user = userMapper.selectUserById(receiverId);
        user.setPassword(null);
        chatUserDTO.setUser(user);

        Concern concern = concernMapper.selectConcern(userId, receiverId);
        //如果有关注，则设置备注名
        if (concern != null) {
            chatUserDTO.setNotesName(concern.getNotesName());
        }

        //查询最后一条聊天记录
        Chat chat = chatMapper.selectLastChat(userId, receiverId);

        chatUserDTO.setLastMsgTime(chat.getRecordTime());

        if (chat.getIsRead() == ChatRead.UNREAD) {
            chatUserDTO.setRead(false);
        } else {
            chatUserDTO.setRead(true);
        }

        return Result.success(chatUserDTO);
    }


}
