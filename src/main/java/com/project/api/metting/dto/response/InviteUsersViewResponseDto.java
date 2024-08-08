package com.project.api.metting.dto.response;

import com.project.api.metting.entity.GroupUser;
import lombok.*;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class InviteUsersViewResponseDto {

        private String id;
        private String userName;
        private String userUnivName;
        private String userMajor;

        public InviteUsersViewResponseDto(GroupUser groupUser) {
            this.id = groupUser.getId();
            this.userName = groupUser.getUser().getName();
            this.userUnivName = groupUser.getUser().getUnivName();
            this.userMajor = groupUser.getUser().getMajor();
        }
    }
