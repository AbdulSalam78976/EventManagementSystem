# Event Details Screen

## Purpose
Display comprehensive information about a specific event and allow attendees to register.

## Design

```
+------------------------------------------------------+
| EVENT MANAGEMENT SYSTEM                 User ▼ | 🔔 |
+------------------------------------------------------+
| [🏠] [🔍] [📅] [⭐] [👤]                             |
+------------------------------------------------------+
|                                                      |
| < Back to Events                                     |
|                                                      |
| +--------------------------------------------------+ |
| |                                                  | |
| | +----------------+                               | |
| | |                |                               | |
| | |                |                               | |
| | |  Event Image   |  Tech Conference 2024         | |
| | |                |                               | |
| | |                |  📅 March 15, 2024            | |
| | +----------------+  🕒 9:00 AM - 5:00 PM         | |
| |                     📍 Main Hall                 | |
| |                     👥 120/150 Slots Available   | |
| |                                                  | |
| |                     [     REGISTER     ]         | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| | Description                                      | |
| +--------------------------------------------------+ |
| |                                                  | |
| | Join us for the biggest tech conference of the   | |
| | year featuring industry experts and cutting-edge | |
| | technologies. Network with professionals and     | |
| | learn about the latest trends in technology.     | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| | Event Details                                    | |
| +--------------------------------------------------+ |
| |                                                  | |
| | Category: Conference                             | |
| | Organizer: Tech Association                      | |
| | Contact: organizer@example.com                   | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| | Eligibility Criteria                             | |
| +--------------------------------------------------+ |
| |                                                  | |
| | - Open to all students                           | |
| | - Must have valid student ID                     | |
| |                                                  | |
| +--------------------------------------------------+ |
|                                                      |
| +--------------------------------------------------+ |
| | Schedule                                         | |
| +--------------------------------------------------+ |
| |                                                  | |
| | 09:00 AM - 10:00 AM: Registration               | |
| | 10:00 AM - 12:00 PM: Keynote Speeches           | |
| | 12:00 PM - 01:00 PM: Lunch Break                | |
| | 01:00 PM - 03:00 PM: Workshop Sessions          | |
| | 03:00 PM - 04:00 PM: Panel Discussion           | |
| | 04:00 PM - 05:00 PM: Networking                 | |
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
   - Back to Events link

3. **Event Header Section**
   - Event image/banner
   - Event title (large, prominent)
   - Date and time
   - Location
   - Available slots
   - Register button (primary action)

4. **Description Section**
   - Detailed event description

5. **Event Details Section**
   - Category
   - Organizer
   - Contact information

6. **Eligibility Criteria Section**
   - List of requirements for attendance

7. **Schedule Section**
   - Detailed timeline of event activities

## Behavior

- Register button changes to "Unregister" if already registered
- Register button is disabled if event is full or user is ineligible
- Back button returns to previous screen
- If event has passed, registration is disabled with appropriate message

## Visual Style

- Clean, card-based sections with subtle shadows
- Large, high-quality event image
- Clear visual hierarchy with section headers
- Consistent color scheme with primary blue accents
- Important information (date, time, location) with relevant icons

## Responsive Considerations

- Image and content reflow for smaller screens
- Maintains readability on all screen sizes
- Full-width sections on mobile
