package pro.sky.telegrambot.model;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "NotificationTask")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long chat_id;
    private String message;
    private LocalDateTime data;

    public NotificationTask() {
    }

    public NotificationTask(Long chat_id, String message, LocalDateTime data) {
        this.chat_id = chat_id;
        this.message = message;
        this.data = data;
    }


    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(chat_id, that.chat_id) && Objects.equals(message, that.message) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chat_id, message, data);
    }
}
