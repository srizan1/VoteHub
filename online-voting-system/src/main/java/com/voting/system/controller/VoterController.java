package com.voting.system.controller;

import com.voting.system.dto.*;
import com.voting.system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/voter")
@CrossOrigin(origins = "*")
public class VoterController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/join-room")
    public ResponseEntity<String> joinRoom(@RequestBody JoinRoomRequest request) {
        String result = roomService.joinRoom(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/vote")
    public ResponseEntity<VoteResponse> castVote(@RequestBody VoteRequest request) {
        VoteResponse response = roomService.castVote(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<RoomDetailsResponse> getRoomDetails(@PathVariable String roomId) {
        RoomDetailsResponse response = roomService.getRoomDetails(roomId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-rooms/{userId}")
    public ResponseEntity<List<RoomDetailsResponse>> getMyRoomsList(@PathVariable Long userId) {
        List<RoomDetailsResponse> response = roomService.getUserRoomsWithDetails(userId);
        return ResponseEntity.ok(response);
    }


    // NEW: Leave room
    @PostMapping("/leave-room")
    public ResponseEntity<String> leaveRoom(@RequestBody LeaveRoomRequest request) {
        String message = roomService.leaveRoom(request);
        return ResponseEntity.ok(message);
    }
}