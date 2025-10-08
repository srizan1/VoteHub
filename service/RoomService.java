package com.voting.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.system.dto.*;
import com.voting.system.model.*;
import com.voting.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRecordRepository voteRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        try {
            Optional<Administrator> adminOpt = administratorRepository.findById(request.getAdminId());
            if (adminOpt.isEmpty()) {
                return new CreateRoomResponse(null, null, "Administrator not found");
            }

            // Ensure NOTA is in party list
            if (!request.getPartyNames().contains("NOTA")) {
                request.getPartyNames().add("NOTA");
            }

            // Generate unique room ID
            String roomId = generateRoomCode();
            while (roomRepository.existsByRoomId(roomId)) {
                roomId = generateRoomCode();
            }

            // Create room
            Room room = new Room();
            room.setRoomId(roomId);
            room.setRoomName(request.getRoomName());
            room.setAdminId(request.getAdminId());
            room.setVotingStartTime(request.getVotingStartTime());
            room.setVotingEndTime(request.getVotingEndTime());

            // Initialize party votes
            Map<String, Integer> votes = new HashMap<>();
            for (String party : request.getPartyNames()) {
                votes.put(party, 0);
            }
            room.setPartyVotesJson(objectMapper.writeValueAsString(votes));

            // Store party names
            Map<String, List<String>> partyData = new HashMap<>();
            partyData.put("parties", request.getPartyNames());
            room.setPartyNamesJson(objectMapper.writeValueAsString(partyData));

            roomRepository.save(room);

            // Update administrator’s created rooms
            Administrator admin = adminOpt.get();
            updateAdminRooms(admin, roomId);

            return new CreateRoomResponse(roomId, request.getRoomName(), "Room created successfully");

        } catch (JsonProcessingException e) {
            return new CreateRoomResponse(null, null, "Error creating room");
        }
    }

    @Transactional
    public String joinRoom(JoinRoomRequest request) {
        try {
            Optional<Room> roomOpt = roomRepository.findByRoomId(request.getRoomId());
            if (roomOpt.isEmpty()) return "Room not found";

            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isEmpty()) return "User not found";

            Room room = roomOpt.get();
            User user = userOpt.get();

            if (hasUserJoinedRoom(user, request.getRoomId())) {
                return "Already joined this room";
            }

            updateUserRooms(user, request.getRoomId());

            room.setTotalRegistered(room.getTotalRegistered() + 1);
            roomRepository.save(room);

            VoteRecord record = new VoteRecord();
            record.setUserId(request.getUserId());
            record.setRoomId(request.getRoomId());
            record.setHasVoted(false);
            record.setVotedAt(LocalDateTime.now());
            voteRecordRepository.save(record);

            return "Successfully joined room";

        } catch (Exception e) {
            return "Error joining room";
        }
    }

    @Transactional
    public VoteResponse castVote(VoteRequest request) {
        try {
            Optional<Room> roomOpt = roomRepository.findByRoomId(request.getRoomId());
            if (roomOpt.isEmpty()) {
                return new VoteResponse("Room not found", false);
            }

            Room room = roomOpt.get();
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(room.getVotingStartTime())) {
                return new VoteResponse("Voting has not started yet", false);
            }
            if (now.isAfter(room.getVotingEndTime())) {
                return new VoteResponse("Voting has ended", false);
            }

            Optional<VoteRecord> recordOpt =
                    voteRecordRepository.findByUserIdAndRoomId(request.getUserId(), request.getRoomId());

            if (recordOpt.isEmpty()) {
                return new VoteResponse("User not registered in this room", false);
            }

            VoteRecord record = recordOpt.get();
            if (record.getHasVoted()) {
                return new VoteResponse("You have already voted", false);
            }

            Map<String, Integer> votes = objectMapper.readValue(
                    room.getPartyVotesJson(),
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class)
            );

            if (!votes.containsKey(request.getPartyName())) {
                return new VoteResponse("Invalid party name", false);
            }

            votes.put(request.getPartyName(), votes.get(request.getPartyName()) + 1);
            room.setPartyVotesJson(objectMapper.writeValueAsString(votes));
            roomRepository.save(room);

            record.setHasVoted(true);
            record.setVotedAt(LocalDateTime.now());
            voteRecordRepository.save(record);

            return new VoteResponse("Vote cast successfully", true);

        } catch (Exception e) {
            return new VoteResponse("Error casting vote: " + e.getMessage(), false);
        }
    }

    public RoomDetailsResponse getRoomDetails(String roomId) {
        try {
            Optional<Room> roomOpt = roomRepository.findByRoomId(roomId);
            if (roomOpt.isEmpty()) return null;

            Room room = roomOpt.get();

            Map<String, Integer> votes = objectMapper.readValue(
                    room.getPartyVotesJson(),
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class)
            );

            Map<String, List<String>> partyData = objectMapper.readValue(
                    room.getPartyNamesJson(),
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class)
            );

            return new RoomDetailsResponse(
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getTotalRegistered(),
                    room.getVotingStartTime(),
                    room.getVotingEndTime(),
                    partyData.get("parties"),
                    votes,
                    room.getIsActive()
            );

        } catch (Exception e) {
            return null;
        }
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void updateUserRooms(User user, String roomId) throws JsonProcessingException {
        Map<String, List<String>> roomData = objectMapper.readValue(
                user.getRoomsJson(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class)
        );
        roomData.get("rooms").add(roomId);
        user.setRoomsJson(objectMapper.writeValueAsString(roomData));
        userRepository.save(user);
    }

    private void updateAdminRooms(Administrator admin, String roomId) throws JsonProcessingException {
        Map<String, List<String>> roomData = objectMapper.readValue(
                admin.getCreatedRoomsJson(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class)
        );
        roomData.get("rooms").add(roomId);
        admin.setCreatedRoomsJson(objectMapper.writeValueAsString(roomData));
        administratorRepository.save(admin);
    }

    private boolean hasUserJoinedRoom(User user, String roomId) throws JsonProcessingException {
        Map<String, List<String>> roomData = objectMapper.readValue(
                user.getRoomsJson(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class)
        );
        return roomData.get("rooms").contains(roomId);
    }
}
