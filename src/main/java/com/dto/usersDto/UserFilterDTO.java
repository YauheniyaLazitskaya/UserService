package com.dto.usersDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterDTO {
    private String name;
    private String surname;
    private String email;
    private Boolean active;
}
