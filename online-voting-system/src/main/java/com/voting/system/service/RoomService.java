package com.voting.system.service;

import com.voting.system.dto.*;
import com.voting.system.exception.*;
import com.voting.system.model.*;
import com.voting.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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

    // ------------------- BASIC METHODS -------------------

    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        try {
            Administrator admin = administratorRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new ResourceNotFoundException("Administrator not found with ID: " + request.getAdminId()));

            if (request.getRoomName() == null || request.getRoomName().trim().isEmpty())
                throw new InvalidRequestException("Room name cannot be empty");
            if (request.getPartyNames() == null || request.getPartyNames().isEmpty())
                throw new InvalidRequestException("At least one party name is required");
            if (request.getVotingEndTime().isBefore(request.getVotingStartTime()))
                throw new InvalidRequestException("Voting end time must be after start time");

            String roomId = UUID.randomUUID().toString().substring(0, 8);

            Map<String, Integer> partyVotes = new HashMap<>();
            for (String party : request.getPartyNames()) partyVotes.put(party, 0);

            Room room = new Room();
            room.setRoomId(roomId);
            room.setRoomName(request.getRoomName());
            room.setAdminId(request.getAdminId());
            room.setVotingStartTime(request.getVotingStartTime());
            room.setVotingEndTime(request.getVotingEndTime());
            room.setPartyNamesJson(objectMapper.writeValueAsString(request.getPartyNames()));
            room.setPartyVotesJson(objectMapper.writeValueAsString(partyVotes));
            room.setIsActive(1);
            room.setIsBlocked(0);
            room.setTotalRegistered(0);

            roomRepository.save(room);

            List<String> createdRooms = new ArrayList<>();
            if (admin.getCreatedRoomsJson() != null && !admin.getCreatedRoomsJson().isEmpty())
                createdRooms = objectMapper.readValue(admin.getCreatedRoomsJson(), new TypeReference<List<String>>() {});
            createdRooms.add(roomId);
            admin.setCreatedRoomsJson(objectMapper.writeValueAsString(createdRooms));
            administratorRepository.save(admin);

            return new CreateRoomResponse(roomId, request.getRoomName(), "Room created successfully");

        } catch (ResourceNotFoundException | InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error creating room: " + e.getMessage(), e);
        }
    }

    public RoomDetailsResponse getRoomDetails(String roomId) {
        try {
            Room room = roomRepository.findByRoomId(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

            List<String> partyNames = objectMapper.readValue(room.getPartyNamesJson(), new TypeReference<List<String>>() {});
            Map<String, Integer> currentVotes = objectMapper.readValue(room.getPartyVotesJson(), new TypeReference<Map<String, Integer>>() {});

            RoomDetailsResponse response = new RoomDetailsResponse(
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getTotalRegistered(),
                    room.getVotingStartTime(),
                    room.getVotingEndTime(),
                    partyNames,
                    currentVotes,
                    room.getIsActive(),
                    room.getIsBlocked()
            );
            
            // Calculate total votes from currentVotes map
            int totalVotes = currentVotes.values().stream().mapToInt(Integer::intValue).sum();
            response.setTotalVotes(totalVotes);
            
            // Set hasVoted to null for general room details (not user-specific)
            response.setHasVoted(null);
            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving room details: " + e.getMessage(), e);
        }
    }

    public String joinRoom(JoinRoomRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            if (room.getIsBlocked() == 1) throw new RoomAccessException("This room is blocked. No new members can join.");

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

            List<String> userRooms = new ArrayList<>();
            if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty())
                userRooms = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});

            if (userRooms.contains(request.getRoomId()))
                throw new DuplicateResourceException("You have already joined this room");

            userRooms.add(request.getRoomId());
            user.setRoomsJson(objectMapper.writeValueAsString(userRooms));
            userRepository.save(user);

            room.setTotalRegistered(room.getTotalRegistered() + 1);
            roomRepository.save(room);

            VoteRecord voteRecord = new VoteRecord();
            voteRecord.setUserId(request.getUserId());
            voteRecord.setRoomId(request.getRoomId());
            voteRecord.setHasVoted(0);
            voteRecordRepository.save(voteRecord);

            return "Successfully joined room: " + room.getRoomName();

        } catch (ResourceNotFoundException | RoomAccessException | DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error joining room: " + e.getMessage(), e);
        }
    }

    public VoteResponse castVote(VoteRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(room.getVotingStartTime())) throw new VotingException("Voting has not started yet", 425);
            if (now.isAfter(room.getVotingEndTime())) throw new VotingException("Voting has ended", 410);
            if (room.getIsActive() != 1) throw new VotingException("This room is no longer active", 403);

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

            List<String> userRooms = new ArrayList<>();
            if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty())
                userRooms = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});
            if (!userRooms.contains(request.getRoomId()))
                throw new VotingException("You must join the room before voting", 403);

            VoteRecord voteRecord = voteRecordRepository.findByUserIdAndRoomId(request.getUserId(), request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vote record not found"));

            if (voteRecord.getHasVoted() == 1) throw new VotingException("You have already voted in this room", 409);

            List<String> partyNames = objectMapper.readValue(room.getPartyNamesJson(), new TypeReference<List<String>>() {});
            if (!partyNames.contains(request.getPartyName()))
                throw new InvalidRequestException("Invalid party name: " + request.getPartyName());

            Map<String, Integer> partyVotes = objectMapper.readValue(room.getPartyVotesJson(), new TypeReference<Map<String, Integer>>() {});
            partyVotes.put(request.getPartyName(), partyVotes.get(request.getPartyName()) + 1);
            room.setPartyVotesJson(objectMapper.writeValueAsString(partyVotes));
            roomRepository.save(room);

            voteRecord.setHasVoted(1);
            voteRecord.setVotedAt(LocalDateTime.now());
            voteRecordRepository.save(voteRecord);

            return new VoteResponse("Vote cast successfully for " + request.getPartyName(), true);

        } catch (ResourceNotFoundException | VotingException | InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error casting vote: " + e.getMessage(), e);
        }
    }

    public String blockRoom(BlockRoomRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            if (!room.getAdminId().equals(request.getAdminId()))
                throw new RoomAccessException("Only the room creator can block/unblock the room");

            room.setIsBlocked(request.getBlocked());
            roomRepository.save(room);

            String status = request.getBlocked() == 1 ? "blocked" : "unblocked";
            return "Room successfully " + status + ". " +
                    (request.getBlocked() == 1 ? "No new members can join." : "New members can now join.");

        } catch (ResourceNotFoundException | RoomAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating room status: " + e.getMessage(), e);
        }
    }

    // ------------------- NEW METHODS -------------------


    public UserRoomsListResponse getUserRoomsList(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            List<String> roomIds = new ArrayList<>();
            if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty()) {
                roomIds = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});
            }

            return new UserRoomsListResponse(
                    user.getId(),
                    user.getPhoneNumber(),
                    roomIds
            );

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error getting user rooms list: " + e.getMessage(), e);
        }
    }

    public List<RoomDetailsResponse> getUserRoomsWithDetails(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            List<String> roomIds = new ArrayList<>();
            if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty()) {
                roomIds = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});
            }

            List<RoomDetailsResponse> rooms = new ArrayList<>();
            for (String roomId : roomIds) {
                try {
                    Room room = roomRepository.findByRoomId(roomId).orElse(null);
                    if (room != null) {
                        List<String> partyNames = objectMapper.readValue(room.getPartyNamesJson(), new TypeReference<List<String>>() {});
                        Map<String, Integer> currentVotes = objectMapper.readValue(room.getPartyVotesJson(), new TypeReference<Map<String, Integer>>() {});

                        // Check if user has voted in this room
                        VoteRecord voteRecord = voteRecordRepository.findByUserIdAndRoomId(userId, roomId).orElse(null);
                        boolean hasVoted = voteRecord != null && voteRecord.getHasVoted() == 1;

                        RoomDetailsResponse roomDetails = new RoomDetailsResponse(
                                room.getRoomId(),
                                room.getRoomName(),
                                room.getTotalRegistered(),
                                room.getVotingStartTime(),
                                room.getVotingEndTime(),
                                partyNames,
                                currentVotes,
                                room.getIsActive(),
                                room.getIsBlocked()
                        );
                        
                        // Calculate total votes from currentVotes map
                        int totalVotes = currentVotes.values().stream().mapToInt(Integer::intValue).sum();
                        roomDetails.setTotalVotes(totalVotes);
                        
                        // Add voting status to the response
                        roomDetails.setHasVoted(hasVoted);
                        rooms.add(roomDetails);
                    }
                } catch (Exception e) {
                    // Skip rooms that can't be parsed
                    continue;
                }
            }

            return rooms;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error getting user rooms with details: " + e.getMessage(), e);
        }
    }
    
    


    public String leaveRoom(LeaveRoomRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(room.getVotingStartTime()) && now.isBefore(room.getVotingEndTime()))
                throw new VotingException("Cannot leave room during active voting period", 403);

            List<String> userRooms = new ArrayList<>();
            if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty())
                userRooms = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});

            if (!userRooms.contains(request.getRoomId()))
                throw new InvalidRequestException("You are not a member of this room");

            userRooms.remove(request.getRoomId());
            user.setRoomsJson(objectMapper.writeValueAsString(userRooms));
            userRepository.save(user);

            room.setTotalRegistered(Math.max(0, room.getTotalRegistered() - 1));
            roomRepository.save(room);

            voteRecordRepository.findByUserIdAndRoomId(request.getUserId(), request.getRoomId())
                    .ifPresent(voteRecordRepository::delete);

            return "Successfully left room: " + room.getRoomName();

        } catch (ResourceNotFoundException | VotingException | InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error leaving room: " + e.getMessage(), e);
        }
    }

    public String removeUserFromRoom(RemoveUserRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            if (!room.getAdminId().equals(request.getAdminId()))
                throw new RoomAccessException("Only the room creator can remove users");

            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(room.getVotingStartTime()) && now.isBefore(room.getVotingEndTime()))
                throw new VotingException("Cannot remove users during active voting period", 403);

            User userToRemove = userRepository.findById(request.getUserIdToRemove())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserIdToRemove()));

            List<String> userRooms = new ArrayList<>();
            if (userToRemove.getRoomsJson() != null && !userToRemove.getRoomsJson().isEmpty())
                userRooms = objectMapper.readValue(userToRemove.getRoomsJson(), new TypeReference<List<String>>() {});

            if (!userRooms.contains(request.getRoomId()))
                throw new InvalidRequestException("User is not a member of this room");

            userRooms.remove(request.getRoomId());
            userToRemove.setRoomsJson(objectMapper.writeValueAsString(userRooms));
            userRepository.save(userToRemove);

            room.setTotalRegistered(Math.max(0, room.getTotalRegistered() - 1));
            roomRepository.save(room);

            voteRecordRepository.findByUserIdAndRoomId(request.getUserIdToRemove(), request.getRoomId())
                    .ifPresent(voteRecordRepository::delete);

            return "User successfully removed from room: " + room.getRoomName();

        } catch (ResourceNotFoundException | RoomAccessException | VotingException | InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error removing user from room: " + e.getMessage(), e);
        }
    }

    public String deleteRoom(DeleteRoomRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            if (!room.getAdminId().equals(request.getAdminId()))
                throw new RoomAccessException("Only the room creator can delete the room");

            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(room.getVotingStartTime()) && now.isBefore(room.getVotingEndTime()))
                throw new VotingException("Cannot delete room during active voting period", 403);

            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty()) {
                    List<String> userRooms = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});
                    if (userRooms.contains(request.getRoomId())) {
                        userRooms.remove(request.getRoomId());
                        user.setRoomsJson(objectMapper.writeValueAsString(userRooms));
                        userRepository.save(user);
                    }
                }
            }

            List<VoteRecord> voteRecords = voteRecordRepository.findAll();
            for (VoteRecord record : voteRecords) {
                if (record.getRoomId().equals(request.getRoomId()))
                    voteRecordRepository.delete(record);
            }

            Administrator admin = administratorRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new ResourceNotFoundException("Administrator not found"));
            if (admin.getCreatedRoomsJson() != null && !admin.getCreatedRoomsJson().isEmpty()) {
                List<String> createdRooms = objectMapper.readValue(admin.getCreatedRoomsJson(), new TypeReference<List<String>>() {});
                createdRooms.remove(request.getRoomId());
                admin.setCreatedRoomsJson(objectMapper.writeValueAsString(createdRooms));
                administratorRepository.save(admin);
            }

            String roomName = room.getRoomName();
            roomRepository.delete(room);

            return "Room '" + roomName + "' successfully deleted";

        } catch (ResourceNotFoundException | RoomAccessException | VotingException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting room: " + e.getMessage(), e);
        }
    }

    public List<RoomDetailsResponse> getAdminRooms(Long adminId) {
        try {
            Administrator admin = administratorRepository.findById(adminId)
                    .orElseThrow(() -> new ResourceNotFoundException("Administrator not found with ID: " + adminId));

            List<String> roomIds = new ArrayList<>();
            if (admin.getCreatedRoomsJson() != null && !admin.getCreatedRoomsJson().isEmpty()) {
                roomIds = objectMapper.readValue(admin.getCreatedRoomsJson(), new TypeReference<List<String>>() {});
            }

            List<RoomDetailsResponse> rooms = new ArrayList<>();
            for (String roomId : roomIds) {
                try {
                    Room room = roomRepository.findByRoomId(roomId).orElse(null);
                    if (room != null) {
                        List<String> partyNames = objectMapper.readValue(room.getPartyNamesJson(), new TypeReference<List<String>>() {});
                        Map<String, Integer> currentVotes = objectMapper.readValue(room.getPartyVotesJson(), new TypeReference<Map<String, Integer>>() {});

                        RoomDetailsResponse roomDetails = new RoomDetailsResponse(
                                room.getRoomId(),
                                room.getRoomName(),
                                room.getTotalRegistered(),
                                room.getVotingStartTime(),
                                room.getVotingEndTime(),
                                partyNames,
                                currentVotes,
                                room.getIsActive(),
                                room.getIsBlocked()
                        );
                        
                        // Calculate total votes from currentVotes map
                        int totalVotes = currentVotes.values().stream().mapToInt(Integer::intValue).sum();
                        roomDetails.setTotalVotes(totalVotes);
                        
                        // Set hasVoted to false for administrators (they don't vote)
                        roomDetails.setHasVoted(false);
                        rooms.add(roomDetails);
                    }
                } catch (Exception e) {
                    // Skip rooms that can't be parsed
                    continue;
                }
            }

            return rooms;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error getting admin rooms: " + e.getMessage(), e);
        }
    }

    public List<RoomParticipantResponse> getRoomParticipants(String roomId) {
        try {
            Room room = roomRepository.findByRoomId(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

            List<RoomParticipantResponse> participants = new ArrayList<>();
            
            // Get all users who have joined this room
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty()) {
                    List<String> userRooms = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});
                    if (userRooms.contains(roomId)) {
                        // Check if user has voted
                        VoteRecord voteRecord = voteRecordRepository.findByUserIdAndRoomId(user.getId(), roomId).orElse(null);
                        boolean hasVoted = voteRecord != null && voteRecord.getHasVoted() == 1;
                        
                        participants.add(new RoomParticipantResponse(
                                user.getId(),
                                user.getName(),
                                user.getPhoneNumber(),
                                hasVoted,
                                voteRecord != null ? voteRecord.getVotedAt() : null
                        ));
                    }
                }
            }

            return participants;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error getting room participants: " + e.getMessage(), e);
        }
    }
}
