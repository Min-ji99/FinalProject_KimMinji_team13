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
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    public AlarmService(UserRepository userRepository, AlarmRepository alarmRepository) {
        this.userRepository = userRepository;
        this.alarmRepository = alarmRepository;
    }

    public Page<AlarmResponse> getAlarmList(Pageable pageable, String userName) {
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s가 존재하지 않습니다.", userName)));
        Page<AlarmResponse> alarmResponses=AlarmResponse.toList(alarmRepository.findAllByUser(user, pageable));

        return alarmResponses;
    }

    public void saveAlarm(Alarm alarm) {
        alarmRepository.save(alarm);
    }
}
