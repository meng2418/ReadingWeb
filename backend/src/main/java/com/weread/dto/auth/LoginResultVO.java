package com.weread.dto.auth;
import com.weread.vo.user.UserSimpleVO;
import lombok.Data;

@Data
public class LoginResultVO {
    private UserSimpleVO user;
    private String token;

    public UserSimpleVO getUser() {
        return user;
    }

    public void setUser(UserSimpleVO user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
