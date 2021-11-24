package persistence.DTO;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDTO {
    private int idx;
    private String id;
    private String password;
    private char category;
}
