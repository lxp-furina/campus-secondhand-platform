package com.campus.secondhand.service;

import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Message;

import java.util.List;

public interface MessageService {
    Message create(Requests.MessageSave request);
    List<Views.MessageView> listByItem(Long itemId);
}
