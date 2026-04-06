# CPS406 Project - Bug Tracker System
 
A Java desktop application for tracking and managing software bugs across two development methodologies: **Scrum** and **Waterfall**. Built with Java Swing for the UI and DuckDB as the embedded database.
 
---
 
## Technologies Used
 
- **Java Swing** - Desktop GUI
- **DuckDB** - Embedded local database
- **JDBC** - Database connectivity
- **JUnit 5** - Unit testing
 
---
 
## How to Run
 
1. Make sure you have Java installed.
2. Add the DuckDB JDBC driver to your project dependencies.
3. Run `MainUI.java` - the database and tables are created automatically on first launch.
 
---
 
## Features
 
### Two Bug Methodologies
 
**Scrum Bugs**
- Linked to a Product Backlog Item (PBI)
- Each bug must belong to an existing or new PBI
- No phase assigned - progress is tracked via status
 
**Waterfall Bugs**
- Not linked to any PBI
- Assigned to a development phase: `REQUIREMENTS`, `DESIGN`, `IMPLEMENTATION`, `TESTING`, or `DEPLOYMENT`
 
---
 
### Bug Table (Main Screen)
 
The main screen displays all bugs in a single table with the following columns:
 
| Column | Description |
|---|---|
| ID | Unique bug identifier |
| PBI Title | Name of the linked PBI (Scrum only) |
| Phase | Development phase (Waterfall only) |
| Bug Title | Title of the bug |
| Description | Short description of the bug |
| Severity | CRITICAL / MAJOR / MINOR / TRIVIAL |
| Status | Current bug status |
| Created | Date the bug was created |
| Updated | Date the bug was last updated |
| Fast Track | Checkbox - ticked if bug is fast tracked |
| Resolved | Date the bug was resolved |
 
**Color coding by severity:**
- 🔴 CRITICAL - Red
- 🟠 MAJOR - Orange
- 🟡 MINOR - Yellow
- 🟢 TRIVIAL - Green
 
**Fast Track bugs always appear at the top of the table.**
 
---
 
### Buttons
 
| Button | Action |
|---|---|
| Create Bug | Opens a dialog to create a new Scrum or Waterfall bug |
| Add a Bug to Existing PBI | Adds a new Scrum bug to an already existing PBI |
| Refresh | Reloads the table from the database |
| Reload Application | Fully restarts the UI |
 
---
 
### Create Bug
 
- Choose between **Scrum** or **Waterfall**
- **Scrum:** Enter a new PBI name and description, then fill in bug details
- **Waterfall:** Select a phase and fill in bug details directly
 
---
 
### Add Bug to Existing PBI
 
- Scrum only
- Dropdown is populated with all existing PBI names from the database
- Inserts the bug directly linked to the selected PBI without creating a new one
 
---
 
### Update Bug
 
- Double-click any row in the table to open the update dialog
- Editable fields: Title, Description, Severity, Status, Fast Track
- Waterfall bugs also allow updating the Phase
- Setting status to `RESOLVED`, `CLOSED`, or `REJECTED` automatically sets the resolved date
 
---
 
## Bug Statuses
 
`NEW` → `PLANNED` → `IN_PROGRESS` → `RESOLVED` → `TESTED` → `CLOSED` / `REJECTED`
 
---
 
## Database Tables
 
**`bugs`** - stores all bugs (Scrum and Waterfall)  
**`product_backlog_items`** - stores PBIs linked to Scrum bugs  
 
The database file `BugTracker.db` is created automatically in the project directory on first run.
