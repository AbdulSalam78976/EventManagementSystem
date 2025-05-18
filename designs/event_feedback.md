# Event Feedback Screen

## Purpose
Allow attendees to rate events and provide feedback after attending.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM                 User ▼ | 🔔 |
+------------------------------------------------------+
| [🏠] [🔍] [📅] [⭐] [👤]                             |
+------------------------------------------------------+
|                                                      |
| < Back to My Events                                  |
|                                                      |
| +--------------------------------------------------+ |
| |                 EVENT FEEDBACK                   | |
| +--------------------------------------------------+ |
| |                                                  | |
| | Event: Tech Conference 2024                      | |
| | Date: March 15, 2024                             | |
| | Venue: Main Hall                                 | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| |                  RATE THIS EVENT                 | |
| +--------------------------------------------------+ |
| |                                                  | |
| | Overall Experience:                              | |
| | ○ ○ ○ ○ ○                                       | |
| | 1 2 3 4 5                                        | |
| |                                                  | |
| | Content Quality:                                 | |
| | ○ ○ ○ ○ ○                                       | |
| | 1 2 3 4 5                                        | |
| |                                                  | |
| | Organization:                                    | |
| | ○ ○ ○ ○ ○                                       | |
| | 1 2 3 4 5                                        | |
| |                                                  | |
| | Venue & Facilities:                              | |
| | ○ ○ ○ ○ ○                                       | |
| | 1 2 3 4 5                                        | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| |                 YOUR FEEDBACK                    | |
| +--------------------------------------------------+ |
| |                                                  | |
| | What did you like most about this event?         | |
| | [                                              ] | |
| | [                                              ] | |
| |                                                  | |
| | What could be improved?                          | |
| | [                                              ] | |
| | [                                              ] | |
| |                                                  | |
| | Additional Comments (Optional):                  | |
| | [                                              ] | |
| | [                                              ] | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| |                                                  | |
| | [        CANCEL        ] [      SUBMIT       ]   | |
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
   - Back to My Events link

3. **Event Information Section**
   - Event title
   - Date
   - Venue

4. **Rating Section**
   - Overall Experience rating (1-5 stars)
   - Content Quality rating (1-5 stars)
   - Organization rating (1-5 stars)
   - Venue & Facilities rating (1-5 stars)

5. **Feedback Section**
   - "What did you like most" textarea
   - "What could be improved" textarea
   - Additional Comments textarea (optional)

6. **Action Buttons**
   - Cancel button
   - Submit button (primary action)

## Behavior

- Star ratings are interactive and highlight on hover
- At least one rating category is required
- At least one text feedback field is required
- Cancel button returns to My Events without saving
- Submit button validates and sends feedback
- After submission, shows thank you message and returns to My Events

## Visual Style

- Clean, card-based sections with subtle shadows
- Interactive star rating system with color feedback
- Clear visual hierarchy with section headers
- Consistent color scheme with primary blue accents
- Prominent submit button

## Responsive Considerations

- Form fields adjust to full width on mobile
- Star ratings remain usable on touch devices
- Maintains readability on all screen sizes
