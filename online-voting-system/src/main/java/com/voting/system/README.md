# Online Voting System

A secure and scalable online voting system built with Spring Boot, featuring room-based voting, phone number authentication, dual login types, and comprehensive exception handling.

## 🚀 Features

### Core Features
- **Room-Based Voting**: Administrators can create voting rooms with multiple parties
- **Phone Number Authentication**: Secure login/registration using phone numbers (10-15 digits)
- **Dual Login Types**: Same phone number can be used for BOTH voter and administrator accounts
- **Time-Controlled Voting**: Set voting start and end times for each room
- **Vote Tracking**: Prevents duplicate voting with database-level enforcement
- **Real-Time Results**: View current vote counts for each party
- **Integer Status Flags**: Uses 0 and 1 instead of boolean for better database compatibility

### Admin Features
- Create voting rooms with custom parties
- Set voting time windows
- **Block/Unblock Rooms**: Control room access (0 = open, 1 = blocked)
- View room details and voting statistics
- Manage multiple rooms
- Only room creators can block their own rooms

### Voter Features
- Register and login with phone number
- Join multiple voting rooms
- Cast votes for parties
- View room details and voting times
- One vote per room guarantee

### Security Features
- **BCrypt Password Hashing**: Secure password storage
- **Phone Number Validation**: 10-15 digit numeric format only
- **Dual Account Support**: Separate voter and admin accounts with same phone
- **Room Ownership Verification**: Only room creators can modify room settings
- **Duplicate Vote Prevention**: Database-level tracking with `vote_records` table
- **Proper HTTP Status Codes**: Clear error communication (410 for ended voting, 425 for not started, etc.)

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.6
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security Crypto (BCrypt)
- **JSON Processing**: Jackson
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd online-voting-system
```

### 2. Configure Database

Create a MySQL database:
```sql
CREATE DATABASE voting_system;
```

Update `src/main/resources/application.properties`:
```properties
spring.application.name=online-voting-system

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/voting_system
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot-run
```

The application will start on `http://localhost:8080`

## 📡 API Endpoints

### Authentication APIs

#### Register
**Important**: You can register the same phone number as both VOTER and ADMINISTRATOR separately!

```http
POST /api/auth/register
Content-Type: application/json

{
  "phoneNumber": "1234567890",
  "password": "password123",
  "loginType": "VOTER"  // or "ADMINISTRATOR"
}
```

**Response (200 OK):**
```json
{
  "message": "Registration successful as VOTER",
  "loginType": "VOTER",
  "userId": 1,
  "phoneNumber": "1234567890"
}
```

#### Login
**Important**: You must specify `loginType` to indicate which account to login to!

```http
POST /api/auth/login
Content-Type: application/json

{
  "phoneNumber": "1234567890",
  "password": "password123",
  "loginType": "VOTER"  // REQUIRED: "VOTER" or "ADMINISTRATOR"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful as VOTER",
  "loginType": "VOTER",
  "userId": 1,
  "phoneNumber": "1234567890"
}
```

---

### Admin APIs

#### Create Room
```http
POST /api/admin/create-room
Content-Type: application/json

{
  "roomName": "Presidential Election 2025",
  "adminId": 1,
  "partyNames": ["Party A", "Party B", "Party C"],
  "votingStartTime": "2025-10-10T09:00:00",
  "votingEndTime": "2025-10-10T18:00:00"
}
```

**Response (200 OK):**
```json
{
  "roomId": "a1b2c3d4",
  "roomName": "Presidential Election 2025",
  "message": "Room created successfully"
}
```

#### Get Room Details
```http
GET /api/admin/room/{roomId}
```

**Response (200 OK):**
```json
{
  "roomId": "a1b2c3d4",
  "roomName": "Presidential Election 2025",
  "totalRegistered": 150,
  "votingStartTime": "2025-10-10T09:00:00",
  "votingEndTime": "2025-10-10T18:00:00",
  "partyNames": ["Party A", "Party B", "Party C"],
  "currentVotes": {
    "Party A": 45,
    "Party B": 67,
    "Party C": 38
  },
  "isActive": 1,
  "isBlocked": 0
}
```

**Status Flags:**
- `isActive`: `0` = Inactive, `1` = Active
- `isBlocked`: `0` = Not Blocked (Open), `1` = Blocked (Closed)

#### Block/Unblock Room
Only the room creator can block/unblock their room!

```http
POST /api/admin/block-room
Content-Type: application/json

{
  "roomId": "a1b2c3d4",
  "adminId": 1,
  "blocked": 1
}
```

**Values:**
- `blocked: 0` = Unblock room (allow new members)
- `blocked: 1` = Block room (prevent new members from joining)

**Response (200 OK):**
```
"Room successfully blocked. No new members can join."
```

---

### Voter APIs

#### Join Room
```http
POST /api/voter/join-room
Content-Type: application/json

{
  "roomId": "a1b2c3d4",
  "userId": 2
}
```

**Response (200 OK):**
```
"Successfully joined room: Presidential Election 2025"
```

**What Happens:**
1. User is added to the room
2. Room's `total_registered` count increases
3. A `vote_record` is created with `has_voted = 0`

#### Cast Vote
```http
POST /api/voter/vote
Content-Type: application/json

{
  "roomId": "a1b2c3d4",
  "userId": 2,
  "partyName": "Party A"
}
```

**Response (200 OK):**
```json
{
  "message": "Vote cast successfully for Party A",
  "success": true
}
```

**What Happens:**
1. Vote count for the party increases by 1
2. `vote_record.has_voted` is set to `1`
3. `vote_record.voted_at` is set to current timestamp

#### Get Room Details
```http
GET /api/voter/room/{roomId}
```

**Response**: Same as Admin's Get Room Details

GET /api/voter/my-rooms/{userId}
Returns count of total rooms, active rooms, and completed rooms for a user.
Path Parameter:

userId (Long) - The user ID
**Response (200 OK):**
```json
{
  "userId": 1,
  "phoneNumber": "9876543210",
  "totalRooms": 5,
  "activeRooms": 2,
  "completedRooms": 3
}
```

**Example:**
```bash
curl -X GET http://localhost:8080/api/voter/my-rooms/1
```

---

### 2. Leave Room (Voter)

**POST** `/api/voter/leave-room`

Allows a voter to leave a room. **Cannot leave during active voting period.**

**Request Body:**
```json
{
  "roomId": "a1b2c3d4",
  "userId": 2
}
```

**Response (200 OK):**
```
"Successfully left room: Presidential Election 2025"
```

**Error Cases:**

**403 Forbidden - During Voting:**
```json
{
  "status": 403,
  "message": "Cannot leave room during active voting period",
  "timestamp": "2025-10-10T10:30:00"
}
```

**400 Bad Request - Not a Member:**
```json
{
  "status": 400,
  "message": "You are not a member of this room",
  "timestamp": "2025-10-10T10:30:00"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/voter/leave-room \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "a1b2c3d4",
    "userId": 2
  }'
```

**What Happens:**
1. User is removed from the room
2. Room's `total_registered` count decreases
3. User's `vote_record` is deleted
4. User's `rooms_json` is updated

**⚠️ Restrictions:**
- ❌ Cannot leave if voting is currently active (between start and end time)
- ✅ Can leave before voting starts
- ✅ Can leave after voting ends

---

## 🔴 Admin APIs

### 3. Remove User from Room (Admin)

**POST** `/api/admin/remove-user`

Admin can remove a specific user from their room. **Cannot remove during active voting period.**

**Request Body:**
```json
{
  "roomId": "a1b2c3d4",
  "adminId": 1,
  "userIdToRemove": 5
}
```

**Response (200 OK):**
```
"User successfully removed from room: Presidential Election 2025"
```

**Error Cases:**

**403 Forbidden - Not Room Owner:**
```json
{
  "status": 403,
  "message": "Only the room creator can remove users",
  "timestamp": "2025-10-10T10:30:00"
}
```

**403 Forbidden - During Voting:**
```json
{
  "status": 403,
  "message": "Cannot remove users during active voting period",
  "timestamp": "2025-10-10T10:30:00"
}
```

**400 Bad Request - User Not in Room:**
```json
{
  "status": 400,
  "message": "User is not a member of this room",
  "timestamp": "2025-10-10T10:30:00"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/remove-user \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "a1b2c3d4",
    "adminId": 1,
    "userIdToRemove": 5
  }'
```

**What Happens:**
1. User is removed from the room
2. Room's `total_registered` count decreases
3. User's `vote_record` is deleted
4. User's `rooms_json` is updated

**⚠️ Restrictions:**
- ❌ Cannot remove users if voting is currently active
- ❌ Only room creator can remove users
- ✅ Can remove before voting starts
- ✅ Can remove after voting ends

---

### 4. Delete Room (Admin)

**DELETE** `/api/admin/delete-room`

Admin can delete their room entirely. **Cannot delete during active voting period.**

**Request Body:**
```json
{
  "roomId": "a1b2c3d4",
  "adminId": 1
}
```

**Response (200 OK):**
```
"Room 'Presidential Election 2025' successfully deleted"
```

**Error Cases:**

**403 Forbidden - Not Room Owner:**
```json
{
  "status": 403,
  "message": "Only the room creator can delete the room",
  "timestamp": "2025-10-10T10:30:00"
}
```

**403 Forbidden - During Voting:**
```json
{
  "status": 403,
  "message": "Cannot delete room during active voting period",
  "timestamp": "2025-10-10T10:30:00"
}
```

**404 Not Found:**
```json
{
  "status": 404,
  "message": "Room not found with ID: a1b2c3d4",
  "timestamp": "2025-10-10T10:30:00"
}
```

**Example:**
```bash
curl -X DELETE http://localhost:8080/api/admin/delete-room \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "a1b2c3d4",
    "adminId": 1
  }'
```
## 🔴 HTTP Status Codes

| Status Code | Meaning | Example Scenario |
|-------------|---------|------------------|
| **200** | OK | Successful operation |
| **400** | Bad Request | Invalid request data or party name |
| **401** | Unauthorized | Invalid login credentials |
| **403** | Forbidden | Room blocked, not authorized, or room inactive |
| **404** | Not Found | Room/User/Vote record not found |
| **409** | Conflict | Already voted or duplicate phone+loginType |
| **410** | Gone | Voting has ended |
| **425** | Too Early | Voting hasn't started yet |
| **500** | Internal Server Error | Server-side error |

---

## 📂 Project Structure

```
src/main/java/com/voting/system/
├── controller/
│   ├── AdminController.java
│   ├── AuthController.java
│   └── VoterController.java
├── dto/
│   ├── BlockRoomRequest.java
│   ├── CreateRoomRequest.java
│   ├── CreateRoomResponse.java
│   ├── JoinRoomRequest.java
│   ├── LoginRequest.java (includes loginType)
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── RoomDetailsResponse.java
│   ├── VoteRequest.java
│   └── VoteResponse.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   ├── InvalidCredentialsException.java
│   ├── VotingException.java
│   ├── RoomAccessException.java
│   └── InvalidRequestException.java
├── model/
│   ├── Administrator.java (phone_number field)
│   ├── Login.java (composite unique: phone_number + login_type)
│   ├── Room.java (isActive, isBlocked as Integer)
│   ├── User.java (phone_number field)
│   └── VoteRecord.java (hasVoted as Integer)
├── repository/
│   ├── AdministratorRepository.java
│   ├── LoginRepository.java (findByPhoneNumberAndLoginType)
│   ├── RoomRepository.java
│   ├── UserRepository.java
│   └── VoteRecordRepository.java
├── service/
│   ├── AuthService.java (dual login type support)
│   └── RoomService.java (0/1 status flags)
└── OnlineVotingSystemApplication.java
```

---

## 🗄️ Database Schema

### Tables

1. **logins** - Authentication credentials (composite unique: phone_number + login_type)
2. **users** - Voter information
3. **administrators** - Admin information
4. **rooms** - Voting room details (with isActive and isBlocked as INT)
5. **vote_records** - Vote tracking (prevents duplicate voting)

### Key Relationships

- Each room belongs to one administrator
- Users can join multiple rooms
- Each user can vote once per room
- Vote records prevent duplicate voting
- Same phone number can have separate voter and admin accounts

### Status Flag Values

| Field | Value | Meaning |
|-------|-------|---------|
| `is_active` | 0 | Room is inactive |
| `is_active` | 1 | Room is active |
| `is_blocked` | 0 | Room is NOT blocked (open) |
| `is_blocked` | 1 | Room IS blocked (closed) |
| `has_voted` | 0 | User has NOT voted yet |
| `has_voted` | 1 | User HAS voted |

---

## 🧪 Testing the API

### Dual Login Type Example

**Register as Voter:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "9876543210",
    "password": "voter123",
    "loginType": "VOTER"
  }'
```

**Register as Admin (SAME PHONE):**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "9876543210",
    "password": "admin456",
    "loginType": "ADMINISTRATOR"
  }'
```

**Login as Voter:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "9876543210",
    "password": "voter123",
    "loginType": "VOTER"
  }'
```

**Login as Admin:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "9876543210",
    "password": "admin456",
    "loginType": "ADMINISTRATOR"
  }'
```

### Block Room Example

```bash
curl -X POST http://localhost:8080/api/admin/block-room \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "abc123",
    "adminId": 1,
    "blocked": 1
  }'
```

### Vote Example

```bash
curl -X POST http://localhost:8080/api/voter/vote \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "abc123",
    "userId": 2,
    "partyName": "Party A"
  }'
```

---

## 🔒 Security Considerations

- **Password Hashing**: All passwords hashed using BCrypt (10 rounds)
- **Phone Number Validation**: Only numeric, 10-15 digits accepted
- **Duplicate Prevention**: Database constraints prevent duplicate phone+loginType
- **Vote Integrity**: One vote per user per room enforced at database level
- **Room Ownership**: Only room creators can block/unblock their rooms
- **Separate Accounts**: Voter and admin accounts are independent even with same phone

---

## 🐛 Common Issues & Solutions

### Issue: "Vote record not found" (404)
**Cause**: User joined room but `vote_record` wasn't created

**Solution 1 - SQL Fix:**
```sql
INSERT INTO vote_records (user_id, room_id, has_voted, voted_at) 
VALUES (1, 'room_id_here', 0, NOW());
```

**Solution 2 - Rejoin Room:**
1. Clear user's `rooms_json` in database
2. Call `/api/voter/join-room` again

### Issue: "Phone number already registered as VOTER" (409)
**Cause**: Trying to register same phone+loginType twice

**Solution**:
- Use `/api/auth/login` instead
- OR register with different `loginType` (ADMINISTRATOR)

### Issue: "Login type must be specified" (400)
**Cause**: Forgot to include `loginType` in login request

**Solution**: Add `"loginType": "VOTER"` or `"loginType": "ADMINISTRATOR"`

### Issue: "Voting has ended" returns 410 (not 400)
**This is correct!** Status code 410 Gone means the voting period has permanently ended.

### Issue: Room blocked, can't join (403)
**Cause**: Admin set `isBlocked = 1`

**Solution**: Admin must unblock room using `/api/admin/block-room` with `"blocked": 0`

---

## 📝 Key Changes from Previous Version

### ✅ Phone Number Authentication
- Changed from username to phone number (10-15 digits)
- Prevents duplicate signups

### ✅ Dual Login Type Support
- Same phone can register as BOTH voter and admin
- Must specify `loginType` in login requests
- Separate passwords for each role

### ✅ Integer Status Flags
- `isActive`: 0/1 instead of false/true
- `isBlocked`: 0/1 instead of false/true
- `hasVoted`: 0/1 instead of false/true

### ✅ Room Blocking Feature
- Admins can block rooms to prevent new members
- Only room creators can block their own rooms
- Blocked rooms return 403 Forbidden on join attempts

### ✅ Proper HTTP Status Codes
- 410 Gone for ended voting (not 400)
- 425 Too Early for voting not started
- 409 Conflict for already voted
- 403 Forbidden for blocked rooms

### ✅ Comprehensive Exception Handling
- Global exception handler
- Custom exceptions for each scenario
- Detailed error messages with timestamps

---

## 📊 Database Field Reference

### Room Status Values
```sql
-- Active room that's open for joining
is_active = 1, is_blocked = 0

-- Active room that's blocked
is_active = 1, is_blocked = 1

-- Inactive room (voting ended or cancelled)
is_active = 0, is_blocked = 0
```

### Vote Record Status
```sql
-- User joined, hasn't voted yet
has_voted = 0, voted_at = NULL

-- User has voted
has_voted = 1, voted_at = '2025-10-09 10:30:00'
```

---

## 🎯 Future Enhancements

- [ ] JWT token-based authentication
- [ ] OTP verification for phone numbers
- [ ] Email/SMS notifications
- [ ] Real-time vote updates with WebSocket
- [ ] Admin dashboard with analytics
- [ ] Export results to PDF/Excel
- [ ] Multi-language support
- [ ] Vote encryption
- [ ] Scheduled automatic room activation/deactivation

---

## 👥 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📧 Contact

For questions or support, please contact: [your-email@example.com]

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- MySQL for reliable database management
- Jackson for JSON processing
- Spring Security for BCrypt implementation
- All contributors and testers

---

**Built with ❤️ using Spring Boot**

**Version**: 2.0.0 (Updated with dual login types and integer status flags)