# CPS406Project---Bug-Tracker
Intro to Software engineering class project 

Last update: 3-15-2026

Created the first method in Scrum to add a backlog item to the database.
Make sure the variables used are the same type as described in the database.

See the example below:
```java
public Long cretatePBI(String name, String des){
        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:BugTracker.db")) {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO product_backlog_items(name, description)"+
                    "VALUES (?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, des);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        }
        catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return null;

    }
```
