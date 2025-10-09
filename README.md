# Online Voting System

A secure and scalable online voting system built with Spring Boot, featuring room-based voting, phone number authentication, and comprehensive exception handling.

## 🚀 Features

### Core Features
- **Room-Based Voting**: Administrators can create voting rooms with multiple parties
- **Phone Number Authentication**: Secure login/registration using phone numbers (prevents duplicate signups)
- **Time-Controlled Voting**: Set voting start and end times for each room
- **Vote Tracking**: Prevents duplicate voting and tracks voter participation
- **Real-Time Results**: View current vote counts for each party

### Admin Features
- Create voting rooms with custom parties
- Set voting time windows
- **Block/Unblock Rooms**: Control room access to prevent new members from joining
- View room details and voting statistics
- Manage multiple rooms

### Voter Features
- Register and login with phone number
- Join multiple voting rooms
- Cast votes for parties
- View room details and voting times
- One vote per room guarantee

### Security Features
- **BCrypt Password Hashing**: Secure password storage
- **Phone Number Validation**: 10-15 digit numeric format
- **Room Ownership Verification**: Only room creators can block their rooms
- **Duplicate Vote Prevention**: Database-level tracking
- **Proper HTTP Status Codes**: Clear error communication

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.6
- **Database**: MySQL
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security Crypto (BCrypt)
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
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📡 API Endpoints

### Authentication APIs

#### Register
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
  "message": "Registration successful",
  "loginType": "VOTER",
  "userId": 1,
  "phoneNumber": "1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "phoneNumber": "1234567890",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful",
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
  "isActive": true,
  "isBlocked": false
}
```

#### Block/Unblock Room
```http
POST /api/admin/block-room
Content-Type: application/json

{
  "roomId": "a1b2c3d4",
  "adminId": 1,
  "blocked": true
}
```

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

#### Get Room Details
```http
GET /api/voter/room/{roomId}
```

**Response**: Same as Admin's Get Room Details

---

## 🔴 HTTP Status Codes

| Status Code | Meaning | Example Scenario |
|-------------|---------|------------------|
| **200** | OK | Successful operation |
| **400** | Bad Request | Invalid request data |
| **401** | Unauthorized | Invalid login credentials |
| **403** | Forbidden | Room blocked or unauthorized action |
| **404** | Not Found | Room/User not found |
| **409** | Conflict | Already voted or duplicate phone number |
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
│   ├── LoginRequest.java
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
│   ├── Administrator.java
│   ├── Login.java
│   ├── Room.java
│   ├── User.java
│   └── VoteRecord.java
├── repository/
│   ├── AdministratorRepository.java
│   ├── LoginRepository.java
│   ├── RoomRepository.java
│   ├── UserRepository.java
│   └── VoteRecordRepository.java
├── service/
│   ├── AuthService.java
│   └── RoomService.java
└── OnlineVotingSystemApplication.java
```

---

## 🗄️ Database Schema

### Tables

1. **logins** - Authentication credentials
2. **users** - Voter information
3. **administrators** - Admin information
4. **rooms** - Voting room details
5. **vote_records** - Vote tracking

### Key Relationships

- Each room belongs to one administrator
- Users can join multiple rooms
- Each user can vote once per room
- Vote records prevent duplicate voting

---

## 🧪 Testing the API

### Using cURL

**Register a Voter:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "9876543210",
    "password": "voter123",
    "loginType": "VOTER"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "9876543210",
    "password": "voter123"
  }'
```

**Create Room (Admin):**
```bash
curl -X POST http://localhost:8080/api/admin/create-room \
  -H "Content-Type: application/json" \
  -d '{
    "roomName": "Test Election",
    "adminId": 1,
    "partyNames": ["Party A", "Party B"],
    "votingStartTime": "2025-10-10T09:00:00",
    "votingEndTime": "2025-10-10T18:00:00"
  }'
```

### Using Postman

1. Import the API endpoints into Postman
2. Set base URL: `http://localhost:8080`
3. Test each endpoint with sample data

---

## 🔒 Security Considerations

- **Password Hashing**: All passwords are hashed using BCrypt
- **Phone Number Validation**: Only numeric phone numbers (10-15 digits) accepted
- **Duplicate Prevention**: Database constraints prevent duplicate phone numbers
- **Vote Integrity**: One vote per user per room enforced at database level
- **Room Ownership**: Only room creators can modify room settings

---

## 🐛 Common Issues & Solutions

### Issue: "Cannot resolve BCryptPasswordEncoder"
**Solution**: Add Spring Security Crypto dependency in `pom.xml` and reload Maven

### Issue: "Phone number already registered"
**Solution**: Each phone number can only be registered once. Use a different number or login with existing credentials

### Issue: "Voting has ended" returns 400 instead of 410
**Solution**: Ensure you're using the updated services with proper exception handling

### Issue: Database connection failed
**Solution**: Check MySQL is running and credentials in `application.properties` are correct

---

## 📝 Future Enhancements

- [ ] JWT token-based authentication
- [ ] Email/SMS notifications
- [ ] Real-time vote updates with WebSocket
- [ ] Admin dashboard with analytics
- [ ] Voter verification with OTP
- [ ] Export results to PDF/Excel
- [ ] Multi-language support
- [ ] Vote encryption

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
- All contributors and testers

---

**Built with ❤️ using Spring Boot**