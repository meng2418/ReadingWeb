package com.weread.vo.book;

import lombok.Builder;
import lombok.Data;

/**
 * 书籍访问权限状态展示值对象 (Value Object)
 * 用于检查用户是否拥有某本书的永久阅读权限。
 */
@Data
@Builder
public class BookAccessStatusVO {

    private Long userId;
    
    private Long bookId;

    /**
     * 是否拥有该书籍的永久阅读权限（通过购买获得）
     */
    private Boolean hasPermanentAccess; 
    
    // 注意：如果是完整的权限检查，这里可能还需要包含：
    // private Boolean hasTemporaryAccess; // 例如：是否在会员有效期内
}