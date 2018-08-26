package cc.viridian.service.statement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleTask {

    //@Scheduled(cron = "0 * * * * ?")  //each minute at 0 seconds
    public void scheduleTaskUsingCronExpression() {

        //long now = System.currentTimeMillis() / 1000;
        //System.out.println("Current Thread : " +  Thread.currentThread().getName());
    }
}
