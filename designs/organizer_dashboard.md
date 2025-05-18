# Organizer Dashboard Design

## Overview
The Organizer Dashboard serves as the central hub for External Event Organizers to manage their events. It provides functionality to create new events, view event status (pending, approved, rejected), manage registered participants, and upload event media.

## Layout Structure
The dashboard follows the same design pattern as other screens in the system, with:
- Header with system name, user info, and notifications
- Sidebar navigation
- Main content area with tabs

## Color Scheme
- Uses the application's color scheme defined in AppColors
- Primary: Royal Blue (#4169E1)
- Background: Light gray (#F0F0F0)
- Text: Dark gray for primary text, lighter gray for secondary text

## Components

### Header
- System name on the left
- User dropdown and notifications on the right
- Consistent with other screens in the application

### Sidebar
- Profile section with organizer name and role
- Navigation menu with icons:
  - Dashboard (Home)
  - My Events
  - Create Event
  - Participants
  - Settings
  - Logout

### Main Content Area
The main content area is divided into tabs:

#### Dashboard Tab
- Summary statistics:
  - Total events
  - Pending approval
  - Approved events
  - Rejected events
  - Total participants
- Quick actions:
  - Create new event
  - View participants
- Recent events list with status indicators

#### My Events Tab
- Filter options: All, Pending, Approved, Rejected
- Event cards showing:
  - Event name
  - Date and time
  - Venue
  - Status (with color coding)
  - Number of participants
  - Action buttons:
    - View details
    - Edit (if pending)
    - Cancel
    - View participants
    - Upload media

#### Create Event Tab
- Form to create a new event (similar to CreateEventScreenNew)
- Fields:
  - Event name
  - Description
  - Category
  - Date
  - Start time
  - End time
  - Venue
  - Capacity
  - Eligibility criteria
  - Image upload
  - Documents upload

#### Participants Tab
- Filter by event dropdown
- Search functionality
- Table showing:
  - Name
  - Email
  - Phone
  - Student ID
  - Registration date
  - Special requirements
  - Actions (approve/reject individual registrations)
- Export to CSV option

#### Media Upload Tab
- Select event dropdown
- Drag and drop area for images
- Preview of uploaded images
- Description fields for each image
- Submit button to save changes

## Responsive Behavior
- Sidebar collapses to icons on smaller screens
- Cards stack vertically on smaller screens
- Tables become scrollable horizontally

## Status Indicators
- Pending: Yellow/Amber
- Approved: Green
- Rejected: Red

## Interactive Elements
- Hover effects on buttons and cards
- Tooltips for icons
- Confirmation dialogs for important actions
