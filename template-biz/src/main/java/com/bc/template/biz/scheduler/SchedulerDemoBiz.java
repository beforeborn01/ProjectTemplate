package com.bc.template.biz.scheduler;

import com.bc.template.service.util.DingTalkAlertUtil;
import javax.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.biz.scheduler
 * @Description:
 * @date Date : 2022年10月13日 14:11
 */
@Service
public class SchedulerDemoBiz {

    @Resource
    private DingTalkAlertUtil dingTalkAlertUtil;

    /**
     * //todo delete
     * codeRules 对于定时任务、mq消费等后台任务，需要手动进行catch，并根据需要打印日志、发送告警
     */
    @Scheduled(cron="0/5 1 * * * ?")
    public void task(){
        try {
            int n = 0;
            System.out.println(3 / n);
        }catch (Exception e){
            System.out.println("task error");
            dingTalkAlertUtil.alert(e,"scheduler","process task failed");
        }
    }

}
