package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.AlarmResponse;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.service.AlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@Api(tags="Alarm")
@RestController
@RequestMapping("/api/v1/alarms")
public class AlarmRestController {
    private final AlarmService alarmService;

    public AlarmRestController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }
    @ApiOperation(value="알람 조회", notes="로그인 후 확인 가능\n 사용자가 작성한 글에 달린 댓글과 좋아요 최신순으로 조회")
    @GetMapping
    public Response<Page<AlarmResponse>> getAlarmList(@ApiIgnore Authentication authentication,
                                                      @PageableDefault(size=20)
                                                      @SortDefault(sort="createdAt", direction= Sort.Direction.DESC) Pageable pageable){
        Page<AlarmResponse> alarms=alarmService.getAlarmList(pageable, authentication.getName());
        return Response.success(alarms);
    }
}
