package com.weread.dto.response.user;

import com.weread.vo.user.UserWithFollowVO;
import java.util.List;

public class FollowingResponse {
    private List<UserWithFollowVO> items;
    private boolean hasMore;
    private String nextCursor;

    // getter & setter
    public List<UserWithFollowVO> getItems() {
        return items;
    }
    public void setItems(List<UserWithFollowVO> items) {
        this.items = items;
    }
    public boolean isHasMore() {
        return hasMore;
    }
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
    public String getNextCursor() {
        return nextCursor;
    }
    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
