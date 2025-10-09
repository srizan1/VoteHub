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
            for (String party : request.getPartyNames())
                partyVotes.put(party, 0);

            Room room = new Room();
            room.setRoomId(roomId);
            room.setRoomName(request.getRoomName());
            room.setAdminId(request.getAdminId());
            room.setVotingStartTime(request.getVotingStartTime());
            room.setVotingEndTime(request.getVotingEndTime());
            room.setPartyNamesJson(objectMapper.writeValueAsString(request.getPartyNames()));
            room.setPartyVotesJson(objectMapper.writeValueAsString(partyVotes));
            room.setIsActive(true);
            room.setIsBlocked(false);
            room.setTotalRegistered(0);
            roomRepository.save(room);

            List<String> createdRooms = new ArrayList<>();
            if (admin.getCreatedRoomsJson() != null && !admin.getCreatedRoomsJson().isEmpty())
                createdRooms = objectMapper.readValue(admin.getCreatedRoomsJson(), new TypeReference<List<String>>() {});
            createdRooms.add(roomId);
            admin.setCreatedRoomsJson(objectMapper.writeValueAsString(createdRooms));
            administratorRepository.save(admin);

            return new CreateRoomResponse(roomId, request.getRoomName(), "Room created successfully");

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

            return new RoomDetailsResponse(
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
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving room details: " + e.getMessage(), e);
        }
    }
    public String joinRoom(JoinRoomRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            if (room.getIsBlocked())
                throw new RoomAccessException("This room is blocked. No new members can join.");

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
            voteRecord.setHasVoted(false);
            voteRecordRepository.save(voteRecord);

            return "Successfully joined room: " + room.getRoomName();

        } catch (Exception e) {
            throw new RuntimeException("Error joining room: " + e.getMessage(), e);
        }
    }
    public VoteResponse castVote(VoteRequest request) {
        try {
            Room room = roomRepository.findByRoomId(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(room.getVotingStartTime()))
                throw new VotingException("Voting has not started yet", 425);
            if (now.isAfter(room.getVotingEndTime()))
                throw new VotingException("Voting has ended", 410);
            if (!room.getIsActive())
                throw new VotingException("This room is no longer active", 403);

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

            List<String> userRooms = new ArrayList<>();
            if (user.getRoomsJson() != null && !user.getRoomsJson().isEmpty())
                userRooms = objectMapper.readValue(user.getRoomsJson(), new TypeReference<List<String>>() {});
            if (!userRooms.contains(request.getRoomId()))
                throw new VotingException("You must join the room before voting", 403);

            VoteRecord voteRecord = voteRecordRepository.findByUserIdAndRoomId(request.getUserId(), request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vote record not found"));

            if (voteRecord.getHasVoted())
                throw new VotingException("You have already voted in this room", 409);

            List<String> partyNames = objectMapper.readValue(room.getPartyNamesJson(), new TypeReference<List<String>>() {});
            if (!partyNames.contains(request.getPartyName()))
                throw new InvalidRequestException("Invalid party name: " + request.getPartyName());

            Map<String, Integer> partyVotes = objectMapper.readValue(room.getPartyVotesJson(), new TypeReference<Map<String, Integer>>() {});
            partyVotes.put(request.getPartyName(), partyVotes.get(request.getPartyName()) + 1);
            room.setPartyVotesJson(objectMapper.writeValueAsString(partyVotes));
            roomRepository.save(room);

            voteRecord.setHasVoted(true);
            voteRecord.setVotedAt(LocalDateTime.now());
            voteRecordRepository.save(voteRecord);

            return new VoteResponse("Vote cast successfully for " + request.getPartyName(), true);

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

            String status = request.getBlocked() ? "blocked" : "unblocked";
            return "Room successfully " + status + ". " +
                    (request.getBlocked() ? "No new members can join." : "New members can now join.");

        } catch (Exception e) {
            throw new RuntimeException("Error updating room status: " + e.getMessage(), e);
        }
    }
}
