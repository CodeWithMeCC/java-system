
package com.publicconsult.platform.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AuthMapper {

    Map<String, Object> findByUsername(
            @Param("username") String username
    );

    int insertUser(
            @Param("user") Map<String, Object> user
    );

    Map<String, Object> findById(
            @Param("id") Long id
    );

    int insertProfessorProfile(
            @Param("userId") Long userId,
            @Param("realName") String realName
    );
}
