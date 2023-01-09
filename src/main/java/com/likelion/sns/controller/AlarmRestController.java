package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.AlarmResponse;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.service.AlarmService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/alarms")
public class AlarmRestController {
    private final AlarmService alarmService;

    public AlarmRestController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }
    @GetMapping
    public Response<Page<AlarmResponse>> getAlarmList(@ApiIgnore Authentication authentication,
                                                      @PageableDefault(size=10)
                                                      @SortDefault(sort="createdAt", direction= Sort.Direction.DESC) Pageable pageable){
        Page<AlarmResponse> alarms=alarmService.getAlarmList(pageable, authentication.getName());
        return Response.success(alarms);
    }
}
