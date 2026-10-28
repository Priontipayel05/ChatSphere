# ChatSphere — Premium messaging refresh

ChatSphere is a Spring Boot + HTML/CSS/JavaScript real-time chat application using STOMP over WebSocket and a file-backed H2 database.

## Included
- Premium purple/indigo responsive chat interface
- Light/dark theme preference saved in the browser
- User display name, status message and avatar colour settings
- Database-backed public room creation and listing
- Emoji picker and reply previews
- Search for messages in the current room
- Typing indicators and best-effort online presence events
- Unread badges for inactive rooms
- Edit/delete your own messages (soft delete) with updates broadcast to room subscribers
- Persistent users and chat history using Spring Data JPA/H2
- Validation for profile, room, search, edit and chat inputs

## Run in VS Code on Windows
Open the extracted `ChatSphere` folder in VS Code, then open **Terminal → New Terminal**.

Check Java 17 or newer:
```powershell
java -version
```

Run tests:
```powershell
.\mvnw.cmd clean test
```

Start the server:
```powershell
.\mvnw.cmd spring-boot:run
```

Then open `http://localhost:8081/login.html`.

If PowerShell blocks the wrapper, choose a Command Prompt terminal and run `mvnw.cmd clean test`, then `mvnw.cmd spring-boot:run`.

## Database
H2 is file-backed at `./data/chatsphere`. Hibernate uses `ddl-auto=update`; back up the `data` folder before replacing project files.

H2 console: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:file:./data/chatsphere;DB_CLOSE_ON_EXIT=FALSE`
- User: `sa`
- Password: blank (default development setup)

## Notes
- The first run needs internet access for Maven dependencies; the browser also loads the STOMP JavaScript bundle from a CDN.
- This is still a development/academic project, not a production-secured deployment. Before public deployment, use HTTPS/WSS, restrict origins, add CSRF/rate limiting and stronger session security, and use a managed database with backups.
- Presence is event-based and best-effort; multiple simultaneous sessions for one account can affect online/offline accuracy.
- Search returns up to 100 matching results. Room history currently loads the latest 100 messages.


Branding: ChatSphere logo/favicon is at `src/main/resources/static/assets/chatsphere-logo.svg`. The login and chat pages reference it as `/assets/chatsphere-logo.svg`.
