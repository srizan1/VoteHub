package com.voting.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting.system.dto.BlockRoomRequest;
import com.voting.system.dto.CreateRoomRequest;
import com.voting.system.dto.CreateRoomResponse;
import com.voting.system.dto.DeleteRoomRequest;
import com.voting.system.dto.RemoveUserRequest;
import com.voting.system.dto.RoomDetailsResponse;
import com.voting.system.dto.RoomParticipantResponse;
import com.voting.system.service.RoomService;

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

    // NEW: Remove user from room
    @PostMapping("/remove-user")
    public ResponseEntity<String> removeUser(@RequestBody RemoveUserRequest request) {
        String message = roomService.removeUserFromRoom(request);
        return ResponseEntity.ok(message);
    }

    // NEW: Delete room
    @DeleteMapping("/delete-room")
    public ResponseEntity<String> deleteRoom(@RequestBody DeleteRoomRequest request) {
        String message = roomService.deleteRoom(request);
        return ResponseEntity.ok(message);
    }

    // NEW: Get admin rooms
    @GetMapping("/rooms/{adminId}")
    public ResponseEntity<List<RoomDetailsResponse>> getAdminRooms(@PathVariable Long adminId) {
        List<RoomDetailsResponse> rooms = roomService.getAdminRooms(adminId);
        return ResponseEntity.ok(rooms);
    }

    // NEW: Get room participants
    @GetMapping("/room/{roomId}/participants")
    public ResponseEntity<List<RoomParticipantResponse>> getRoomParticipants(@PathVariable String roomId) {
        List<RoomParticipantResponse> participants = roomService.getRoomParticipants(roomId);
        return ResponseEntity.ok(participants);
    }
}
