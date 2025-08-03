# 🏥 Medicare - Online Doctor Clinic

[![Deployment Status](https://img.shields.io/badge/Deployment-Live-brightgreen.svg?style=for-the-badge)](https://online-doctor-clinic.onrender.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg?style=flat&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg?style=flat&logo=java)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg?style=flat&logo=postgresql)](https://www.postgresql.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green.svg?style=flat&logo=thymeleaf)](https://www.thymeleaf.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.0-blue.svg?style=flat&logo=tailwind-css)](https://tailwindcss.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat)](LICENSE)

> **A comprehensive, full-stack healthcare platform enabling seamless doctor-patient interactions through virtual consultations, appointment management, and secure payment processing.**

---

## 🌟 Overview

**Medicare** is a modern, server-rendered web application that revolutionizes healthcare accessibility by providing a complete digital clinic experience. Built with Spring Boot and Thymeleaf, this monolithic application delivers a robust, secure, and user-friendly platform for healthcare management.

### 🎯 Real-World Use Cases

- **Virtual Consultations** - Enable remote medical consultations with video conferencing using google meet
- **Appointment Scheduling** - Intelligent booking system with availability management
- **Prescription Management** - Digital prescription generation and patient history tracking
- **Secure Payments** - Integrated Razorpay payment gateway for consultation fees
- **Multi-Role Dashboard** - Dedicated interfaces for patients, doctors, and administrators
- **Document Management** - Cloudinary-powered secure upload for medical documents and reports

---

## ✨ Key Features

### 👥 **Multi-Role Authentication System**
- **Patients**: Book appointments, view prescriptions, manage medical history
- **Doctors**: Manage schedules, conduct consultations, generate prescriptions
- **Admins**: System oversight, doctor verification, platform analytics

### 💊 **Healthcare Management**
- Real-time appointment booking with availability checks
- Digital prescription generation and management
- Patient medical history and document storage
- Doctor rating and review system

### 💳 **Payment Integration**
- Secure Razorpay payment gateway integration
- Consultation fee processing
- Payment history and invoice generation

### 🔒 **Security & Compliance**
- Spring Security implementation with role-based access control
- Secure file upload via Cloudinary CDN
- HIPAA-compliant data handling practices
- Environment-based configuration management

---

## 🚀 Live Demo

🌐 **[View Live Application](https://online-doctor-clinic.onrender.com/)**

---

## 🛠️ Tech Stack

<details>
<summary><strong>🔧 Backend Technologies</strong></summary>

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 | Core Programming Language |
| **Spring Boot** | 3.5.4 | Application Framework |
| **Spring Security** | 6.x | Authentication & Authorization |
| **Spring Data JPA** | 3.x | Data Persistence Layer |
| **Thymeleaf** | 3.1 | Server-Side Template Engine |
| **PostgreSQL** | 14+ | Primary Database |
| **Maven** | 3.8+ | Dependency Management |

</details>

<details>
<summary><strong>🎨 Frontend Technologies</strong></summary>

| Technology | Purpose |
|------------|---------|
| **Thymeleaf** | Server-Side Rendering (SSR) |
| **Tailwind CSS** | Utility-First CSS Framework |
| **HTML5/CSS3** | Semantic Markup & Styling |
| **JavaScript** | Client-Side Interactivity |

</details>

<details>
<summary><strong>🔌 Third-Party Integrations</strong></summary>

| Service | Version | Purpose |
|---------|---------|---------|
| **Razorpay** | 1.4.4 | Payment Gateway |
| **Cloudinary** | 1.39.0 | Media Upload & CDN |
| **Neon Database** | - | PostgreSQL Cloud Hosting |

</details>

---

## 📦 Prerequisites

Ensure you have the following installed on your system:

- **Java Development Kit (JDK)** `17+` - [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Maven** `3.8+` - [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL** `14+` - [Download](https://www.postgresql.org/download/)
- **Git** - [Download](https://git-scm.com/downloads)
- **Bash Shell** (Windows users: Git Bash or WSL)

### 🔐 Required Service Accounts

- **Neon Database** - [Sign up](https://neon.tech/) for PostgreSQL hosting
- **Razorpay** - [Create account](https://razorpay.com/) for payment processing
- **Cloudinary** - [Register](https://cloudinary.com/) for media management

---

## 🚀 Quick Start

### 1️⃣ **Clone the Repository**

```bash
git clone https://github.com/shibam-pandit/online-doctor-clinic
cd online-doctor-clinic
```

### 2️⃣ **Environment Configuration**

Create a `.env` file in the project root:

```env
# Database Configuration
DB_URL=jdbc:postgresql://your-neon-endpoint:5432/neondb?sslmode=require&channelBinding=require
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Payment Gateway
RAZORPAY_DUMMY_KEY=rzp_test_your_key_here

# Cloud Storage
CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name
```

> 📝 **Note**: The `.env` file contains sensitive credentials. Never commit this to version control.

### 3️⃣ **Database Setup**

The application uses **automatic DDL** via Spring Data JPA. Tables will be created automatically on first run.

### 4️⃣ **Build and Run**

#### Option A: Using the Provided Script (Recommended)
```bash
# Make the script executable
chmod +x start.sh

# Run the application with environment variables loaded
./start.sh
```

#### Option B: Manual Execution
```bash
# Load environment variables manually
export $(grep -v '^#' .env | xargs)

# Run the application
./mvnw spring-boot:run
```

### 5️⃣ **Access the Application**

🌐 Open your browser and navigate to: **http://localhost:8080**

---

## 🗂️ Project Structure

```
online-doctor-clinic/
├── 📄 .env                          # Environment variables
├── 📄 Dockerfile                    # Container configuration
├── 📄 pom.xml                       # Maven dependencies
├── 📄 start.sh                      # Application startup script
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/shibam/clinic/
│   │   │   ├── 📁 Controllers/       # MVC Controllers
│   │   │   │   ├── 🎯 AdminController.java
│   │   │   │   ├── 🔐 AuthController.java
│   │   │   │   ├── 👨‍⚕️ DoctorController.java
│   │   │   │   ├── 🏠 HomeController.java
│   │   │   │   └── 👤 PatientController.java
│   │   │   ├── 📁 Models/            # JPA Entities
│   │   │   ├── 📁 Repositories/      # Data Access Layer
│   │   │   ├── 📁 Services/          # Business Logic
│   │   │   └── 📁 Config/            # Security & Configuration
│   │   └── 📁 resources/
│   │       ├── 📄 application.properties  # Spring Configuration
│   │       ├── 📁 static/            # CSS, JS, Images
│   │       └── 📁 templates/         # Thymeleaf Templates
│   │           ├── 📁 admin/         # Admin Dashboard Views
│   │           ├── 📁 auth/          # Authentication Pages
│   │           ├── 📁 doctor/        # Doctor Dashboard Views
│   │           ├── 📁 patient/       # Patient Dashboard Views
│   │           └── 📁 fragments/     # Reusable Components
│   └── 📁 test/                      # Unit & Integration Tests
└── 📁 target/                        # Build Output
```

---

## 🛣️ Controller Routes

<details>
<summary><strong>🏠 Home Controller - Public Routes</strong></summary>

| Method | Route | Description |
|--------|-------|-------------|
| `GET` | `/` | Landing page redirect |
| `GET` | `/home` | Application homepage |

</details>

<details>
<summary><strong>🔐 Auth Controller - Authentication</strong></summary>

| Method | Route | Description |
|--------|-------|-------------|
| `GET` | `/auth/login` | User login page |
| `POST` | `/auth/login` | Process login credentials |
| `GET` | `/auth/register` | User registration page |
| `POST` | `/auth/register` | Process user registration |
| `GET` | `/auth/logout` | User logout |

</details>

<details>
<summary><strong>👤 Patient Controller - Patient Dashboard</strong></summary>

| Method | Route | Description |
|--------|-------|-------------|
| `GET` | `/patient/` | Patient dashboard home |
| `GET` | `/patient/book-appointment` | Browse available doctors |
| `GET` | `/patient/book-slot/{doctorId}` | Select appointment slot |
| `POST` | `/patient/confirm-appointment` | Confirm & process payment |
| `GET` | `/patient/bookingSuccess` | Booking confirmation page |
| `GET` | `/patient/appointments` | View appointment history |
| `POST` | `/patient/cancel-appointment/{id}` | Cancel appointment |
| `GET` | `/patient/prescriptions` | View digital prescriptions |
| `GET` | `/patient/settings` | Patient profile settings |
| `POST` | `/patient/rate-appointment` | Rate completed consultation |

</details>

<details>
<summary><strong>👨‍⚕️ Doctor Controller - Doctor Dashboard</strong></summary>

| Method | Route | Description |
|--------|-------|-------------|
| `GET` | `/doctor/` | Doctor dashboard home |
| `GET` | `/doctor/wait` | Approval waiting page |
| `GET` | `/doctor/appointments` | Manage appointments |
| `POST` | `/doctor/appointments/add-meeting-link/{id}` | Add consultation link |
| `POST` | `/doctor/appointments/complete/{id}` | Mark appointment as completed |
| `GET` | `/doctor/availability` | Manage schedule & availability |
| `POST` | `/doctor/set-availability` | Create new availability slots |
| `POST` | `/doctor/cancel-slot/{id}` | Cancel availability slot |
| `GET` | `/doctor/patients` | View patient list & summaries |
| `GET` | `/doctor/earnings` | View earnings & analytics |
| `GET` | `/doctor/settings` | Doctor profile settings |
| `POST` | `/doctor/write-prescription` | Write prescription for appointment |

</details>

<details>
<summary><strong>⚙️ Admin Controller - System Administration</strong></summary>

| Method | Route | Description |
|--------|-------|-------------|
| `GET` | `/admin/` | Admin dashboard |
| `GET` | `/admin/doctors` | Manage doctor approvals |
| `POST` | `/admin/approve-doctor/{id}` | Approve doctor registration |
| `GET` | `/admin/analytics` | Platform analytics |
| `GET` | `/admin/settings` | System configuration |

</details>

---

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PatientControllerTest

# Generate test coverage report
./mvnw jacoco:report
```

---

## 🐳 Docker Deployment

```bash
# Build Docker image
docker build -t medicare-clinic .

# Run container
docker run -p 8080:8080 --env-file .env medicare-clinic
```

---

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

### 🔄 **Development Workflow**

1. **Fork** the repository
2. **Clone** your fork: `git clone https://github.com/shibam-pandit/online-doctor-clinic`
3. **Create** a feature branch: `git checkout -b feature/amazing-feature`
4. **Commit** your changes: `git commit -m 'Add amazing feature'`
5. **Push** to the branch: `git push origin feature/amazing-feature`
6. **Open** a Pull Request

### 📋 **Contribution Guidelines**

- Follow **Java coding standards** and **Spring Boot best practices**
- Write **comprehensive unit tests** for new features
- Update **documentation** for any API changes
- Ensure **responsive design** compatibility
- Test **cross-browser compatibility**

### 🐛 **Bug Reports**

Please use the [issue tracker](https://github.com/shibam-pandit/online-doctor-clinic/issues) to report bugs. Include:
- Steps to reproduce
- Expected vs actual behavior
- Screenshots (if applicable)
- Environment details

---

## 🖼️ Screenshots

<details>
<summary><strong>📱 Application Screenshots</strong></summary>

**Screenshots:**
- 🏠 Homepage & Landing

  <img width="1353" height="642" alt="image" src="https://github.com/user-attachments/assets/49ec33ae-7ff7-4da2-963a-8b953663920c" />

- 👤 Patient Dashboard

  <img width="1093" height="519" alt="image" src="https://github.com/user-attachments/assets/785fe4ac-c1aa-4625-9c32-9aeaa5bb6d65" />

- 👨‍⚕️ Doctor Dashboard

  <img width="1093" height="518" alt="image" src="https://github.com/user-attachments/assets/4534299a-52b3-48a7-aa4b-01f0cfdd1852" />

- 📅 Appointment Booking Flow

  <img width="1093" height="440" alt="image" src="https://github.com/user-attachments/assets/95e8ae2b-790e-4b0e-82ac-89c572bc7ead" />

- 💳 Payment Processing

  <img width="1093" height="516" alt="image" src="https://github.com/user-attachments/assets/df150dd6-94cb-426a-94c0-58178bf6ddc9" />

- ⚙️ Admin Panel

  <img width="1093" height="561" alt="image" src="https://github.com/user-attachments/assets/6563a5b2-8490-481c-b271-5eb57b2929dc" />


</details>

---

## ⚖️ License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### 📄 **License Summary**
- ✅ Commercial use allowed
- ✅ Modification allowed
- ✅ Distribution allowed
- ✅ Private use allowed
- ❌ No liability
- ❌ No warranty

---

## 🙏 Acknowledgments

- **Spring Boot Team** - For the excellent framework
- **Thymeleaf** - For powerful server-side templating
- **Tailwind CSS** - For utility-first CSS framework
- **Razorpay** - For seamless payment integration
- **Cloudinary** - For reliable media management
- **Neon Database** - For PostgreSQL cloud hosting
- **Healthcare Community** - For inspiration and requirements

---

## 📞 Support & Contact

- **Developer**: [Shibam Pandit](https://github.com/shibam-pandit)
- **Email**: shibampandit31@gmail.com
- **Project Repository**: [online-doctor-clinic](https://github.com/shibam-pandit/online-doctor-clinic)
- **Issues**: [Report a Bug](https://github.com/shibam-pandit/online-doctor-clinic/issues)

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

[![GitHub stars](https://img.shields.io/github/stars/shibam-pandit/online-doctor-clinic.svg?style=social&label=Star)](https://github.com/shibam-pandit/online-doctor-clinic)
[![GitHub forks](https://img.shields.io/github/forks/shibam-pandit/online-doctor-clinic.svg?style=social&label=Fork)](https://github.com/shibam-pandit/online-doctor-clinic/fork)

---

*Built with ❤️ for healthcare accessibility*

</div>
