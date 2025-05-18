# Event Registration Screen

## Purpose
Allow attendees to register for an event by confirming their details and eligibility.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM                 User ▼ | 🔔 |
+------------------------------------------------------+
| [🏠] [🔍] [📅] [⭐] [👤]                             |
+------------------------------------------------------+
|                                                      |
| < Back to Event Details                              |
|                                                      |
| +--------------------------------------------------+ |
| |               EVENT REGISTRATION                 | |
| +--------------------------------------------------+ |
| |                                                  | |
| | Event: Tech Conference 2024                      | |
| | Date: March 15, 2024                             | |
| | Time: 9:00 AM - 5:00 PM                          | |
| | Venue: Main Hall                                 | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| |             ELIGIBILITY CONFIRMATION             | |
| +--------------------------------------------------+ |
| |                                                  | |
| | ✓ I confirm that I meet the following criteria:  | |
| |   - Open to all students                         | |
| |   - Must have valid student ID                   | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| |              ATTENDEE INFORMATION                | |
| +--------------------------------------------------+ |
| |                                                  | |
| | Name: [John Doe                               ]  | |
| | Email: [john.doe@example.com                  ]  | |
| | Phone: [                                      ]  | |
| | Student ID: [                                 ]  | |
| |                                                  | |
| | Special Requirements (Optional):                 | |
| | [                                              ] | |
| | [                                              ] | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| |                                                  | |
| | By registering, you agree to the event terms and | |
| | conditions and cancellation policy.              | |
| |                                                  | |
| | [        CANCEL        ] [      REGISTER      ]  | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
+------------------------------------------------------+
```

## Elements

1. **Header**
   - System name
   - User profile dropdown
   - Notifications icon
   - Navigation menu

2. **Back Navigation**
   - Back to Event Details link

3. **Event Information Section**
   - Event title
   - Date and time
   - Venue

4. **Eligibility Confirmation Section**
   - Checkbox to confirm eligibility
   - List of eligibility criteria

5. **Attendee Information Section**
   - Pre-filled name and email from user profile
   - Phone number input
   - Student ID input
   - Special requirements textarea (optional)

6. **Terms and Registration Section**
   - Terms and conditions notice
   - Cancel button
   - Register button (primary action)

## Behavior

- Name and email are pre-filled from user profile and non-editable
- All required fields must be completed before registration
- Eligibility checkbox must be checked
- Cancel button returns to event details without registering
- Register button submits the form and shows confirmation
- Validation errors appear below the relevant fields

## Visual Style

- Clean, card-based sections with subtle shadows
- Clear visual hierarchy with section headers
- Consistent color scheme with primary blue accents
- Required fields clearly marked
- Prominent register button

## Responsive Considerations

- Form fields adjust to full width on mobile
- Maintains readability on all screen sizes
- Appropriate input types for mobile (tel for phone, etc.)
