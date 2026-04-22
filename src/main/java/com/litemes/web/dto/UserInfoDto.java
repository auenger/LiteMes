package com.litemes.web.dto;

public class UserInfoDto {

    private Long id;
    private String username;
    private String realName;
    private String[] roles;

    public UserInfoDto() {
    }

    public UserInfoDto(Long id, String username, String realName, String[] roles) {
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
