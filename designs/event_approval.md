# Event Approval Screen

## Purpose
Allow administrators to review and approve or reject events submitted by external organizers.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM                 Admin ▼ | 🔔 |
+------------------------------------------------------+
|                      |                               |
| DASHBOARD            |  < Back to Pending Approvals  |
| PENDING APPROVALS    |                               |
| ALL EVENTS           |  +---------------------------+|
| REGISTERED USERS     |  |       EVENT APPROVAL      ||
| EVENT REPORTS        |  +---------------------------+|
| SYSTEM SETTINGS      |  |                           ||
|                      |  | Event: Tech Conference 2024||
| LOGOUT               |  | Organizer: John Smith     ||
|                      |  | Email: org1@example.com   ||
|                      |  | Submitted: Mar 10, 2024   ||
|                      |  |                           ||
|                      |  +---------------------------+|
|                      |                               |
|                      |  +---------------------------+|
|                      |  |       EVENT DETAILS       ||
|                      |  +---------------------------+|
|                      |  |                           ||
|                      |  | Date: March 15, 2024      ||
|                      |  | Time: 9:00 AM - 5:00 PM   ||
|                      |  | Venue: Main Hall          ||
|                      |  | Category: Conference      ||
|                      |  | Capacity: 150 attendees   ||
|                      |  |                           ||
|                      |  | Description:              ||
|                      |  | Join us for the biggest   ||
|                      |  | tech conference of the    ||
|                      |  | year featuring industry   ||
|                      |  | experts and cutting-edge  ||
|                      |  | technologies. Network with||
|                      |  | professionals and learn   ||
|                      |  | about the latest trends.  ||
|                      |  |                           ||
|                      |  | Eligibility:              ||
|                      |  | - Open to all students    ||
|                      |  | - Must have valid ID      ||
|                      |  |                           ||
|                      |  | [VIEW ATTACHED DOCUMENTS] ||
|                      |  +---------------------------+|
|                      |                               |
|                      |  +---------------------------+|
|                      |  |      ADMIN DECISION       ||
|                      |  +---------------------------+|
|                      |  |                           ||
|                      |  | Comments (optional):      ||
|                      |  | [                       ] ||
|                      |  | [                       ] ||
|                      |  |                           ||
|                      |  | [  REJECT  ] [ APPROVE ]  ||
|                      |  |                           ||
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
   - Dashboard
   - Pending Approvals (highlighted)
   - All Events
   - Registered Users
   - Event Reports
   - System Settings
   - Logout

3. **Back Navigation**
   - Back to Pending Approvals link

4. **Event Header Section**
   - Event title
   - Organizer name
   - Organizer email
   - Submission date

5. **Event Details Section**
   - Date and time
   - Venue
   - Category
   - Capacity
   - Description
   - Eligibility criteria
   - Link to view attached documents

6. **Admin Decision Section**
   - Comments textarea for feedback
   - Reject button
   - Approve button (primary action)

## Behavior

- Back button returns to pending approvals list
- View Attached Documents opens documents in a modal or new tab
- Reject button prompts for rejection reason if comments are empty
- Approve button confirms approval and notifies organizer
- After decision, redirects to pending approvals with success message

## Visual Style

- Clean, card-based sections with subtle shadows
- Sidebar with dark background and light text
- Clear visual hierarchy with section headers
- Consistent color scheme with admin-specific accents
- Approve button in green, Reject button in red

## Responsive Considerations

- Sidebar collapses to hamburger menu on mobile
- Sections stack vertically on smaller screens
- Maintains readability on all screen sizes
