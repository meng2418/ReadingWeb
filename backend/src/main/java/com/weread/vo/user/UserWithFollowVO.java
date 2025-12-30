package com.weread.vo.user;

public class UserWithFollowVO {
    private Integer userId;
    private String username;
    private String avatar;
    private String bio;
    private Boolean isFollowing;
    private Boolean isFollower;

    // getter & setter
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public Boolean getIsFollowing() {
        return isFollowing;
    }
    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
    public Boolean getIsFollower() {
        return isFollower;
    }
    public void setIsFollower(Boolean isFollower) {
        this.isFollower = isFollower;
    }
}
