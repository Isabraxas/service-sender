package cc.viridian.service.statement.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ScheduleService {

    private static ScheduleService instance = new ScheduleService();

    public static ScheduleService getInstance() {
        return instance;
    }
}
