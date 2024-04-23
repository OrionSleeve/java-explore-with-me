package ru.practicum.request.model;
import lombok.*;
import ru.practicum.request.enumRequest.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class RequestEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "requester_id", nullable = false)
    private Integer requester;
    @Column(name = "event_id", nullable = false)
    private Integer event;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;
}
