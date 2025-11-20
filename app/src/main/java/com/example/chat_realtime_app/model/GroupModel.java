package com.example.chat_realtime_app.model;

import com.google.firebase.Timestamp;
import java.util.List;

public class GroupModel {
    private String groupId;
    private String name;
    private String createdBy;
    private Timestamp createdAt;
    private boolean isPublic;
    private long maxMembers;
    private long memberCount;
    private List<String> members;

    public GroupModel() {
    }

    public GroupModel(String groupId, String name, String createdBy,
                      Timestamp createdAt, boolean isPublic,
                      long maxMembers, long memberCount, List<String> members) {
        this.groupId = groupId;
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.maxMembers = maxMembers;
        this.memberCount = memberCount;
        this.members = members;
    }


    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public long getMaxMembers() { return maxMembers; }
    public void setMaxMembers(long maxMembers) { this.maxMembers = maxMembers; }

    public long getMemberCount() { return memberCount; }
    public void setMemberCount(long memberCount) { this.memberCount = memberCount; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) {
        this.members = members;
    }
}
