package MAP.domain;

import java.time.LocalDateTime;

import MAP.domain.User;

public class    Friendship extends Entity<Tuple<Long, Long>>{

    LocalDateTime friendsFrom;

    public Friendship(){
        friendsFrom = LocalDateTime.now();
    }

    public Friendship(LocalDateTime localDateTime) {
        friendsFrom = localDateTime;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }
}
