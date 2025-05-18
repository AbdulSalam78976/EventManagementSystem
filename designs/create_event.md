# Create Event Screen

## Purpose
Allow administrators and external organizers to create new events in the system.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM           Organizer ▼ | 🔔  |
+------------------------------------------------------+
|                      |                               |
| DASHBOARD            |  < Back to Dashboard          |
| MY EVENTS            |                               |
| CREATE EVENT         |  +---------------------------+|
| PARTICIPANTS         |  |        CREATE EVENT       ||
| MEDIA UPLOADS        |  +---------------------------+|
|                      |  |                           ||
| LOGOUT               |  | Event Name:               ||
|                      |  | [                       ] ||
|                      |  |                           ||
|                      |  | Category:                 ||
|                      |  | [Select Category     ▼  ] ||
|                      |  |                           ||
|                      |  | Description:              ||
|                      |  | [                       ] ||
|                      |  | [                       ] ||
|                      |  | [                       ] ||
|                      |  |                           ||
|                      |  | Date (YYYY-MM-DD):        ||
|                      |  | [                       ] ||
|                      |  |                           ||
|                      |  | Time:                     ||
|                      |  | Start: [        ] End: [  ]||
|                      |  |                           ||
|                      |  | Venue:                    ||
|                      |  | [                       ] ||
|                      |  |                           ||
|                      |  | Available Slots:          ||
|                      |  | [    50     ] ▲▼          ||
|                      |  |                           ||
|                      |  | Contact Info:             ||
|                      |  | [                       ] ||
|                      |  |                           ||
|                      |  | Eligibility Criteria:     ||
|                      |  | [                       ] ||
|                      |  | [                       ] ||
|                      |  |                           ||
|                      |  | Event Image:              ||
|                      |  | [    CHOOSE FILE    ]     ||
|                      |  | No file chosen            ||
|                      |  |                           ||
|                      |  | Additional Documents:     ||
|                      |  | [    CHOOSE FILES   ]     ||
|                      |  | No files chosen            ||
|                      |  |                           ||
|                      |  | [  CANCEL  ] [ CREATE ]   ||
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
   - Create Event (highlighted)
   - Participants (for organizers) / Users (for admins)
   - Media Uploads (for organizers) / Reports (for admins)
   - System Settings (for admins only)
   - Logout

3. **Back Navigation**
   - Back to Dashboard link

4. **Create Event Form**
   - Event Name input
   - Category dropdown
   - Description textarea
   - Date input (with date picker)
   - Time inputs (start and end)
   - Venue input
   - Available Slots spinner
   - Contact Info input
   - Eligibility Criteria textarea
   - Event Image upload
   - Additional Documents upload
   - Cancel and Create buttons

## Behavior

- All required fields are marked with asterisk
- Date field opens a date picker calendar
- Time fields use time picker or formatted input
- Available Slots has minimum value of 1
- Image upload shows preview after selection
- Documents upload allows multiple files
- Cancel button prompts confirmation if form has changes
- Create button validates all required fields before submission
- For external organizers, submission goes to admin approval queue
- For admins, events are created and published immediately

## Visual Style

- Clean, form-based layout with clear labels
- Sidebar with dark background and light text
- Clear visual hierarchy with section headers
- Consistent color scheme with role-specific accents
- Create button in primary blue color

## Responsive Considerations

- Sidebar collapses to hamburger menu on mobile
- Form fields stack vertically and expand to full width on mobile
- Date and time pickers optimized for touch input
- Maintains readability on all screen sizes
