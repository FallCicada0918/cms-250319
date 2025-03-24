package com.briup.cms.bean.extend;

import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.User;
import lombok.Data;

@Data
public class SubCommentExtend extends Subcomment {
    //二级评论发布人
    private User author;
}
