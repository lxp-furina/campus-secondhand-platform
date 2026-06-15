package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Item;
import com.campus.secondhand.entity.Message;
import com.campus.secondhand.exception.BizException;
import com.campus.secondhand.mapper.ItemMapper;
import com.campus.secondhand.mapper.MessageMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.MessageService;
import com.campus.secondhand.utils.AuthContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageMapper messageMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public MessageServiceImpl(MessageMapper messageMapper, ItemMapper itemMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Message create(Requests.MessageSave request) {
        Item item = itemMapper.selectById(request.getItemId());
        if (item == null) {
            throw new BizException("物品不存在");
        }
        if (request.getParentId() != null && messageMapper.selectById(request.getParentId()) == null) {
            throw new BizException("回复的留言不存在");
        }
        Message message = new Message();
        message.setItemId(request.getItemId());
        message.setParentId(request.getParentId());
        message.setContent(request.getContent());
        message.setUserId(AuthContext.userId());
        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<Views.MessageView> listByItem(Long itemId) {
        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getItemId, itemId)
                .orderByAsc(Message::getCreatedAt));
        Map<Long, List<Message>> replies = messages.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(Message::getParentId));
        return messages.stream()
                .filter(m -> m.getParentId() == null)
                .map(m -> toView(m, replies))
                .collect(Collectors.toList());
    }

    private Views.MessageView toView(Message message, Map<Long, List<Message>> replies) {
        Views.MessageView view = new Views.MessageView();
        view.setMessage(message);
        view.setUser(userMapper.selectById(message.getUserId()));
        view.setReplies(replies.getOrDefault(message.getId(), java.util.Collections.emptyList())
                .stream()
                .map(m -> toView(m, replies))
                .collect(Collectors.toList()));
        return view;
    }
}
