# 🎯 Online Quiz Application

A full-featured Spring Boot online quiz platform with real-time capabilities, JWT authentication, MySQL database, and responsive Thymeleaf frontend.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![Render](https://img.shields.io/badge/Deployed%20on-Render-46a2f1)](https://render.com)

## 🚀 Live Demo

**🌐 Application URL**: [https://online-quiz-app.onrender.com](https://online-quiz-app.onrender.com)  
**📚 API Documentation**: [https://online-quiz-app.onrender.com/swagger-ui.html](https://online-quiz-app.onrender.com/swagger-ui.html)  
**🏥 Health Check**: [https://online-quiz-app.onrender.com/actuator/health](https://online-quiz-app.onrender.com/actuator/health)

> ⚠️ **Note**: On Render free tier, the app sleeps after 15 minutes of inactivity. First request may take 50 seconds to wake up.

## ✨ Features

### 🛡️ Authentication & Security
- JWT-based authentication & authorization
- Role-based access control (Admin, Teacher, Student)
- Password encryption with BCrypt
- 2-Factor Authentication (Google Authenticator)
- Session management with timeout

### 📊 Quiz Management
- Create, edit, and delete quizzes
- Multiple question types (MCQ, True/False, Short Answer)
- Difficulty levels (Easy, Medium, Hard)
- Category and tag-based organization
- Bulk import questions via CSV

### 🎮 Real-time Experience
- Live quiz sessions using WebSocket
- Real-time score updates
- Countdown timer for quizzes
- Instant result calculation
- Web notifications

### 👨‍💼 Admin Dashboard
- User management
- Quiz analytics and statistics
- Performance tracking
- Category management
- System monitoring

### 📱 Frontend
- Responsive design with Bootstrap 5
- Thymeleaf templates
- Mobile-friendly interface
- Interactive charts and graphs
- Toast notifications

### 🔧 Developer Features
- Swagger/OpenAPI documentation
- Comprehensive logging
- Health monitoring with Spring Actuator
- Unit and integration tests
- Docker containerization

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 3.2.0, Java 17 |
| **Frontend** | Thymeleaf, Bootstrap 5, JavaScript |
| **Database** | MySQL 8.0, Spring Data JPA |
| **Security** | Spring Security, JWT |
| **Real-time** | WebSocket, STOMP, SockJS |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, Mockito, H2 Database |
| **API Docs** | SpringDoc OpenAPI 3.0 |
| **Deployment** | Docker, Render |

## 📁 Project Structure
