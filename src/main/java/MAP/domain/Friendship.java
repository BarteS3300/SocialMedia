package MAP.domain;

import java.time.LocalDateTime;

import MAP.domain.User;

public class Friendship extends Entity<Tuple<Long, Long>>{

    LocalDateTime date;

    public Friendship(){
        date = LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }
}
