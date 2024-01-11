package MAP.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{

    private Long from;

    private List<Long> to = new ArrayList<>();

    private String message;

    private LocalDateTime data;

    private Long reply = null;

    public Message(String message, LocalDateTime data, Long reply) {
        this.message = message;
        this.data = data;
        this.reply = reply;
    }

    public Message(Long from, String message, LocalDateTime data, Long reply) {
        this.from = from;
        this.message = message;
        this.data = LocalDateTime.now();
        this.reply = reply;
    }
    public Message(Long from, List<Long> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = LocalDateTime.now();
    }

    public Message(Long from, List<Long> to, String message, Long reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = LocalDateTime.now();
        this.reply = reply;
    }

    public Message(Long from, List<Long> to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
    }

    public Message(Long from, List<Long> to, String message, LocalDateTime data, Long reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = reply;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public void addTo(Long id){
        this.to.add(id);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Message message1)) return false;
        if (!super.equals(o)) return false;

        if (!Objects.equals(from, message1.from)) return false;
        if (!Objects.equals(to, message1.to)) return false;
        if (!Objects.equals(message, message1.message)) return false;
        if (!Objects.equals(data, message1.data)) return false;
        return Objects.equals(reply, message1.reply);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (reply != null ? reply.hashCode() : 0);
        return result;
    }
}
