package com.voting.system.controller;

import com.voting.system.dto.*;
import com.voting.system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/create-room")
    public ResponseEntity<CreateRoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<RoomDetailsResponse> getRoomDetails(@PathVariable String roomId) {
        RoomDetailsResponse response = roomService.getRoomDetails(roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/block-room")
    public ResponseEntity<String> blockRoom(@RequestBody BlockRoomRequest request) {
        String message = roomService.blockRoom(request);
        return ResponseEntity.ok(message);
    }
}
