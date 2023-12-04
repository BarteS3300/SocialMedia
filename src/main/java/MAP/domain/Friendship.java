package MAP.domain;

import java.time.LocalDateTime;

import MAP.domain.User;

public class Friendship extends Entity<Tuple<Long, Long>>{

    LocalDateTime friendsFrom;

    public Friendship(){
        friendsFrom = LocalDateTime.now();
    }

    public Friendship(LocalDateTime localDateTime) {
        friendsFrom = localDateTime;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }
}
