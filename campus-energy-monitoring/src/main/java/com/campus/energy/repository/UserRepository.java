package com.campus.energy.repository;

import com.campus.energy.entity.User;
import com.campus.energy.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户信息数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据角色查找用户列表
     */
    List<User> findByRole(UserRole role);
    
    /**
     * 查找所有启用的用户
     */
    List<User> findByEnabledTrue();
    
    /**
     * 根据用户名模糊查询
     */
    List<User> findByUsernameContaining(String username);
}

