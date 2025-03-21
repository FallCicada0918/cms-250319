package com.briup.cms.bean.extend;

import com.briup.cms.bean.Role;
import com.briup.cms.bean.User;
import lombok.Data;

@Data
public class UserExtend extends User {
    private Role role;
}