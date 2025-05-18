# Admin Dashboard

## Purpose
Provide administrators with an overview of system activity and quick access to management functions.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM                 Admin ▼ | 🔔 |
+------------------------------------------------------+
|                      |                               |
| DASHBOARD            |  Welcome, Admin!              |
| PENDING APPROVALS    |                               |
| ALL EVENTS           |  +---------------------------+|
| REGISTERED USERS     |  |        QUICK STATS        ||
| EVENT REPORTS        |  +---------------------------+|
| SYSTEM SETTINGS      |  |                           ||
|                      |  | +-------+  +-------+      ||
| LOGOUT               |  | |  12   |  |   8   |      ||
|                      |  | |Pending|  |Upcoming|      ||
|                      |  | |Approva|  | Events |      ||
|                      |  | +-------+  +-------+      ||
|                      |  |                           ||
|                      |  | +-------+  +-------+      ||
|                      |  | |  245  |  |   42  |      ||
|                      |  | |Regist.|  | Total |      ||
|                      |  | | Users |  | Events|      ||
|                      |  | +-------+  +-------+      ||
|                      |  +---------------------------+|
|                      |                               |
|                      |  +---------------------------+|
|                      |  |     PENDING APPROVALS     ||
|                      |  +---------------------------+|
|                      |  |                           ||
|                      |  | • Tech Conference 2024    ||
|                      |  |   by org1@example.com     ||
|                      |  |   [REVIEW]                ||
|                      |  |                           ||
|                      |  | • Workshop on AI          ||
|                      |  |   by org2@example.com     ||
|                      |  |   [REVIEW]                ||
|                      |  |                           ||
|                      |  | • Career Fair             ||
|                      |  |   by org3@example.com     ||
|                      |  |   [REVIEW]                ||
|                      |  |                           ||
|                      |  | [VIEW ALL PENDING]        ||
|                      |  +---------------------------+|
|                      |                               |
|                      |  +---------------------------+|
|                      |  |      RECENT ACTIVITY      ||
|                      |  +---------------------------+|
|                      |  |                           ||
|                      |  | • John Doe registered for ||
|                      |  |   AI Workshop             ||
|                      |  |   2 minutes ago           ||
|                      |  |                           ||
|                      |  | • Sarah Smith created     ||
|                      |  |   Music Festival event    ||
|                      |  |   15 minutes ago          ||
|                      |  |                           ||
|                      |  | • Mike Johnson cancelled  ||
|                      |  |   registration for Career ||
|                      |  |   Fair                    ||
|                      |  |   1 hour ago              ||
|                      |  |                           ||
|                      |  | [VIEW ALL ACTIVITY]       ||
|                      |  +---------------------------+|
|                      |                               |
+------------------------------------------------------+
```

## Elements

1. **Header**
   - System name
   - Admin profile dropdown
   - Notifications icon

2. **Sidebar Navigation**
   - Dashboard (current view)
   - Pending Approvals
   - All Events
   - Registered Users
   - Event Reports
   - System Settings
   - Logout

3. **Welcome Section**
   - Personalized greeting

4. **Quick Stats Section**
   - Key metrics in card format:
     - Pending approvals count
     - Upcoming events count
     - Registered users count
     - Total events count

5. **Pending Approvals Section**
   - List of events pending approval
   - Event name and organizer email
   - Review button for each
   - View All link

6. **Recent Activity Section**
   - Timeline of recent system activities
   - User actions with timestamps
   - View All link

## Behavior

- Sidebar highlights current section
- Stats cards are clickable to navigate to relevant sections
- Review buttons open the event approval screen
- View All links navigate to the full list views
- Recent activity updates in real-time

## Visual Style

- Clean, card-based design with subtle shadows
- Sidebar with dark background and light text
- Stats cards with colorful accents
- Clear visual hierarchy with section headers
- Consistent color scheme with admin-specific accents

## Responsive Considerations

- Sidebar collapses to hamburger menu on mobile
- Cards reflow to single column on smaller screens
- Maintains readability on all screen sizes
