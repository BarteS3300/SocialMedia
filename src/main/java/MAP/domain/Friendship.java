package MAP.domain;

import java.time.LocalDateTime;

import MAP.domain.User;

public class Friendship extends Entity<Tuple<Long, Long>>{

    LocalDateTime friendsFrom;

    String status = "pending";

    public Friendship(){
        friendsFrom = LocalDateTime.now();
    }

    public Friendship(LocalDateTime localDateTime) {
        friendsFrom = localDateTime;
    }

    public Friendship(LocalDateTime localDateTime, String status) {
        friendsFrom = localDateTime;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }
}
