package org.example.finlog.DTO;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private UUID id;

    @Size(min = 2, max = 100)
    private String name;
}
