package com.voting.system.dto;

import java.util.List;

public class UserRoomsResponse {
    private Long userId;
    private String phoneNumber;
    private List<String> activeRoomIds;
    private List<String> completedRoomIds;

    public UserRoomsResponse(Long userId, String phoneNumber, List<String> activeRoomIds, List<String> completedRoomIds) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.activeRoomIds = activeRoomIds;
        this.completedRoomIds = completedRoomIds;
    }

    // getters and setters
}
