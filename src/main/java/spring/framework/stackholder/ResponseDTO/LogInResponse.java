package spring.framework.stackholder.ResponseDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInResponse {

    private Long id;

    private String accessToken;

    private String refreshToken;

}
