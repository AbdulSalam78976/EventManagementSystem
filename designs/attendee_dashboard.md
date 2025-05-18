# Attendee Dashboard

## Purpose
Provide attendees (students) with an overview of upcoming events, their registered events, and quick access to system features.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM                 User ▼ | 🔔 |
+------------------------------------------------------+
| [🏠] [🔍] [📅] [⭐] [👤]                             |
+------------------------------------------------------+
|                                                      |
| Welcome, [User Name]!                                |
|                                                      |
| +--------------------------------------------------+ |
| |                UPCOMING EVENTS                   | |
| +--------------------------------------------------+ |
| |                                                  | |
| | +----------------+  +----------------+           | |
| | | [Event Image]  |  | [Event Image]  |           | |
| | | Tech Conference|  | AI Workshop    |           | |
| | | Mar 15, 2024   |  | Mar 20, 2024   |           | |
| | | Main Hall      |  | Room 101       |           | |
| | | [REGISTER]     |  | [REGISTER]     |           | |
| | +----------------+  +----------------+           | |
| |                                                  | |
| | +----------------+  +----------------+           | |
| | | [Event Image]  |  | [Event Image]  |           | |
| | | Career Fair    |  | Coding Contest |           | |
| | | Apr 01, 2024   |  | Mar 25, 2024   |           | |
| | | Auditorium     |  | Lab 3          |           | |
| | | [REGISTER]     |  | [REGISTER]     |           | |
| | +----------------+  +----------------+           | |
| |                                                  | |
| |                [VIEW ALL EVENTS]                 | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| |              MY REGISTERED EVENTS                | |
| +--------------------------------------------------+ |
| |                                                  | |
| | +----------------+  +----------------+           | |
| | | [Event Image]  |  | [Event Image]  |           | |
| | | AI Workshop    |  | Career Fair    |           | |
| | | Mar 20, 2024   |  | Apr 01, 2024   |           | |
| | | Room 101       |  | Auditorium     |           | |
| | | [VIEW DETAILS] |  | [VIEW DETAILS] |           | |
| | +----------------+  +----------------+           | |
| |                                                  | |
| |              [VIEW ALL MY EVENTS]                | |
| +--------------------------------------------------+ |
|                                                      |
+------------------------------------------------------+
```

## Elements

1. **Header**
   - System name
   - User profile dropdown
   - Notifications icon

2. **Navigation**
   - Home (Dashboard)
   - Search Events
   - Calendar View
   - My Events
   - Profile

3. **Welcome Section**
   - Personalized greeting with user's name

4. **Upcoming Events Section**
   - Grid of event cards showing:
     - Event image/thumbnail
     - Event title
     - Date and time
     - Location
     - Register button
   - "View All Events" button

5. **My Registered Events Section**
   - Grid of event cards showing:
     - Event image/thumbnail
     - Event title
     - Date and time
     - Location
     - View Details button
   - "View All My Events" button

## Behavior

- Event cards are clickable to view event details
- Register buttons open the registration flow
- View Details buttons open the event details with registration status
- User dropdown provides access to profile settings and logout
- Notifications icon shows unread system notifications

## Visual Style

- Clean card-based design with subtle shadows
- Event cards with thumbnail images
- Consistent color scheme with primary blue accents
- Clear visual hierarchy with section headers
- Responsive grid layout

## Responsive Considerations

- Cards reflow to single column on mobile devices
- Navigation converts to bottom bar or hamburger menu on mobile
- Maintains readability on all screen sizes
