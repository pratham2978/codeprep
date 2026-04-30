package com.example.demo.config;

import com.example.demo.model.BehavioralQuestion;
import com.example.demo.model.StudyTopic;
import com.example.demo.repository.BehavioralQuestionRepository;
import com.example.demo.repository.StudyTopicRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StudyDataLoader {

    @Bean
    CommandLineRunner loadStudyData(StudyTopicRepository topicRepo, BehavioralQuestionRepository behavioralRepo) {
        return args -> {

            // ---- STUDY TOPICS ----
            if (topicRepo.count() == 0) {
                topicRepo.saveAll(List.of(
                        // Core CS - OS
                        new StudyTopic("core-cs", "OS", "Process vs Thread",
                                "A **Process** is an independent program in execution with its own memory space. A **Thread** is a lightweight unit of execution within a process, sharing the same memory space.\n\nKey differences:\n- Processes are isolated; threads share memory\n- Context switching is faster for threads\n- Creating a process is expensive; threads are cheap\n- A crashed thread can crash the entire process",
                                "class ThreadExample extends Thread {\n    public void run() {\n        System.out.println(\"Thread ID: \" + Thread.currentThread().getId());\n    }\n    public static void main(String[] args) {\n        ThreadExample t1 = new ThreadExample();\n        t1.start();\n    }\n}",
                                "Beginner", "OS,Process,Thread,Concurrency"),

                        new StudyTopic("core-cs", "OS", "Deadlock & Banker's Algorithm",
                                "A **Deadlock** occurs when a set of processes are blocked, each waiting for a resource held by another in the set.\n\n**4 Necessary Conditions (Coffman):**\n1. Mutual Exclusion\n2. Hold and Wait\n3. No Preemption\n4. Circular Wait\n\n**Banker's Algorithm** is a deadlock avoidance algorithm that simulates resource allocation and checks if the system remains in a safe state before granting requests.",
                                null, "Intermediate", "OS,Deadlock,Banker,Safety"),

                        new StudyTopic("core-cs", "OS", "Paging & Segmentation",
                                "**Paging** divides physical memory into fixed-size frames and virtual memory into pages. It eliminates external fragmentation but causes internal fragmentation.\n\n**Segmentation** divides memory into variable-size logical units (segments) like code, stack, heap. Eliminates internal fragmentation but causes external fragmentation.\n\n**Page Fault**: Occurs when a referenced page is not in RAM. OS must load it from disk (costly I/O operation).",
                                null, "Intermediate", "OS,Memory,Paging,Segmentation"),

                        // Core CS - DBMS
                        new StudyTopic("core-cs", "DBMS", "Normalization (1NF to BCNF)",
                                "Normalization eliminates data redundancy and ensures data integrity.\n\n**1NF**: Atomic values, no repeating groups.\n**2NF**: 1NF + No partial dependency (all non-key attributes depend on the full primary key).\n**3NF**: 2NF + No transitive dependency.\n**BCNF**: 3NF + For every functional dependency X → Y, X must be a super key.\n\n**Denormalization** is sometimes intentional for read performance.",
                                null, "Intermediate", "DBMS,Normalization,1NF,2NF,3NF,BCNF"),

                        new StudyTopic("core-cs", "DBMS", "Indexing & B+ Trees",
                                "An **Index** is a data structure that speeds up data retrieval at the cost of additional storage and slower writes.\n\n**B+ Tree** is the standard indexing structure in databases (MySQL InnoDB):\n- All data stored in leaf nodes\n- Leaf nodes are linked for range queries\n- Balanced tree gives O(log n) search\n- Order m means each node has at most m children\n\n**Composite Index**: Index on multiple columns. Order matters!",
                                null, "Advanced", "DBMS,Index,B+Tree,Performance"),

                        new StudyTopic("core-cs", "CN", "TCP vs UDP",
                                "**TCP (Transmission Control Protocol)**:\n- Connection-oriented (3-way handshake: SYN, SYN-ACK, ACK)\n- Reliable delivery with acknowledgement and retransmission\n- Flow control and congestion control\n- Used for: HTTP, SMTP, FTP\n\n**UDP (User Datagram Protocol)**:\n- Connectionless, no handshake\n- No delivery guarantee — fire and forget\n- Much faster and lower overhead\n- Used for: DNS, Video Streaming, Online Gaming",
                                null, "Beginner", "CN,TCP,UDP,Networking"),

                        // System Design
                        new StudyTopic("system-design", "HLD", "URL Shortener (like bit.ly)",
                                "**Core Requirements:**\n- Shorten a long URL → short code (6-8 chars)\n- Redirect short URL → original URL\n- Handle 100M+ URLs, 10B+ reads/day\n\n**Design:**\n1. API: POST /shorten, GET /{code}\n2. Encoding: Base62 (a-z, A-Z, 0-9) on a unique ID from DB\n3. Database: MySQL for URL mapping + Redis cache for hot URLs\n4. Horizontal scaling with load balancer\n5. Consistent hashing for cache distribution\n\n**Bottleneck**: DB writes → use pre-generated ID ranges per server",
                                null, "Intermediate", "SystemDesign,URL,Cache,Database"),

                        new StudyTopic("system-design", "HLD", "Design WhatsApp / Messaging System",
                                "**Requirements:** 1-1 and group messaging, online status, message delivery receipts.\n\n**Key Components:**\n1. **WebSocket Gateway**: Persistent connections for real-time delivery\n2. **Message Queue (Kafka)**: Decouple message sending from delivery\n3. **Message DB**: Cassandra (write-heavy, time-series friendly)\n4. **User Service**: MySQL for user profiles\n5. **Presence Service**: Redis TTL-based online status\n\n**Challenges:** Group messaging fan-out, offline message storage, message ordering with distributed clocks.",
                                null, "Advanced", "SystemDesign,Messaging,WebSocket,Kafka"),

                        new StudyTopic("system-design", "LLD", "Design a Parking Lot System",
                                "**Entities:** ParkingLot, ParkingFloor, ParkingSpot (Compact/Large/Handicapped), Vehicle (Car/Truck/Bike), Ticket, Payment.\n\n**Key Classes:**\n- ParkingLot (singleton) manages floors\n- ParkingFloor has a map of SpotType → available count\n- Ticket issued on entry with timestamp\n- Fee calculated on exit based on vehicle type and duration\n\n**Design Patterns:** Singleton (ParkingLot), Strategy (Fee calculation), Factory (Spot creation)",
                                "class ParkingSpot {\n    private SpotType type;\n    private boolean isFree = true;\n    public boolean isFree() { return isFree; }\n    public void assignVehicle(Vehicle vehicle) { isFree = false; }\n    public void removeVehicle() { isFree = true; }\n}",
                                "Intermediate", "LLD,OOP,ParkingLot,Design"),

                        // OOP
                        new StudyTopic("oops", "Principles", "SOLID Principles",
                                "**S - Single Responsibility**: A class should have only one reason to change.\n**O - Open/Closed**: Open for extension, closed for modification.\n**L - Liskov Substitution**: Subclasses must be substitutable for their base class.\n**I - Interface Segregation**: Many specific interfaces > one general interface.\n**D - Dependency Inversion**: Depend on abstractions, not concretions.\n\nViolating SOLID leads to fragile, rigid code that's hard to test and maintain.",
                                "// Bad: Violates SRP\nclass Report {\n    void generate() { ... }\n    void print() { ... }  // Should be separate!\n}\n\n// Good: Separate concerns\nclass Report { void generate() { ... } }\nclass ReportPrinter { void print(Report r) { ... } }",
                                "Intermediate", "OOP,SOLID,Design,Principles"),

                        new StudyTopic("oops", "Design Patterns", "Factory & Singleton Pattern",
                                "**Factory Pattern**: Defines an interface for creating objects but lets subclasses decide which class to instantiate.\n**Singleton Pattern**: Ensures only one instance of a class exists throughout the application lifecycle.\n\nSingleton use cases: Database connection pool, Logger, Thread pool\nFactory use cases: UI framework components, Payment gateway creation",
                                "// Singleton (Thread-safe)\nclass DatabaseConnection {\n    private static volatile DatabaseConnection instance;\n    private DatabaseConnection() {}\n    public static DatabaseConnection getInstance() {\n        if (instance == null) {\n            synchronized (DatabaseConnection.class) {\n                if (instance == null) instance = new DatabaseConnection();\n            }\n        }\n        return instance;\n    }\n}",
                                "Advanced", "OOP,Patterns,Factory,Singleton")));
            }

            // ---- BEHAVIORAL QUESTIONS ----
            if (behavioralRepo.count() == 0) {
                behavioralRepo.saveAll(List.of(
                        new BehavioralQuestion("achievement",
                                "Tell me about a time you delivered a project under a tight deadline.",
                                "S: During my 3rd year project, our team had 2 weeks to build a full-stack app. T: I was responsible for both the backend API and deployment. A: I broke the work into daily milestones, worked extra hours on critical path items, used CI/CD to automate deployments. R: We delivered on time with all features working. The professor awarded us Best Project.",
                                "Common", "deadline,milestone,deliver,teamwork"),

                        new BehavioralQuestion("conflict",
                                "Describe a situation where you disagreed with your team lead. How did you handle it?",
                                "S: My team lead wanted to use a NoSQL database for a strongly relational dataset. T: I believed this would create maintainability issues long-term. A: I prepared a comparison document showing tradeoffs, requested a 30-minute technical meeting, and presented my case with data. R: We had a productive discussion and agreed on a hybrid approach. My credibility on technical topics increased.",
                                "Tough", "conflict,communication,data,respect"),

                        new BehavioralQuestion("failure",
                                "Tell me about a time you failed. What did you learn?",
                                "S: I once submitted a pull request with a bug that made it to production causing a 2-hour outage. T: I had skipped unit testing due to time pressure. A: I immediately helped with the rollback, wrote a post-mortem, and proposed adding mandatory test coverage gates to CI pipeline. R: The team adopted the gates. I never skipped tests again and the codebase quality improved significantly.",
                                "Common", "failure,learn,accountability,improvement"),

                        new BehavioralQuestion("leadership",
                                "Give an example of when you showed leadership without formal authority.",
                                "S: During a hackathon, our team had no defined leader and was losing direction. T: With 6 hours left, we had no working prototype. A: I called a quick realignment meeting, re-scoped the MVP to 3 core features, assigned clear ownership to each team member, and set 1-hour check-ins. R: We finished a working demo with 45 minutes to spare and won 2nd place.",
                                "Common", "leadership,initiative,teamwork,organization"),

                        new BehavioralQuestion("teamwork",
                                "Tell me about a time you had to work with a difficult team member.",
                                "S: A teammate in my final year project consistently missed deadlines and disrupted flow. T: The project grade depended on all of us. A: I had a private, empathetic conversation to understand his blockers. Turns out he was struggling with the tech stack. I paired with him for 2 sessions to get him up to speed. R: He became one of the most consistent contributors in the final month.",
                                "Tough", "empathy,collaboration,communication,patience"),

                        new BehavioralQuestion("achievement",
                                "Tell me about your most technically challenging project.",
                                "S: Built a real-time collaborative code editor for 50+ concurrent users. T: Had to handle conflicts when multiple users edit the same line. A: Implemented Operational Transformation (OT) algorithm, used WebSockets for real-time sync, and Redis Pub/Sub for cross-server message broadcasting. R: System handled 60 concurrent users with sub-100ms sync latency in testing.",
                                "Senior", "technical,architecture,scale,problem-solving"),

                        new BehavioralQuestion("leadership",
                                "Describe a time you had to convince others to change direction on a project.",
                                "S: Our team was building a monolith but traffic projections showed we would need to scale in 6 months. T: I needed to convince senior devs who were resistant to microservices. A: I ran a proof-of-concept, documented the migration path in phases, and showed cost projections. I presented it at the next sprint review. R: The team voted to proceed. The phased migration was completed without any production downtime.",
                                "Senior", "influence,communication,technical vision,change management")));
            }
        };
    }
}
