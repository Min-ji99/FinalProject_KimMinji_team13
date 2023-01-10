package com.likelion.sns.service;

import com.likelion.sns.domain.dto.AlarmResponse;
import com.likelion.sns.domain.entity.Alarm;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.AlarmRepository;
import com.likelion.sns.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlarmService {
    private final UserService userService;
    private final AlarmRepository alarmRepository;

    public AlarmService(UserService userService, AlarmRepository alarmRepository) {
        this.userService = userService;
        this.alarmRepository = alarmRepository;
    }

    public Page<AlarmResponse> getAlarmList(Pageable pageable, String userName) {
        User user=userService.getUserEntityByUsername(userName);
        Page<AlarmResponse> alarmResponses=AlarmResponse.toList(alarmRepository.findAllByUser(user, pageable));

        return alarmResponses;
    }

    public void saveAlarm(Alarm alarm) {
        alarmRepository.save(alarm);
    }
}
