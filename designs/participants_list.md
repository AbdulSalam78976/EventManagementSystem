# Participants List Screen

## Purpose
Allow administrators and event organizers to view and manage the list of registered participants for a specific event.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM           Organizer ▼ | 🔔  |
+------------------------------------------------------+
|                      |                               |
| DASHBOARD            |  < Back to My Events          |
| MY EVENTS            |                               |
| CREATE EVENT         |  +---------------------------+|
| PARTICIPANTS         |  |     EVENT PARTICIPANTS    ||
| MEDIA UPLOADS        |  +---------------------------+|
|                      |  |                           ||
| LOGOUT               |  | Event: Tech Conference 2024||
|                      |  | Date: March 15, 2024      ||
|                      |  | Registered: 120/150       ||
|                      |  |                           ||
|                      |  +---------------------------+|
|                      |                               |
|                      |  +---------------------------+|
|                      |  |      MANAGE PARTICIPANTS  ||
|                      |  +---------------------------+|
|                      |  |                           ||
|                      |  | Search: [              ]  ||
|                      |  | Export: [ DOWNLOAD CSV ]  ||
|                      |  |                           ||
|                      |  | [ALL] [CONFIRMED] [PENDING]||
|                      |  |                           ||
|                      |  | +---------------------+   ||
|                      |  | | Name | Email | Status|  ||
|                      |  | +---------------------+   ||
|                      |  | | John  | john@ | Conf.|  ||
|                      |  | | Smith | ex.com| ✓    |  ||
|                      |  | +---------------------+   ||
|                      |  | | Sarah | sarah | Conf.|  ||
|                      |  | | Lee   | ex.com| ✓    |  ||
|                      |  | +---------------------+   ||
|                      |  | | Mike  | mike@ | Pend.|  ||
|                      |  | | Jones | ex.com| ⌛   |  ||
|                      |  | +---------------------+   ||
|                      |  | | Lisa  | lisa@ | Conf.|  ||
|                      |  | | Wang  | ex.com| ✓    |  ||
|                      |  | +---------------------+   ||
|                      |  |                           ||
|                      |  | Showing 1-4 of 120       ||
|                      |  | < 1 2 3 ... 30 >         ||
|                      |  |                           ||
|                      |  +---------------------------+|
|                      |                               |
|                      |  +---------------------------+|
|                      |  |    COMMUNICATION TOOLS    ||
|                      |  +---------------------------+|
|                      |  |                           ||
|                      |  | [  EMAIL ALL PARTICIPANTS ]||
|                      |  |                           ||
|                      |  | [ SEND REMINDER TO PENDING]||
|                      |  |                           ||
|                      |  | [ GENERATE ATTENDANCE LIST]||
|                      |  |                           ||
|                      |  +---------------------------+|
|                      |                               |
+------------------------------------------------------+
```

## Elements

1. **Header**
   - System name
   - User profile dropdown (Organizer or Admin)
   - Notifications icon

2. **Sidebar Navigation**
   - Dashboard
   - My Events (for organizers) / All Events (for admins)
   - Create Event
   - Participants (highlighted)
   - Media Uploads (for organizers) / Reports (for admins)
   - Logout

3. **Back Navigation**
   - Back to My Events link

4. **Event Information Section**
   - Event title
   - Date
   - Registration count and capacity

5. **Participant Management Section**
   - Search box
   - Export to CSV button
   - Filter tabs (All, Confirmed, Pending)
   - Participants table with columns:
     - Name
     - Email
     - Status (Confirmed/Pending)
     - Actions (hidden in this view)
   - Pagination controls

6. **Communication Tools Section**
   - Email All Participants button
   - Send Reminder to Pending button
   - Generate Attendance List button

## Behavior

- Search filters the participant list in real-time
- Export to CSV downloads participant data
- Filter tabs show different subsets of participants
- Clicking on a participant row opens detailed view
- Pagination controls navigate through the list
- Email buttons open composition interface
- Generate Attendance List creates printable PDF

## Visual Style

- Clean, data-table layout with clear headers
- Sidebar with dark background and light text
- Clear visual hierarchy with section headers
- Consistent color scheme with role-specific accents
- Status indicators with color coding (green for confirmed, amber for pending)

## Responsive Considerations

- Sidebar collapses to hamburger menu on mobile
- Table becomes scrollable horizontally on small screens
- Alternatively, table reformats to card view on mobile
- Action buttons stack vertically on small screens
