package com.voting.system.controller;

import com.voting.system.dto.*;
import com.voting.system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voter")
@CrossOrigin(origins = "*")
public class VoterController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/join-room")
    public ResponseEntity<String> joinRoom(@RequestBody JoinRoomRequest request) {
        String result = roomService.joinRoom(request);
        if (result.contains("not found") || result.contains("Error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/vote")
    public ResponseEntity<VoteResponse> castVote(@RequestBody VoteRequest request) {
        VoteResponse response = roomService.castVote(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<RoomDetailsResponse> getRoomDetails(@PathVariable String roomId) {
        RoomDetailsResponse response = roomService.getRoomDetails(roomId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
